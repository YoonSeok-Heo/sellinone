package com.wsb.sellinone.common;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ErrorResponse {

    private int status;
    private String message;
    private Map<String, Object> result;

    public ErrorResponse(){
        this.status = 400;
        this.message = "";
        this.result = new HashMap<String, Object>();
    }

    public ErrorResponse(int status, String message, Map<String, Object> map){
        this.status = status;
        this.message = message;
        this.result = map;
    }

    public ErrorResponse(int status, String message){
        this.status = status;
        this.message = message;
        this.result = new HashMap<String, Object>();
    }

    public void putData(String key, Object obj){
        this.result.put(key, obj);
    }

}
