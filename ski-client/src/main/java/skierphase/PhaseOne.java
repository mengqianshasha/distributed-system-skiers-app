package skierphase;

import client.SkierClient;
import lift.Lift;
import performance.Counter;
import performance.RequestStatistics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class PhaseOne {
    private final BlockingQueue<Lift> queue;
    private final int numOfThreads;
    private final int numOfRequestsPerThread;
    private final SkierClient client;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final CountDownLatch anyThreadEndedBlock;
    private final CountDownLatch allThreadsEndedBlock;
    private BlockingQueue<RequestStatistics> recordsQueue;

    public PhaseOne(BlockingQueue<Lift> queue, int numOfThreads, int numOfRequestsPerThreads, SkierClient client, Counter successCounter, Counter failureCounter, CountDownLatch anyThreadEndedBlock, CountDownLatch allThreadsEndedBlock, BlockingQueue<RequestStatistics> recordsQueue) {
        this.queue = queue;
        this.numOfThreads = numOfThreads;
        this.numOfRequestsPerThread = numOfRequestsPerThreads;
        this.client = client;
        this.successCounter = successCounter;
        this.failureCounter = failureCounter;
        this.anyThreadEndedBlock = anyThreadEndedBlock;
        this.allThreadsEndedBlock = allThreadsEndedBlock;
        this.recordsQueue = recordsQueue;
    }

    public void execute() {
        System.out.println("Start Phase One");
        for (int i = 0; i < numOfThreads; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < numOfRequestsPerThread; j++) {
                    try {
                        Lift lift = this.queue.take();
                        if (lift != null && lift.isSignal()) {
                            break;
                        }

                        this.client.postLiftRequest(lift, successCounter, failureCounter, recordsQueue);
                    } catch (Exception e) {
                        e.printStackTrace();
                        failureCounter.increment(Thread.currentThread().getName());
                    }
                }

                allThreadsEndedBlock.countDown();
                anyThreadEndedBlock.countDown();
            });

            thread.start();
        }
    }
}
