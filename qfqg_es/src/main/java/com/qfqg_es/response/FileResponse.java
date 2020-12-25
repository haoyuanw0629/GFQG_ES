package com.qfqg_es.response;

import lombok.Data;

import java.util.List;

@Data
public class FileResponse<T> extends BaseResponse {
    private List<T> data;
    private long hits;
    private int current;
}
