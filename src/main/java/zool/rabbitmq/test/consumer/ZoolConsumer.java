package zool.rabbitmq.test.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * @author : zoolye
 * @date : 2019-01-04 10:41
 * @describe :
 */
public class ZoolConsumer extends DefaultConsumer {

    public ZoolConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.err.println("-------- consumer message --------");
        System.err.println("consumerTag: "+consumerTag);
        System.err.println("envelope: "+envelope);
        System.err.println("properties: "+properties);
        System.err.println("body: "+new String(body));
    }
}
