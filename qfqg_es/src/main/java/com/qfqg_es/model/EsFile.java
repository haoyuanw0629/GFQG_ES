package com.qfqg_es.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.util.Date;

/**
 * @author Haoyuan
 * 2020/11/30
 */
@Data
@Document(indexName = "qgsourcefile1")
public class EsFile {


    @Id
    private String id;
    // 文件名
    @Field(type= FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String fileName;
    // 文件路径
    @Field(type = FieldType.Text)
    private String fileUrl;

    // 文件类型
    @Field(type=FieldType.Text)
    private String fileType;
    // 打分
    //@Field(type=FieldType.Double)
    //private double fileSearchScore;

    // 文件修改日期
    @Field(type=FieldType.Date,format = DateFormat.date_optional_time)
    private Date fileModifiedDate;
    // 文件内容
    @Field (type= FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String fileContent;
    @Field(type = FieldType.Text)
    //文件策略名
    private String fileStrategyName;
    //高亮内容 TODO 不存入es
    private String highLightFields;
}
