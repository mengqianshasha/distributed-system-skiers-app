package lift;

import java.util.concurrent.BlockingQueue;

public class EventGenerator implements Runnable{
    private final BlockingQueue<Lift> queue;
    private final int count;
    private final LiftFactory liftFactory;
    private final int numOfSignals;

    public EventGenerator(BlockingQueue<Lift> queue, int count, int numOfSignals) {
        this.queue = queue;
        this.count = count;
        this.liftFactory = new LiftFactory();
        this.numOfSignals = numOfSignals;
    }

    @Override
    public void run() {
        for (int i = 0; i < this.count; i++) {
            Lift lift = liftFactory.getLift(3);
            try {
                queue.put(lift);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            for (int i = 0; i < this.numOfSignals; i++) {
                queue.put(new Lift());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
