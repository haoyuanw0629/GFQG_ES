
package com.qfqg_es.controller;

import com.qfqg_es.helper.CacheManager;
import com.qfqg_es.helper.ResponseCode;
import com.qfqg_es.model.EsFile;
import com.qfqg_es.response.FileResponse;
import com.qfqg_es.service.FileServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/esgf")
@CrossOrigin(allowedHeaders = "*")
public class EsFileController {

    @Autowired
    private FileServiceImpl service;

    private static final Logger logger = LoggerFactory.getLogger(EsFileController.class);

    @GetMapping("/search/{keyword}/{pagenum}")
    @ResponseBody
    public FileResponse search(@PathVariable("keyword") String keyword,
                                       @PathVariable("pagenum") int pageNum){
        //TODO 完善功能
        FileResponse response = service.highLightSearch(keyword,pageNum);

        response.setResponseCode(ResponseCode.SUCCESS);
        response.setCurrent(pageNum);
        return response;
    }

    @GetMapping("/file/{id}")
    public ModelAndView fileDetails(@PathVariable("id") String id){
        EsFile esFile = service.getFileDetails(id);
        Map<String, EsFile> map = new HashMap<>();
        map.put(esFile.getFileName(), esFile);
        return new ModelAndView("strategy", map);
    }
}




