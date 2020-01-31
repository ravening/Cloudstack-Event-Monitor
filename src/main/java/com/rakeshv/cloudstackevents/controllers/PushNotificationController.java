package com.rakeshv.cloudstackevents.controllers;

import com.rakeshv.cloudstackevents.service.SsePushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@CrossOrigin(origins = "*")
public class PushNotificationController {
    @Autowired
    SsePushNotificationService service;

    final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/notification")
    public SseEmitter doNotify() throws IOException {
        final SseEmitter emitter = new SseEmitter();
        service.addEmitter(emitter);
        service.doNotify();
        emitter.onCompletion(() -> service.removeEmitter(emitter));
        emitter.onTimeout(() -> service.removeEmitter(emitter));
        return emitter;
    }
}
