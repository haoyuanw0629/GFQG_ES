package com.qfqg_es.param;

import org.springframework.core.io.ClassPathResource;

public class Param {
    //ES hostname
    public static final String ES_HOSTNAME = "localhost";
    //ES port
    public static final int ES_PORT = 9200;
    //ES Scheme
    public static final String ES_SCHEME = "http";
    //ES 索引名称
    public static final String ES_INDEX_NAME = "qgsourcefile1";
    //要插入到es的本地文件路径
    public static final String ES_FILE_PATH = "/root/whyESTest/测试文件/qgtestfile";
    //User MySQL 表名
    public static final String USER_TABLE = "gf_user";
    //其他配置详见yml文件
}
