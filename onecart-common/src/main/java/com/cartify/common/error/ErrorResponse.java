package com.cartify.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private Instant timestamp = Instant.now();
    private String path;
    private ErrorCode code;
    private String message;
    private String requestId;

    public ErrorResponse() {}

    public ErrorResponse(String path, ErrorCode code, String message, String requestId){
        this.path = path;
        this.code = code;
        this.message = message;
        this.requestId = requestId;
    }

    public Instant getTimestamp() { return timestamp; }
    public String getPath() { return path; }
    public ErrorCode getCode() { return code; }
    public String getMessage() { return message; }
    public String getRequestId() { return requestId; }
}
