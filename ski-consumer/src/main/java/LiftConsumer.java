import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class LiftConsumer {
    private static final int NUM_OF_THREADS = 200;
    private static final String HOST = "54.187.229.67";
    private static final int PORT = 5672;
    private static final String USERNAME = "user1";
    private static final String PASSWORD = "user123";
    public static final String EXCHANGE_NAME = "lift-exchange";
    private static final String QUEUE_NAME = "lift-queue";
    private static final String BINDING_KEY = "lift";
    private static final String EXCHANGE_TYPE = "direct";
    private static boolean EXCHANGE_DURABLE = true;
    private static boolean QUEUE_DURABLE = true;
    private Connection connection;
    private final ConcurrentHashMap<Integer, List<Lift>> lifts;
    private final ExecutorService pool;
    private final JedisPool jedisPool;
    private static final String REDIS_HOST = "http://52.33.26.133";
    private static final int REDIS_PORT = 6379;

    public LiftConsumer() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        Address[] adds = new Address[]{new Address(HOST, PORT)};
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        pool = Executors.newFixedThreadPool(NUM_OF_THREADS);
        this.connection = factory.newConnection(pool, adds);
        this.lifts = new ConcurrentHashMap<>();

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

    public void run() throws IOException {
        for (int i = 0; i < NUM_OF_THREADS; i++) {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, EXCHANGE_DURABLE);
            channel.queueDeclare(QUEUE_NAME, QUEUE_DURABLE, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, BINDING_KEY);

            this.pool.execute(new ConsumerWorker(QUEUE_NAME, channel, this.lifts, this.jedisPool));
        }
    }
}
