package com.qfqg_es.helper;

/**
 * 返回信息码
* */
public class ResponseCode {
    /**
     * 表示返回成功
     */
    public static final int SUCCESS = 200;
    /**
     * 表示API调用时的参数有误
     */
    public static final int PARAMETER_ERROR = 422;

    /**
     * 表示其他服务端错误
     */
    public static final int SERVER_ERROR = 500;

}
