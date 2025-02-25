package com.fool.gamearchivemanager.config.message.queue;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fool.gamearchivemanager.listener.FileDeleteMessageListener;
import com.fool.gamearchivemanager.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class MemoryMessageQueueContainer {

    private final Map<String, SubmissionPublisher<Object>> publisherMap;

    private final ThreadPoolExecutor threadPoolExecutor;

    private final ObjectMapper objectMapper;

    public MemoryMessageQueueContainer(ObjectMapper objectMapper, ThreadPoolExecutor threadPoolExecutor) {
        this.objectMapper = objectMapper;
        this.publisherMap = new HashMap<>();
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public MemoryMessageQueueContainer() {
        this.objectMapper = new ObjectMapper();
        this.publisherMap = new HashMap<>();
        this.threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(100), new NamedThreadFactory("MMQ"));
    }


    public SubmissionPublisher<Object> getPublisher(String key) {
        return publisherMap.get(key);
    }

    public void bind(String key, MessageListener... messageListeners) {
        SubmissionPublisher<Object> publisher = new SubmissionPublisher<>(threadPoolExecutor, 100);

        for (MessageListener messageListener : messageListeners) {
            Flow.Subscriber<Object> subscriber = new Flow.Subscriber<>() {
                private final Logger log = LoggerFactory.getLogger(this.getClass());
                private Flow.Subscription subscription;

                @Override
                public void onSubscribe(Flow.Subscription subscription) {
                    // log.info("File delete listener subscribed!");
                    this.subscription = subscription;
                    subscription.request(5); // 请求5个数据项
                }

                @Override
                public void onNext(Object item) {
                    try {
                        byte[] bytes;
                        if (item instanceof String) {
                            bytes = ((String) item).getBytes(); // 手动处理字符串
                        } else {
                            bytes = objectMapper.writeValueAsBytes(item); // 其他类型用默认序列化
                        }
                        Message message = new Message(bytes);
                        messageListener.onMessage(message);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    subscription.request(1); // 控制背压，减少请求的频率
                }

                @Override
                public void onError(Throwable throwable) {
                    log.error("", throwable);
                }

                @Override
                public void onComplete() {
                    // log.info("Completed");
                }
            };
            publisher.subscribe(subscriber);
            publisherMap.put(key, publisher);
        }


    }

}
