package zool.rabbitmq.config;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import zool.rabbitmq.adapter.MessageDelegate;
import zool.rabbitmq.convert.ImageMessageConverter;
import zool.rabbitmq.convert.PDFMessageConverter;
import zool.rabbitmq.convert.TextMessageConverter;
import zool.rabbitmq.entity.Order;
import zool.rabbitmq.entity.Packaged;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author : zoolye
 * @date : 2019-01-05 10:40
 * @describe :
 */
@Configuration
@ComponentScan({"zool.rabbitmq.*"})
public class RabbitMqConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("localhost:5672");
        connectionFactory.setUsername("zoolye");
        connectionFactory.setPassword("123456");
        connectionFactory.setVirtualHost("/zool");
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     * FanoutExchabge：将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchabge：通过添加属性key-value匹配
     * DirectExchabge：按照routingkey分发到指定队列
     * TopicExchabge：多关键字匹配
     *
     * @return
     */

    @Bean
    public TopicExchange exchange001() {
        return new TopicExchange("topic001", true, false);
    }

    @Bean
    public Queue queue001() {
        return new Queue("queue001", true); //队列持久
    }

    @Bean
    public Binding binding001() {
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }

    @Bean
    public TopicExchange exchange002() {
        return new TopicExchange("topic002", true, false);
    }

    @Bean
    public Queue queue002() {
        return new Queue("queue002", true); //队列持久
    }

    @Bean
    public Binding binding002() {
        return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
    }

    @Bean
    public Queue queue003() {
        return new Queue("queue003", true); //队列持久
    }

    @Bean
    public Binding binding003() {
        return BindingBuilder.bind(queue003()).to(exchange001()).with("mq.*");
    }

    @Bean
    public Queue queue_image() {
        return new Queue("image_queue", true); //队列持久
    }

    @Bean
    public Queue queue_pdf() {
        return new Queue("pdf_queue", true); //队列持久
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue001(), queue002(), queue003(), queue_image(), queue_pdf());
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(5);
        container.setDefaultRequeueRejected(false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setConsumerTagStrategy(queue -> queue + "_" + UUID.randomUUID().toString());
        /*container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            String msg = new String(message.getBody());
            System.err.println("-------- 消费者 --------" + msg);
        });*/

        /**
         * 1 适配器方式，默认是有自己的方法名字的：handleMessage
         *     可以自己制定一个方法的名字：consumeMessage
         *     也可以添加一个转换器：从字节数组转换为String
         */
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        adapter.setMessageConverter(new TextMessageConverter());
//        container.setMessageListener(adapter);


        /**
         * 2 适配器方式：我们的队列名称和方法名称也可以进行一一的匹配
         */
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        Map<String,String> queueOrTagToMethodName = new HashMap<>();
//        queueOrTagToMethodName.put("queue001","method1");
//        queueOrTagToMethodName.put("queue002","method2");
//        adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
//        adapter.setMessageConverter(new TextMessageConverter());
//        container.setMessageListener(adapter);
//        return container;

        /**
         * 1.1 支持json格式的转换器
         */
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        adapter.setMessageConverter(jackson2JsonMessageConverter);
//        container.setMessageListener(adapter);
//        return container;

        /**
         * 1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持Java对象转换
         */
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
//        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
//        adapter.setMessageConverter(jackson2JsonMessageConverter);
//        container.setMessageListener(adapter);
//        return container;

        /**
         * 1.3 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持Java对象多映射转换
         */
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
//
//        Map<String, Class<?>> idClassMapping = new HashMap<>();
//        idClassMapping.put("order", zool.rabbitmq.entity.Order.class);
//        idClassMapping.put("packaged", zool.rabbitmq.entity.Packaged.class);
//
//        javaTypeMapper.setIdClassMapping(idClassMapping);
//        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
//        adapter.setMessageConverter(jackson2JsonMessageConverter);
//        container.setMessageListener(adapter);
//        return container;

        /**
         * 1.4 ext convert
         */
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");

        //全局转换器
        ContentTypeDelegatingMessageConverter converter = new ContentTypeDelegatingMessageConverter();

        TextMessageConverter textConverter = new TextMessageConverter();
        converter.addDelegate("text",textConverter);
        converter.addDelegate("html/text",textConverter);
        converter.addDelegate("xml/text",textConverter);
        converter.addDelegate("text/plain",textConverter);

        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
        converter.addDelegate("json",jsonConverter);
        converter.addDelegate("application/json",jsonConverter);

        ImageMessageConverter imageConverter = new ImageMessageConverter();
        converter.addDelegate("image/png",imageConverter);
        converter.addDelegate("image",imageConverter);

        PDFMessageConverter pdfConverter = new PDFMessageConverter();
        converter.addDelegate("application/pdf",pdfConverter);

        adapter.setMessageConverter(converter);
        container.setMessageListener(adapter);
        return container;

    }


}
