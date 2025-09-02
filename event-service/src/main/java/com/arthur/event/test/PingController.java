package com.arthur.event.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/__ping")
    String ping() {
        return "OK";
    }
}
