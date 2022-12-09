package performance;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class Counter {
    private final ConcurrentHashMap<String, Integer> countMap;

    public Counter() {
        this.countMap = new ConcurrentHashMap<>();
    }

    public void increment(String threadName) {
        int count = this.countMap.getOrDefault(threadName, 0);
        this.countMap.put(threadName, count + 1);
    }

    public int getCount(String threadName) {
        return this.countMap.getOrDefault(threadName, 0);
    }

    public int getTotalCount() {
        Collection<Integer> counts = this.countMap.values();
        return counts.stream().mapToInt(Integer::valueOf).sum();
    }
}
