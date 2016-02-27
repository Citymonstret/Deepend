package com.minecade.deepend.exceptions;

/**
 * Created 2/27/2016 for Deepend
 *
 * @author Citymonstret
 */
public class DeependRuntimeException extends RuntimeException {

    public static final String FORMAT = "<Deepend> %s encountered an exception; %s";

    public DeependRuntimeException(String env, String message) {
        super(String.format(FORMAT, env, message));
    }

    public DeependRuntimeException(String env, String message, Throwable cause) {
        super(String.format(FORMAT, env, message), cause);
    }

}
