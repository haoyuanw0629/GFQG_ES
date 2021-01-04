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

    @GetMapping("/")
    public String index(Model model){
        List<Keyword> list = top5Service.top5Search();
        model.addAttribute("list",list);
        return "index";
    }


}
