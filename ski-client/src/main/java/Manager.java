import client.SkierClient;
import io.swagger.client.ApiClient;
import lift.EventGenerator;
import lift.Lift;
import performance.Counter;
import performance.PerformanceCheck;
import performance.RequestStatistics;
import skierphase.PhaseOne;
import skierphase.PhaseTwo;
import utils.Utils;
import writer.CsvWriter;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class Manager {
    private static final String BASE_URL = "http://34.222.46.38:8080/ski-app_war";
//    private static final String BASE_URL = "http://servlet-load-balancer-450539422.us-west-2.elb.amazonaws.com//ski-app_war";

//    private static final String BASE_URL = "http://localhost:8080/ski-app_war";
//    private static final String BASE_URL = "http://localhost:8080/ski_app_war_exploded/";
    private static final int PHASE_ONE_THREAD_COUNT = 32;
    private static final int NUM_OF_REQUESTS_PER_THREAD = 1000;
    private static final int PHASE_TWO_THREAD_COUNT = 200;
    private static final int TOTAL_NUM_OF_REQUESTS = 200000;
    private static final double LATENCY = 21.3371;
    private static final String FILEPATH = "/Users/qianshameng/workspace/business/neu/6650_distributed_system/assignments/assignment1/ski-client-part2-swagger/src/main/java/writer/metrics.csv";
    private final Counter successCounter;
    private final Counter failureCounter;
    private final ApiClient apiClient;
    private final SkierClient skierClient;
    private final PerformanceCheck performanceCheck;
    private final BlockingQueue<Lift> liftQueue;

    public Manager() {
        this.successCounter = new Counter();
        this.failureCounter = new Counter();
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath(BASE_URL);
        this.skierClient = new SkierClient(this.apiClient);
        this.performanceCheck = new PerformanceCheck(this.apiClient, this.successCounter, this.failureCounter);
        this.liftQueue = new LinkedBlockingQueue<>(PHASE_ONE_THREAD_COUNT + PHASE_TWO_THREAD_COUNT + 50);
    }

    public void run() throws InterruptedException {
        CountDownLatch phaseOneAnyThreadEndedBlock = new CountDownLatch(1);
        CountDownLatch phaseOneAllThreadsEndedBlock = new CountDownLatch(PHASE_ONE_THREAD_COUNT);
        CountDownLatch writerBlock = new CountDownLatch(1);
        BlockingQueue<RequestStatistics> recordsQueue = new LinkedBlockingQueue<>();

        // Start writing CSV thread
        long start = System.currentTimeMillis();
        Thread writingThread = new Thread(new CsvWriter(recordsQueue, writerBlock, FILEPATH));
        writingThread.start();

        // Generate events
        Thread eventGeneration = new Thread(new EventGenerator(this.liftQueue, TOTAL_NUM_OF_REQUESTS, PHASE_ONE_THREAD_COUNT + PHASE_TWO_THREAD_COUNT + 50));
        eventGeneration.start();

        // Phase one
        PhaseOne phaseOne = new PhaseOne(this.liftQueue, PHASE_ONE_THREAD_COUNT, NUM_OF_REQUESTS_PER_THREAD, skierClient, successCounter, failureCounter, phaseOneAnyThreadEndedBlock, phaseOneAllThreadsEndedBlock, recordsQueue);
        phaseOne.execute();
        phaseOneAnyThreadEndedBlock.await();

        // Phase Two
        CountDownLatch phaseTwoAllThreadsEndedBlock = new CountDownLatch(PHASE_TWO_THREAD_COUNT);
        PhaseTwo phaseTwo = new PhaseTwo(this.liftQueue, PHASE_TWO_THREAD_COUNT, skierClient, successCounter, failureCounter, phaseTwoAllThreadsEndedBlock, recordsQueue);
        phaseTwo.execute();
        phaseOneAllThreadsEndedBlock.await();
        phaseTwoAllThreadsEndedBlock.await();
        recordsQueue.put(new RequestStatistics());

        // End
        long end = System.currentTimeMillis();
        writerBlock.await();
        this.printMetrics(start, end);
    }


    private void printMetrics(long start, long end) {
        System.out.println("Start calculating metrics");
//        double latency = this.performanceCheck.getLatency();
        double latency = LATENCY;
        double wallTime = ((double) (end - start)) / 1000;
        int throughput = (int) (TOTAL_NUM_OF_REQUESTS / wallTime);
        int expectedThroughput = PerformanceCheck.getTwoPhasesExpectedThroughput(PHASE_ONE_THREAD_COUNT * NUM_OF_REQUESTS_PER_THREAD, TOTAL_NUM_OF_REQUESTS - PHASE_ONE_THREAD_COUNT * NUM_OF_REQUESTS_PER_THREAD, PerformanceCheck.calculateExpectedThroughput(PHASE_ONE_THREAD_COUNT * NUM_OF_REQUESTS_PER_THREAD, latency), PerformanceCheck.calculateExpectedThroughput(TOTAL_NUM_OF_REQUESTS - PHASE_ONE_THREAD_COUNT * NUM_OF_REQUESTS_PER_THREAD, latency));
        List<Integer> responseTimes = this.performanceCheck.getAllResponseTime(FILEPATH);
        long mean = Utils.getMean(responseTimes);
        long median = Utils.getMedian(responseTimes);
        long p99 = Utils.getP99(responseTimes);

        System.out.println("Number of threads in phase one: " + PHASE_ONE_THREAD_COUNT);
        System.out.println("Number of threads in phase two: " + PHASE_TWO_THREAD_COUNT);
        System.out.println("The number of successful requests: " + successCounter.getTotalCount());
        System.out.println("The number of unsuccessful requests: " + failureCounter.getTotalCount());
        System.out.println("Wall time: " + wallTime + " s");
        System.out.println("Throughput: " + throughput + " /s");
        System.out.println("Mean response time: " + mean + " ms");
        System.out.println("Median response time: " + median + " ms");
        System.out.println("P99 response time: " + p99 + " ms");
        System.out.println("Latency: " + latency + " ms");
        System.out.println("Expected throughput: " + expectedThroughput + " /s");
    }

    public void printTestLatency() {
        double latency = this.performanceCheck.getLatency();
        System.out.println("Sending 10000 requests");
        System.out.println("The number of successful requests: " + successCounter.getTotalCount());
        System.out.println("The number of unsuccessful requests: " + failureCounter.getTotalCount());
        System.out.println("Latency: " + latency + " ms");
    }
}
