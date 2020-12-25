package com.qfqg_es.helper;

import com.qfqg_es.model.EsFile;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LuaxmlInput {

    public List<EsFile> readLuaXml(){
        String path = "/Users/mr.melo/Desktop/GFQG_ES项目/QG测试子文件夹";
        File file = new File(path);
        List<File> list = new ArrayList();
        list = func(file,list);
        return buildEsList(list);
    }


    //读取lua和xml文件
    private List<File> func(File file,List<File> list){
        File[] fs =file.listFiles();
        for(File f:fs) {
            if (f.isDirectory())
                func(f,list);
            if (f.isFile()) {
                String formatName = f.getName().toLowerCase();
                if (formatName.endsWith(".lua") || formatName.endsWith("xml")) {
                    list.add(f);
                }
            }
        }
        return list;
    }

    // collect selected info
    private List<EsFile> buildEsList(List<File> list) {
        List<EsFile> esList =new ArrayList<>();
        for(File file:list){
            EsFile esWenjian = new EsFile();
            esWenjian.setFileName(file.getName());
            esWenjian.setFileUrl(file.getAbsolutePath());
            esWenjian.setFileType(file.getName().substring(file.getName().length()-3));
            esWenjian.setFileModifiedDate(new Date(file.lastModified()));
           // esWenjian.setFileContent(base64Encoder(file));
            esList.add(esWenjian);
        }
        return esList;
    }
    // 将文件内容转化为base64字符串
//    private String base64Encoder(File file) {
//        try{
//            FileInputStream inputFile = new FileInputStream(file);
//            byte[] buffer = new byte[(int)file.length()];
//            inputFile.read(buffer);
//            inputFile.close();
//            return new BASE64Encoder().encode(buffer);
//        } catch (IOException e){
//            System.out.println(e.fillInStackTrace());
//            return "";
//        }
//    }
}

