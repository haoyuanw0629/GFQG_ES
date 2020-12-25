package com.qfqg_es.service;

import com.qfqg_es.model.EsFile;
import com.qfqg_es.response.FileResponse;

import java.io.IOException;
import java.util.List;

public interface FileService {
    //初始化
    public String addAll();

    //插入数据
    public String add(EsFile esFile);

    //搜索
    public FileResponse highLightSearch(String keyword, Integer pageNum) throws Exception;

    //public void delete();

    // Creat ES Index
    public boolean createFileIndex(String indexName);

}
