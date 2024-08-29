package com.xy.springboot.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class FastResponse implements Serializable {

    private int code;
    private String message;

    public FastResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public FastResponse() { this(0, ""); }

    @Override
    public String toString() {
        return String.format("FastResponse [code=%d, message=%s]", code, message);
    }
}