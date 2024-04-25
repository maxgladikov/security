package dev.gladikov.resource.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/get")
@Slf4j
public class ResourceController {

    @GetMapping
    public Mono<String> greet(@RequestHeader(name = "user") String user, @RequestHeader(name = "roles") String roles){
        log.info ("user is {}",user);
        log.info ("with roles {}",roles);
        return Mono.just ("Hello");
    }


}
