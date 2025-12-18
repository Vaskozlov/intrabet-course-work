package org.intrabet.api;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.intrabet.bean.User;
import org.intrabet.service.notifications.UserPublisher;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user/notifications")
public class UserController {
    private final UserPublisher userPublisher;

    public UserController(UserPublisher userPublisher) {
        this.userPublisher = userPublisher;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamUserNotifications(@AuthenticationPrincipal User user) {
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(60));
        userPublisher.addEmitter(user, emitter);
        return emitter;
    }

    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal User user) {
        return user;
    }
}
