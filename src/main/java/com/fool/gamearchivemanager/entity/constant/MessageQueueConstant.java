package com.fool.gamearchivemanager.entity.constant;

public interface MessageQueueConstant {

    String QUEUE_FILE_SAVED = "FileSavedQueue";
    String EXCHANGE_FILE_SAVED = "FileSavedExchange";

    String QUEUE_TEST = "QUEUE-TEST";
    String EXCHANGE_TEST = "EXCHANGE-TEST";

    String DLX_QUEUE_TEST = "DLX-QUEUE-TEST";
    String DLX_EXCHANGE_TEST = "DLX-EXCHANGE-TEST";


    String DLX_FILE_DELETE_ROUTING_KEY = "dlx-routing-key";
}
