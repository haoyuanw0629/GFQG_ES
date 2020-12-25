package com.qfqg_es.response;

import lombok.Data;

@Data
public class BaseResponse {
    //返回码
    private Integer responseCode;
    //返回描述
    private String responseDesc;
}
