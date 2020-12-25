
package com.qfqg_es.controller;

import com.qfqg_es.helper.ResponseCode;
import com.qfqg_es.model.EsFile;
import com.qfqg_es.response.FileResponse;
import com.qfqg_es.service.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/esgf")
@CrossOrigin(allowedHeaders = "*")
public class EsFileController {

    @Autowired
    private FileServiceImpl service;

    @GetMapping("/search/{keyword}/{pagenum}")
    @ResponseBody
    public FileResponse<EsFile> search(@PathVariable("keyword") String keyword,
                                       @PathVariable("pagenum") int pageNum){
        //TODO 完善功能
        FileResponse response = service.highLightSearch(keyword,pageNum);
        response.setResponseCode(ResponseCode.SUCCESS);
        response.setCurrent(pageNum);
        return response;
    }
    //临时注释
}




