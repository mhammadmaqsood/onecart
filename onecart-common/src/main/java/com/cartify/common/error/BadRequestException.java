package com.cartify.common.error;

public class BadRequestException extends BaseException{
    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
