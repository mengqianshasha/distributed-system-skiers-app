package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.IOException;

/**
 * A simple RabbitMQ channel factory based on the APche pooling libraries
 */
public class RMQChannelFactory extends BasePooledObjectFactory<Channel> {

    // Valid RMQ connection
    private final Connection connection;
    // used to count created channels for debugging
    private int count;
    public static final String EXCHANGE_NAME = "lift-exchange";
    private static final String QUEUE_NAME = "lift-queue";
    public static final String BINDING_KEY = "lift";
    private static final String EXCHANGE_TYPE = "direct";
    private static boolean EXCHANGE_DURABLE = true;
    private static boolean QUEUE_DURABLE = true;


    public RMQChannelFactory(Connection connection) {
        this.connection = connection;
        count = 0;
    }

    @Override
    synchronized public Channel create() throws IOException {
        count ++;
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, EXCHANGE_DURABLE);
        channel.queueDeclare(QUEUE_NAME, QUEUE_DURABLE, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, BINDING_KEY);

        // Uncomment the line below to validate the expected number of channels are being created
        // System.out.println("Channel created: " + count);
        return channel;
    }

    @Override
    public PooledObject<Channel> wrap(Channel channel) {
        //System.out.println("Wrapping channel");
        return new DefaultPooledObject<>(channel);
    }

    public int getChannelCount() {
        return count;
    }

    // for all other methods, the no-op implementation
    // in BasePooledObjectFactory will suffice
}