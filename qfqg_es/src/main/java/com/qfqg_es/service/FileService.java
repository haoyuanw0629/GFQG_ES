package com.qfqg_es.service;

import com.qfqg_es.model.EsFile;
import com.qfqg_es.response.FileResponse;

import java.io.IOException;
import java.util.List;

public interface FileService {
    /**
     * 将本地所有文件插入Es
     * */
    @Deprecated
    String addAll();

    /**
     * 向Es中插入单个文件
     * @param esFile 对象
     * */
    String add(EsFile esFile);

    /**
     * 高亮分页搜索
     * @param keyword 关键字
     * @param pageNum 第几页
     * */
    FileResponse highLightSearch(String keyword, Integer pageNum) throws Exception;

    //public void delete();

    /**
     * 创建Es索引
     * */
    boolean createFileIndex(String indexName);

}
