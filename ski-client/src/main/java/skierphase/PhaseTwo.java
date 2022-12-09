package skierphase;

import client.SkierClient;
import lift.Lift;
import performance.Counter;
import performance.RequestStatistics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class PhaseTwo {
    private final BlockingQueue<Lift> queue;
    private final int numOfThreads;
    private final SkierClient client;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final CountDownLatch countDownLatch;
    private BlockingQueue<RequestStatistics> recordsQueue;

    public PhaseTwo(BlockingQueue<Lift> queue, int numOfThreads, SkierClient client, Counter successCounter, Counter failureCounter, CountDownLatch countDownLatch, BlockingQueue<RequestStatistics> recordsQueue) {
        this.queue = queue;
        this.numOfThreads = numOfThreads;
        this.client = client;
        this.successCounter = successCounter;
        this.failureCounter = failureCounter;
        this.countDownLatch = countDownLatch;
        this.recordsQueue = recordsQueue;
    }

    public void execute() {
        System.out.println("Start phase two");
        for (int i = 0; i < numOfThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        Lift lift = queue.take();
                        if (lift != null && lift.isSignal()) {
                            break;
                        }
                        this.client.postLiftRequest(lift, successCounter, failureCounter, recordsQueue);
                    } catch (Exception e) {
                        e.printStackTrace();
                        failureCounter.increment(Thread.currentThread().getName());
                    }
                }

                countDownLatch.countDown();
            });

            thread.start();
        }
    }
}
