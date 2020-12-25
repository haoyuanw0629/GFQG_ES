package com.qfqg_es.response;

import lombok.Data;

@Data
public class LoginResponse extends BaseResponse{
    private String accessToken;
    private Integer userId;
    private String loginName;
}
