package com.qfqg_es;

import com.qfqg_es.model.EsFile;
import com.qfqg_es.param.Param;
import com.qfqg_es.service.FileServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class ESInitializer implements CommandLineRunner {

    @Autowired
    private FileServiceImpl service;
    private static final String index = Param.ES_INDEX_NAME;
    private static final Logger logger = LoggerFactory.getLogger(ESInitializer.class);
    //遍历录入文件夹文件-即读即插
    @Override
    public void run(String... args){
        //service.deleteIndex(index);
        if(service.isIndexExist(index)){
            if(service.isAutoCreated(index)){
                service.deleteIndex(index);
                createAndAddFile(index);
            }
            //do nothing
        } else {
            createAndAddFile(index);
        }
    }
    private void createAndAddFile(String index){
        service.createFileIndex(index);
        logger.info("===== 向es中插入本地文件, 请等待 =====");
        String path = Param.ES_FILE_PATH;
//                "/Users/mr.melo/Desktop/GFQG_ES项目/QG测试子文件夹";
        File file = new File(path);
        func(file);
        logger.info("===== 插入完成 =====");
    }
    //读取lua和xml文件
    private void func(File file){
        File[] fs =file.listFiles();
        for(File f:fs) {
            if (f.isDirectory())
                func(f);
            if (f.isFile()) {
                String formatName = f.getName();
                if (formatName.endsWith(".lua") || formatName.endsWith("xml")) {
                    service.add(buildEsFile(f));
                }
            }
        }
    }

    // collect selected info
    private EsFile buildEsFile(File file){
        EsFile esWenjian = new EsFile();
        esWenjian.setFileName(file.getName());
        esWenjian.setFileUrl(file.getAbsolutePath());
        esWenjian.setFileType(file.getName().substring(file.getName().length()-3));
        esWenjian.setFileModifiedDate(new Date(file.lastModified()));
        esWenjian.setFileContent(readFileContent(file));
        esWenjian.setFileStrategyName(file.getName().substring(0,file.getName().length()-4));
        return esWenjian;
    }
    // 读取文件内容
    private String readFileContent(File file){
    String str = "";
    try {
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        if(buffer[0]==-17&&buffer[1]==-69&&buffer[2]==-65){
            str = new String(buffer, StandardCharsets.UTF_8);
        } else {
            str = new String(buffer,"GBK");
        }
        return str;
    } catch (IOException e) {
        System.out.println(e.fillInStackTrace());
        return null;
    }
}
}
