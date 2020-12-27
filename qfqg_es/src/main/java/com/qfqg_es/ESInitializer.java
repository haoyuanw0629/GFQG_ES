package com.qfqg_es;

import com.qfqg_es.model.EsFile;
import com.qfqg_es.service.FileServiceImpl;
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
    //遍历录入文件夹文件-即读即插
    @Override
    public void run(String... args){
        service.createFileIndex("qgsourcefile1");
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
//    // 读取文件内容
//    private String readFileContent(File file){
//        String str = "";
//        try {
//            FileReader inputFile = new FileReader(file);
//            char[] buffer = new char[(int) file.length()/2+1];
//            inputFile.read(buffer);
//            inputFile.close();
//            str = String.valueOf(buffer);
//            return str;
//        } catch (IOException e) {
//            System.out.println(e.fillInStackTrace());
//            return null;
//        }
//    }
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
