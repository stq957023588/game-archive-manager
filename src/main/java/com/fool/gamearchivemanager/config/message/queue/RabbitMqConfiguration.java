package com.fool.gamearchivemanager.config.message.queue;

import com.fool.gamearchivemanager.Application;
import com.fool.gamearchivemanager.listener.ArchiveFileSavedMessageListener;
import com.fool.gamearchivemanager.listener.SimpleMessageListener;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fool.gamearchivemanager.entity.constant.MessageQueueConstant.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "message-queue.type", havingValue = "RABBIT")
public class RabbitMqConfiguration {
    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    // region 存档文件保存队列
    @Bean(QUEUE_FILE_SAVED)
    public Queue archiveFileSavedQueue() {
        return new Queue(QUEUE_FILE_SAVED);
    }

    @Bean(EXCHANGE_FILE_SAVED)
    public FanoutExchange archiveFileSavedExchange() {
        return new FanoutExchange(EXCHANGE_FILE_SAVED);
    }

    @Bean
    public Binding archiveFileSavedBinding(@Qualifier(QUEUE_FILE_SAVED) Queue queue, @Qualifier(EXCHANGE_FILE_SAVED) FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

    // endregion

    // region 创建文件删除队列
    // 用于生成唯一的队列名称
    @Bean(QUEUE_TEST)
    public Queue fileDeleteQueue() {

        Map<String, Object> arguments = new HashMap<>();
        // 设置消息在队列中的存活时间，超过存活时间会进入死信队列（如果有配置的的化）
        arguments.put("x-message-ttl", 60 * 1000);
        arguments.put("x-dead-letter-exchange", DLX_EXCHANGE_TEST); // 设置死信交换机
        arguments.put("x-dead-letter-routing-key", DLX_FILE_DELETE_ROUTING_KEY); // 设置死信路由键
        arguments.put("x-expires", 30 * 1000);// 当队列在30秒内没有消费者连接时，销毁队列

        // 使用自定义名称
        String uniqueQueueName = QUEUE_TEST + Application.INSTANCE_ID;
        // 当前连接关闭后，队列中存在的消息进入死信队列，当前队列删除
        return new Queue(uniqueQueueName, true, false, false, arguments);
    }

    @Bean(EXCHANGE_TEST)
    public FanoutExchange fileDeleteExchange() {
        return new FanoutExchange(EXCHANGE_TEST);
    }

    // 将队列与交换机绑定
    @Bean
    public Binding fileDeleteBinding(@Qualifier(QUEUE_TEST) Queue myQueue, @Qualifier(EXCHANGE_TEST) FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(myQueue).to(fanoutExchange);
    }
    // endregion

    // 创建死信交换机
    @Bean(DLX_EXCHANGE_TEST)
    public DirectExchange fileDeleteDlxExchange() {
        return new DirectExchange(DLX_EXCHANGE_TEST);
    }

    // 创建死信队列
    @Bean(DLX_QUEUE_TEST)
    public Queue fileDeleteDlxQueue() {
        return new Queue(DLX_QUEUE_TEST, true);
    }

    @Bean
    public Binding bindingDLX(@Qualifier(DLX_QUEUE_TEST) Queue myQueue, @Qualifier(DLX_EXCHANGE_TEST) DirectExchange exchange) {
        return BindingBuilder.bind(myQueue).to(exchange).with(DLX_FILE_DELETE_ROUTING_KEY);
    }


    // 测试队列
    @Bean
    public SimpleMessageListenerContainer testMessageListener(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setQueueNames(QUEUE_TEST + Application.INSTANCE_ID); // 绑定要监听的队列
        // container.setMessageListener(new FileDeleteMessageListener());
        container.setMessageListener(new SimpleMessageListener());
        return container;
    }


    // 死信测试队列
    @Bean
    public SimpleMessageListenerContainer dlxTestMessageListener(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setQueueNames(DLX_QUEUE_TEST); // 绑定要监听的队列
        container.setMessageListener(new SimpleMessageListener());
        return container;
    }


    // 配置测试队列消费者
    @Bean
    public SimpleMessageListenerContainer fileSavedMessageListener(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setQueueNames(QUEUE_FILE_SAVED); // 绑定要监听的队列
        container.setMessageListener(new ArchiveFileSavedMessageListener());
        return container;
    }


    @Bean
    public RabbitMessageQueueTemplate messageQueueTemplate(RabbitTemplate rabbitTemplate) {
        return new RabbitMessageQueueTemplate(rabbitTemplate);
    }


}
