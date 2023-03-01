package com.wsb.sellinone.common;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ApiResponse {

    private int status;
    private String message;
    private Map<String, Object> result;

    public ApiResponse(){
        this.status = 200;
        this.message = "";
        this.result = new HashMap<String, Object>();
    }

    public ApiResponse(int status, String message, Map<String, Object> map){
        this.status = status;
        this.message = message;
        this.result = map;
    }

    public ApiResponse(int status, String message){
        this.status = status;
        this.message = message;
        this.result = new HashMap<String, Object>();
    }

    public void putData(String key, Object obj){
        this.result.put(key, obj);
    }

}
