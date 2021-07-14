package com.example.demo.model;

public class Response {

    private String message;
    private Long code;
    private Long timestamp;

    public Response() {
    }

    public Response(String message, Long code, Long timestamp) {
        this.message = message;
        this.code = code;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
