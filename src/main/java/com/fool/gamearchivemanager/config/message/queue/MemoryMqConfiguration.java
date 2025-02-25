package com.fool.gamearchivemanager.config.message.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fool.gamearchivemanager.entity.constant.MessageQueueConstant;
import com.fool.gamearchivemanager.listener.ArchiveFileSavedMessageListener;
import com.fool.gamearchivemanager.util.NamedThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
@ConditionalOnProperty(name = "message-queue.type", havingValue = "MEMORY")
public class MemoryMqConfiguration {


    @Bean
    @ConditionalOnMissingBean(ThreadPoolExecutor.class)
    public ThreadPoolExecutor executor() {
        return new ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(100), new NamedThreadFactory("MMQ"));
    }

    @Bean
    public MemoryMessageQueueContainer memoryMessageQueueContainer(ObjectMapper objectMapper, ThreadPoolExecutor threadPoolExecutor) {
        MemoryMessageQueueContainer container = new MemoryMessageQueueContainer(objectMapper, threadPoolExecutor);
        container.bind(MessageQueueConstant.EXCHANGE_FILE_SAVED, new ArchiveFileSavedMessageListener());

        return container;
    }


    @Bean
    public MessageQueueTemplate messageQueueTemplate(MemoryMessageQueueContainer container) {
        return new MemoryMessageQueueTemplate(container);
    }

}
