package com.cartify.common.error;

public abstract class BaseException extends RuntimeException {
    private final ErrorCode code;

    protected BaseException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }
    public ErrorCode getCode() { return code; }
}
