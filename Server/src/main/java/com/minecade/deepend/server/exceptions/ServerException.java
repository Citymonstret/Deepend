package com.minecade.deepend.server.exceptions;

import com.minecade.deepend.exceptions.DeependRuntimeException;

/**
 * Created 2/27/2016 for Deepend
 *
 * @author Citymonstret
 */
public class ServerException extends DeependRuntimeException {

    public ServerException(String message) {
        super("Server", message);
    }

    public ServerException(String message, Throwable cause) {
        super("Server", message, cause);
    }

}
