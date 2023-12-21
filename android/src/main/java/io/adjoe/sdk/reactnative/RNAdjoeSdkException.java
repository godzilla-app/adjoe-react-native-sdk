package io.adjoe.sdk.reactnative;

public class RNAdjoeSdkException extends Exception {
    RNAdjoeSdkException(String message) {
        super(message);
    }

    RNAdjoeSdkException(Exception exception) {
        super(exception);
    }

    RNAdjoeSdkException(String message, Throwable cause) {
        super(message, cause);
    }
}
