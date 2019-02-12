package zool.rabbitmq.test.returnlistener;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
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

        String exchangeName = "test_return_exchange";
        String routingKey = "return.save";
        String routingKeyError = "abc.save";

        String msg = "Hello RabbitMQ Send Return message!";

        channel.addReturnListener((i, s, s1, s2, basicProperties, bytes) -> {
            System.err.println("-------- HandleReturn --------");
            System.err.println("replyCode: " + i);
            System.err.println("replyText: " + s);
            System.err.println("exchange: " + s1);
            System.err.println("routingKey: " + s2);
            System.err.println("properties: " + basicProperties);
            System.err.println("body: " + new String(bytes));
        });

        //channel.basicPublish(exchangeName,routingKey,true,null,msg.getBytes());
        channel.basicPublish(exchangeName, routingKeyError, true, null, msg.getBytes());
    }

}
