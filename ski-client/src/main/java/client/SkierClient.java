package client;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import lift.Lift;
import performance.Counter;
import performance.RequestStatistics;
import performance.RequestType;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class SkierClient {
    private final static int RETRY_COUNT = 5;
    private final SkiersApi skiersApi;

    public SkierClient(ApiClient apiClient) {
        skiersApi = new SkiersApi(apiClient);
    }

    public void postLiftRequest(Lift lift, Counter successCounter, Counter failureCounter, BlockingQueue<RequestStatistics> recordsQueue) {
        long start = System.currentTimeMillis();
        Date startTime = new Date();
        ApiResponse<Void> res = this.write(lift, RETRY_COUNT);
        if (res == null || res.getStatusCode() != 201) {
            failureCounter.increment(Thread.currentThread().getName());
            if (res != null) {
                System.out.println(res.getStatusCode());
            }
            System.out.println("Fail");
        } else {
            successCounter.increment(Thread.currentThread().getName());
        }

        long end = System.currentTimeMillis();
        if (res != null) {
            try {
                recordsQueue.put(new RequestStatistics(startTime, RequestType.POST, (int) (end - start), res.getStatusCode()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ApiResponse<Void> write(Lift lift, int retryCount) {
        LiftRide liftRide = new LiftRide();
        liftRide.setLiftID(lift.getLiftId());
        liftRide.setTime(lift.getTime());

        try {
            ApiResponse<Void> res = this.skiersApi.writeNewLiftRideWithHttpInfo(liftRide,
                    lift.getResortId(),
                    String.valueOf(lift.getSeasonId()),
                    String.valueOf(lift.getDayId()),
                    lift.getSkierId());

            retryCount -= 1;
            if (res.getStatusCode() == 201) {
                return res;
            } else {
                if (retryCount > 0) {
                    System.out.println("Retry");
                    return this.write(lift, retryCount);
                } else {
                    return res;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
