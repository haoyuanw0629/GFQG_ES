package com.qfqg_es.response;

import lombok.Data;
/**
 * 最基本的回应信息，其他所有回应继承于此
 * */
@Data
public class BaseResponse {
    //返回码
    private Integer responseCode;
    //返回描述
    private String responseDesc;
}
