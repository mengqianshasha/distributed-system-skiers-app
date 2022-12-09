package redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisClient {
    private static final String REDIS_HOST = "http://52.33.26.133";
    private static final int REDIS_PORT = 6379;
    private final JedisPool jedisPool;

    public RedisClient() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(200);
        poolConfig.setMaxIdle(200);
        poolConfig.setMinIdle(40);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        this.jedisPool = new JedisPool(poolConfig, REDIS_HOST + ":" + REDIS_PORT);
    }

    public String getTotalVerticalForSomeDay(String resortId, String seasonId, String dayId, String skierId) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            // Example "resort_season_day_skier:1_1_1_90213:totalVertical"
            String queryKey = "resort_season_day_skier:" + resortId + "_" + seasonId + "_" + dayId + "_" + skierId + ":totalVertical";
            return jedis.get(queryKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getTotalVertical(String skierId, String resortId, String seasonId) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            if (resortId != null && seasonId != null) {
                // Example "skier_resort_season:90213_1_1:totalVertical"
                return jedis.get("skier_resort_season:" + skierId + "_" + resortId + "_" + seasonId + ":totalVertical");
            }

            if (resortId != null) {
                // Example "skier_resort:90213_1:totalVertical"
                return jedis.get("skier_resort:" + skierId + "_" + resortId + ":totalVertical");
            }
        }

        return null;
    }

    public long getNumOfSkiers(String resortId, String seasonId, String dayId) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            // Example "resort_season_day:1_1_1:skiersSet"
            String queryKey = "resort_season_day:" + resortId + "_" + seasonId + "_" + dayId + ":skiersSet";
            return jedis.scard(queryKey);
        }
    }
}
