package com.qfqg_es.response;

import lombok.Data;

@Data
public class LoginResponse extends BaseResponse{
    private String accessToken;
    private String userId;
    private String loginName;
}
