package com.woyao.librarymanagementsystem.common.result;

public final class ResultCode {

    /**
     * 构造方法私有化，防止外部通过 new ResultCode() 创建对象
     */
    private ResultCode(){
    }

    public static final int SUCCESS = 200;
    public static final int PARAM_ERROR = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUD = 404;
    public static final int BUSINESS_ERROR = 1000;
    public static final int SYSTEM_ERROR = 500;
}
