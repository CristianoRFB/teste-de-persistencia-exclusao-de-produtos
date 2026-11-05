package br.com.devops.devops.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/devops")
public class DevOpsController {

    @GetMapping
    public String index(Model model) {
        return "home";
    }
}
