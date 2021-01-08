
package com.qfqg_es.controller;

import com.qfqg_es.helper.ResponseCode;
import com.qfqg_es.model.EsFile;
import com.qfqg_es.response.FileResponse;
import com.qfqg_es.service.FileServiceImpl;
import com.qfqg_es.service.Top5ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/esgf")
@CrossOrigin(allowedHeaders = "*")
public class EsFileController {

    @Autowired
    private FileServiceImpl service;
    @Autowired
    private Top5ServiceImpl top5Service;

    private static final Logger logger = LoggerFactory.getLogger(EsFileController.class);

    /**
    * 文件搜索
    * @param: 路径参数 keyword:要搜索的关键字
    *                pagenum：要搜索第pagenum页的内容
    * */
    @GetMapping("/search/{keyword}/{pagenum}")
    @ResponseBody
    public FileResponse search(@PathVariable("keyword") String keyword,
                                       @PathVariable("pagenum") int pageNum){
        try{
            //记录调用es服务前的时间
            Date startTime = new Date();
            //调用es服务
            logger.info("========== 调用ES搜索 ==========");
            FileResponse response = service.highLightSearch(keyword,pageNum);
            logger.info("========== 搜索结束 ==========");
            //记录本次搜索的关键字并更新关键字表
            logger.info("========== 更新关键字表 ==========");
            top5Service.updateTopK(keyword);
            logger.info("=========== 关键字更新完毕 ==========");
            //记录es搜索结束的时间
            Date endTime = new Date();
            //封装要返回到前端的response对象
            response.setResponseCode(ResponseCode.SUCCESS);
            response.setCurrent(pageNum);
            response.setSearchTime(endTime.getTime() - startTime.getTime());
            return response;
        } catch (Exception e){
            //如果es服务搜索失败，则返回空的response对象
            FileResponse response = new FileResponse();
            response.setResponseCode(ResponseCode.SERVER_ERROR);
            return response;
        }

    }
    /**
    *通过文件id从缓存或es中查询单个文件
    * 路径参数：id
    * 参数：Spring Model：用于向返回到前端的页面中添加数据
    * */
    @GetMapping("/file/{id}")
    public String fileDetails(@PathVariable("id") String id, Model model){
        EsFile esFile = service.getFileDetails(id);
        //设置返回页面的数据，数据名称为"file"，集体数据为EsFile对象
        model.addAttribute("file",esFile);
        return "file-detail";
    }
}




