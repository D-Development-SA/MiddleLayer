package com.Datys.CapaIntermedia.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class HomeController {
    @GetMapping("/home1")
    @PreAuthorize("hasRole('client_admin')")
    public String home(){
        return "hello user";
    }
    @GetMapping("/home2")
    public String home2(){
        return "hello admin";
    }
}
