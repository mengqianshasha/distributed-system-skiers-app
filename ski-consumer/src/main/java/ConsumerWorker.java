import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerWorker implements Runnable {
    private String queueName;
    private final Channel channel;
    private final ConcurrentHashMap<Integer, List<Lift>> lifts;
    private static final boolean AUTO_ACK = false;
    private final JedisPool jedisPool;

    public ConsumerWorker(String queueName, Channel channel, ConcurrentHashMap<Integer, List<Lift>> lifts, JedisPool jedisPool) {
        this.queueName = queueName;
        this.channel = channel;
        this.lifts = lifts;
        this.jedisPool = jedisPool;
    }

    @Override
    public void run() {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            Lift lift = this.getLift(message);

            try (Jedis jedis = this.jedisPool.getResource()) {
                Transaction t = jedis.multi();

                // For GET request: /resorts/{resortID}/seasons/{seasonID}/day/{dayID}/skiers
                // Schema: resort_season_day:<resortId>_<seasonId>_<dayId>:skiersSet
                t.sadd("resort_season_day:" + lift.getResortId() + "_" + lift.getSeasonId() + "_" + lift.getDayId() + ":skiersSet", Integer.toString(lift.skierId));

                // For GET request: /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
                // Schema: resort_season_day_skier:<resortId>_<seasonId>_<dayId>_<skierId>:totalVertical
                t.incrBy("resort_season_day_skier:" + lift.getResortId() + "_" + lift.getSeasonId() + "_" + lift.getDayId() + "_" + lift.getSkierId() + ":totalVertical", (long) lift.getLiftId() * 10);

                // For GET request: /skiers/{skierID}/vertical?resort=<resortId>&season=<seasonId>
                // Schema: skier_resort_season:<skierId>_<resortId>_<seasonId>:totalVertical
                t.incrBy("skier_resort_season:" + lift.getSkierId() + "_" + lift.getResortId() + "_" + lift.getSeasonId() + ":totalVertical", (long) lift.getLiftId() * 10);

                // For GET request: /skiers/{skierID}/vertical?resort=<resortId>
                // Schema: skier_resort:<skierId>_<resortId>:totalVertical
                t.incrBy("skier_resort:" + lift.getSkierId() + "_" + lift.getResortId() + ":totalVertical", (long) lift.getLiftId() * 10);

                t.exec();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        try {
            this.channel.basicConsume(this.queueName, AUTO_ACK, deliverCallback, consumerTag -> {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Lift getLift(String message) {
        JsonObject liftJson = JsonParser.parseString(message).getAsJsonObject();
        int resortId = Utils.getIntFromJsonElement(liftJson.get("resortId"));
        int seasonId = Utils.getIntFromJsonElement(liftJson.get("seasonId"));
        int dayId = Utils.getIntFromJsonElement(liftJson.get("dayId"));
        int skierId = Utils.getIntFromJsonElement(liftJson.get("skierId"));
        int time = Utils.getIntFromJsonElement(liftJson.get("time"));
        int liftId = Utils.getIntFromJsonElement(liftJson.get("liftId"));

        return new Lift(resortId, seasonId, dayId, skierId, time, liftId);
    }
}
