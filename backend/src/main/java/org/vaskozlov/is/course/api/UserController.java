package org.vaskozlov.is.course.api;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.vaskozlov.is.course.bean.User;
import org.vaskozlov.is.course.service.notifications.UserNotificationService;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user/notifications")
public class UserController {
    private final UserNotificationService userNotificationService;

    public UserController(UserNotificationService userNotificationService) {
        this.userNotificationService = userNotificationService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamUserNotifications(@AuthenticationPrincipal User user) {
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(60));
        userNotificationService.addEmitter(user, emitter);
        return emitter;
    }
}
