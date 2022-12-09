package performance;

import client.SkierClient;
import com.opencsv.CSVReader;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiResponse;
import lift.Lift;
import lift.LiftFactory;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PerformanceCheck {
    private static final int TOTAL_POST = 10000;
    private final LiftFactory liftFactory;
    private final SkierClient client;
    private final Counter successCounter;
    private final Counter failureCounter;

    public PerformanceCheck(ApiClient apiClient, Counter successCounter, Counter failureCounter) {
        this.liftFactory = new LiftFactory();
        this.client = new SkierClient(apiClient);
        this.successCounter = successCounter;
        this.failureCounter = failureCounter;
    }

    public double getLatency() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < TOTAL_POST; i++) {
            Lift lift = liftFactory.getLift();
            ApiResponse<Void> res = client.write(lift, 5);
            if (res == null || res.getStatusCode() != 201) {
                this.failureCounter.increment(Thread.currentThread().getName());
            } else {
                this.successCounter.increment(Thread.currentThread().getName());
            }
        }

        long end = System.currentTimeMillis();
        long duration = end - start;
        return ((double) duration) / TOTAL_POST;
    }

    public List<Integer> getAllResponseTime(String filepath) {
        List<Integer> responseTimes = new ArrayList<>();
        try {
            FileReader filereader = new FileReader(filepath);
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;

            while ((nextRecord = csvReader.readNext()) != null) {
                try {
                    String firstNum = nextRecord[0];
                    int responseTime = Integer.parseInt(firstNum);
                    responseTimes.add(responseTime);
                } catch (Exception e) {
                    continue;
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(responseTimes);
        return responseTimes;
    }

    public static int getTwoPhasesExpectedThroughput(int requestsNumber1, int requestsNumber2, int throughput1, int throughput2) {
        float ratio1 = ((float) requestsNumber1) / (requestsNumber1 + requestsNumber2);
        float ratio2 = ((float) requestsNumber2) / (requestsNumber1 + requestsNumber2);
        return (int) (ratio1 * throughput1 + ratio2 * throughput2);
    }

    public static int calculateExpectedThroughput(int numOfRequests, double latency) {
        return (int) (numOfRequests / latency);
    }
}
