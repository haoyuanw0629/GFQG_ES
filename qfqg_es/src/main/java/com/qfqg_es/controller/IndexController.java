package com.qfqg_es.controller;

import com.qfqg_es.model.Keyword;
import com.qfqg_es.service.Top5ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@CrossOrigin("*")
public class IndexController {

    @Autowired
    private Top5ServiceImpl top5Service;

    /**返回主页
     *参数Model：用于向返回的页面中添加数据
     * */
    @GetMapping("/")
    public String index(Model model){
        //进入主页时调用topK服务获取当前排名最高的关键字
        List<Keyword> list = top5Service.top5Search();
        model.addAttribute("list",list);
        return "index";
    }


}
