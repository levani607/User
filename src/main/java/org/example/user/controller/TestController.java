package org.example.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.example.user.model.request.SomeTestClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka/test")
@ConditionalOnExpression("${app.cfg.kafka.enabled:true}")
public class TestController {

    private final KafkaTemplate<String, SomeTestClass> testTemplate;
    @Value("${myapp.topics.name}")
    private String topic;

    @GetMapping
    @Operation(
            security = @SecurityRequirement(name = "bearer-token"))
    public void sendMessage(@RequestParam String message) {
        testTemplate.send(topic,"bla", new SomeTestClass(message));
    }
}
