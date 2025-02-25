package com.fool.gamearchivemanager.module.file;

public interface FileTransferProtocolState {


    String OK = "ok";

    String NON_AUTH = "non-auth";

    String AUTH_ERROR = "auth-error";

    String NON_ACTION = "non-action";

    String UNSUPPORTED_ACTION = "unsupported-action";

    String ERROR = "error";

}
