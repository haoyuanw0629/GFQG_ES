package com.qfqg_es.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@CrossOrigin("*")
public class IndexController {

    @GetMapping("/")
    public ModelAndView index(){

        return new ModelAndView("index");
    }


}
