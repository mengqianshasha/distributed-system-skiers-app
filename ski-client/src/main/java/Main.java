import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import performance.PerformanceCheck;
import performance.RequestStatistics;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Manager manager = new Manager();
        manager.run();
//        manager.printTestLatency();
    }
}
