package com.fool.gamearchivemanager.config.message.queue;

import java.util.concurrent.SubmissionPublisher;

public class MemoryMessageQueueTemplate implements MessageQueueTemplate {

    private final MemoryMessageQueueContainer container;

    public MemoryMessageQueueTemplate(MemoryMessageQueueContainer container) {
        this.container = container;
    }


    public void send(String key, Object o) {
        SubmissionPublisher<Object> publisher = container.getPublisher(key);
        if (publisher == null) {
            throw new RuntimeException(String.format("No such publisher mapped key(%s)", key));
        }
        publisher.submit(o);
    }

}
