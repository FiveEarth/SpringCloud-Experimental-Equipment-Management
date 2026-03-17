package edu.graduation.common;

import lombok.Data;

@Data
public class R {
    private Integer code;
    private  String message;
    private  Object object;


    public static R ok(){
        R r = new R();
        r.setCode(200);
        return r;
    }
    //重载
    public static R ok(String msg,Object data){
        R r = new R();
        r.setCode(200);
        r.setMessage(msg);
        r.setObject(data);
        return r;
    }


    public static R error(){
        R r = new R();
        r.setCode(500);
        return r;
    }
    //重载
    public static R error(Integer code, String msg){
        R r = new R();
        r.setCode(500);
        r.setMessage(msg);
        return r;
    }
}

