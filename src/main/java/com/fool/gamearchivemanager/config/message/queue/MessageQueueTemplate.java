package com.fool.gamearchivemanager.config.message.queue;

public interface MessageQueueTemplate {

    void send(String key, Object message);
}
