package com.cartify.common.error;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}
