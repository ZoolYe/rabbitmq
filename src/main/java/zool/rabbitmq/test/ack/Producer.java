package zool.rabbitmq.test.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.javafx.collections.MappingChange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author : zoolye
 * @date : 2019-01-03 17:21
 * @describe :
 */
public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {
        //1 创建ConnectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/zool");

        //2 获取Connection
        Connection connection = connectionFactory.newConnection();

        //3 通过Connection创建一个新的Channel
        Channel channel = connection.createChannel();

        String exchangeName = "test_ack_exchange";
        String routingKey = "ack.save";

        String msg = "Hello RabbitMQ Send Ack message!";

        for (int i = 0; i < 5; i++) {

            Map<String,Object> headers = new HashMap<>();
            headers.put("num",i);

            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2)
                    .contentEncoding("UTF-8")
                    .headers(headers).build();

            channel.basicPublish(exchangeName, routingKey, true, properties, (msg + " ->> " + i).getBytes());
        }
    }
}
