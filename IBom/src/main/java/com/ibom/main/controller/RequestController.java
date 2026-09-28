package com.ibom.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/request")
public class RequestController {

    @GetMapping
    public String request() {
        return "request/request";
    }

    @GetMapping("/requestdetail")
    public String requestDetail() {
        return "request/requestdetail";
    }
}

