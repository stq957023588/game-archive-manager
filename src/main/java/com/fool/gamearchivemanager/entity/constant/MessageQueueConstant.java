package com.fool.gamearchivemanager.entity.constant;

public interface MessageQueueConstant {

    String QUEUE_FILE_SAVED = "FileSavedQueue";
    String EXCHANGE_FILE_SAVED = "FileSavedExchange";

    String QUEUE_FILE_DELETE = "FileDeleteQueue";
    String EXCHANGE_FILE_DELETE = "FileDeleteExchange";

    String DLX_QUEUE_FILE_DELETE = "DLX-FileDeleteQueue";
    String DLX_EXCHANGE_FILE_DELETE = "DLX-FileDeleteExchange";


    String DLX_FILE_DELETE_ROUTING_KEY = "dlx-routing-key";
}
