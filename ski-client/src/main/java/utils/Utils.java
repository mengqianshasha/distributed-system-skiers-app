package utils;

import java.util.List;

public class Utils {
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static int getMean(List<Integer> nums) {
        return nums.stream().mapToInt(Integer::intValue).sum() / nums.size();
    }

    public static int getMedian(List<Integer> nums) {
        if (nums.size() % 2 == 0) {
            int mid1 = nums.get(nums.size() / 2);
            int mid2 = nums.get((nums.size() - 1) / 2);
            return (mid1 + mid2) / 2;
        }

        return nums.get(nums.size() / 2);
    }

    public static int getP99(List<Integer> nums) {
        int index = (int) Math.ceil(0.99 * nums.size());
        return nums.get(index - 1);
    }

}
