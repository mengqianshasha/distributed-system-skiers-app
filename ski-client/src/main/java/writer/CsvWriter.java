package writer;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import performance.RequestStatistics;

import java.io.FileWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class CsvWriter implements Runnable {
    private final BlockingQueue<RequestStatistics> queue;
    private final CountDownLatch countDownLatch;
    private final String filepath;

    public CsvWriter(BlockingQueue<RequestStatistics> queue, CountDownLatch countDownLatch, String filepath) {
        this.queue = queue;
        this.countDownLatch = countDownLatch;
        this.filepath = filepath;
    }

    @Override
    public void run() {
        try(Writer writer = new FileWriter(this.filepath)) {
            StatefulBeanToCsv<RequestStatistics> statefulBeanToCsv = new StatefulBeanToCsvBuilder<RequestStatistics>(writer).build();
            while (true) {
                RequestStatistics requestStatistics = queue.take();
                if (requestStatistics != null && requestStatistics.isSignal()) {
                    break;
                } else {
                    statefulBeanToCsv.write(requestStatistics);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}
