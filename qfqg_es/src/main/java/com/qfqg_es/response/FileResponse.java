package com.qfqg_es.response;

import com.qfqg_es.model.EsFile;
import lombok.Data;

import java.util.List;

@Data
public class FileResponse extends BaseResponse {
    private List<EsFile> data;
    private EsFile singleData;
    private long hits;
    private float searchTime;
    private int current;
}
