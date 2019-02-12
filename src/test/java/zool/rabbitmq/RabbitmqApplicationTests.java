package zool.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import zool.rabbitmq.entity.Order;
import zool.rabbitmq.entity.Packaged;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testAdmin() {

        rabbitAdmin.declareExchange(new DirectExchange("test.direct", false, false));
        rabbitAdmin.declareExchange(new TopicExchange("test.topic", false, false));
        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout", false, false));

        rabbitAdmin.declareQueue(new Queue("test.direct.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.topic.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.fanout.queue", false));

        rabbitAdmin.declareBinding(new Binding("test.direct.queue",
                Binding.DestinationType.QUEUE,
                "test.direct", "direct", new HashMap<>()));


        rabbitAdmin.declareBinding(BindingBuilder.bind(new Queue("test.topic.queue", false))
                .to(new TopicExchange("test.topic", false, false))
                .with("user.#"));

        rabbitAdmin.declareBinding(BindingBuilder.bind(new Queue("test.fanout.queue", false))
                .to(new FanoutExchange("test.fanout", false, false)));

        //清空指定队列的数据
        rabbitAdmin.purgeQueue("test.topic.queue", false);

    }

    @Test
    public void testSendMessage1() {
        //创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc", "信息描述");
        messageProperties.getHeaders().put("type", "自定义消息类型");
        Message message = new Message("Hello RabbitMQ".getBytes(), messageProperties);

        rabbitTemplate.convertAndSend("topic001", "spring.*", message, message1 -> {
            message1.getMessageProperties().getHeaders().put("desc", "额外修改的信息描述");
            message1.getMessageProperties().getHeaders().put("attr", "额外新加的属性");
            System.err.println("-------- 添加额外设置 --------");
            return message1;
        });
    }

    @Test
    public void testSendMessage2() {
        //创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("RabbitMQ Message".getBytes(), messageProperties);

        rabbitTemplate.send("topic001", "spring.abc", message);

        rabbitTemplate.convertAndSend("topic001", "spring.amqp", "Hello Object Send Message One !");
        rabbitTemplate.convertAndSend("topic002", "rabbit.abc", "Hello Object Send Message Two !");
    }

    @Test
    public void testSendMessage4Text() {
        //创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");
        Message message = new Message("RabbitMQ Message".getBytes(), messageProperties);
        rabbitTemplate.send("topic001", "spring.abc", message);
        rabbitTemplate.send("topic002", "rabbit.abc", message);
    }

    @Test
    public void testSendJsonMessage() throws JsonProcessingException {
        Order order = Order.builder()
                .id("001")
                .name("订单消息")
                .content("描述信息").build();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("Order 4 Json: ".concat(json));

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.send("topic001", "spring.order", message);
    }

    @Test
    public void testSendJavaMessage() throws JsonProcessingException {
        Order order = Order.builder()
                .id("001")
                .name("订单消息")
                .content("描述信息").build();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
        System.err.println("Order 4 Json: ".concat(json));

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");

        messageProperties.getHeaders().put("__TypeId__", "zool.rabbitmq.entity.Order");
        Message message = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.send("topic001", "spring.order", message);
    }

    @Test
    public void testSendMappingMessage() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Order order = Order.builder()
                .id("001")
                .name("订单消息")
                .content("订单描述信息").build();

        String json1 = mapper.writeValueAsString(order);
        System.err.println("Order 4 Josn: ".concat(json1));

        MessageProperties messageProperties1 = new MessageProperties();
        messageProperties1.setContentType("application/json");

        messageProperties1.getHeaders().put("__TypeId__", "Order");
        Message message1 = new Message(json1.getBytes(), messageProperties1);
        rabbitTemplate.send("topic001", "spring.order", message1);


        Packaged packaged = Packaged.builder()
                .id("002")
                .name("包裹消息")
                .description("包裹描述信息").build();

        String json2 = mapper.writeValueAsString(packaged);
        System.err.println("Pack 4 Json: ".concat(json2));

        MessageProperties messageProperties2 = new MessageProperties();
        messageProperties2.setContentType("application/json");

        messageProperties2.getHeaders().put("__TypeId__", "Packaged");
        Message message2 = new Message(json1.getBytes(), messageProperties2);
        rabbitTemplate.send("topic001", "spring.pack", message2);
    }

    @Test
    public void testSendExtConverterMessage() throws IOException {

//        byte[] body = Files.readAllBytes(Paths.get("E:/", "6244.png"));
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setContentType("image/png");
//        messageProperties.getHeaders().put("extName", "png");
//        Message message = new Message(body, messageProperties);
//        rabbitTemplate.send("", "image_queue", message);

        byte[] body = Files.readAllBytes(Paths.get("D:/用户目录/Downloads","重构_改善既有代码的设计[高清版].pdf"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/pdf");
        Message message = new Message(body,messageProperties);
        rabbitTemplate.send("","pdf_queue",message);
    }

}

