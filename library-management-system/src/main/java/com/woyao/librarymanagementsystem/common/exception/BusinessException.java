package com.woyao.librarymanagementsystem.common.exception;


import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    private final Integer code;

    /**
     * 默认给一个1000的业务错误码
     */
    public BusinessException(String message){
        super(message);
        this.code = 1000;
    }

    public BusinessException(Integer code,String message){
        super(message);
        this.code = code;
    }

}
