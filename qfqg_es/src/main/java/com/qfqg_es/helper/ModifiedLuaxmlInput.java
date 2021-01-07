package com.qfqg_es.helper;

import com.qfqg_es.model.EsFile;
import com.qfqg_es.service.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//未使用的类
@Deprecated
public class ModifiedLuaxmlInput {

    @Autowired
    private FileServiceImpl service;

    public void modifiedReadLuaXml(){
        String path = "/Users/mr.melo/Desktop/GFQG_ES项目/QG测试子文件夹";
        File file = new File(path);
        func(file);
    }


    //读取lua和xml文件
    private void func(File file){
        File[] fs =file.listFiles();
        for(File f:fs) {
            if (f.isDirectory())
                func(f);
            if (f.isFile()) {
                String formatName = f.getName().toLowerCase();
                if (formatName.endsWith(".lua") || formatName.endsWith("xml")) {
                    service.add(buildEsFile(f));
                }
            }
        }

    }

    // collect selected info
    private EsFile buildEsFile(File file) {
            EsFile esWenjian = new EsFile();
            esWenjian.setFileName(file.getName());
            esWenjian.setFileUrl(file.getAbsolutePath());
            esWenjian.setFileType(file.getName().substring(file.getName().length()-3));
            esWenjian.setFileModifiedDate(new Date(file.lastModified()));
            esWenjian.setFileContent(base64Encoder(file));
        return esWenjian;
    }
    // 将文件内容转化为base64字符串
    private String base64Encoder(File file) {
        try{
            FileInputStream inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int)file.length()];
            inputFile.read(buffer);
            inputFile.close();
            return new BASE64Encoder().encode(buffer);
        } catch (IOException e){
            System.out.println(e.fillInStackTrace());
            return "";
        }
    }
}
