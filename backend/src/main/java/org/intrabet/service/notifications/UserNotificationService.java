package org.intrabet.service.notifications;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.intrabet.bean.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserNotificationService {
    private final Map<Long, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    public void addEmitter(User user, SseEmitter emitter) {
        userEmitters.computeIfAbsent(user.getId(), k -> Collections.synchronizedList(new ArrayList<>())).add(emitter);
        emitter.onCompletion(() -> removeEmitter(user, emitter));
        emitter.onTimeout(() -> removeEmitter(user, emitter));
        emitter.onError((e) -> removeEmitter(user, emitter));
    }

    public void removeEmitter(User user, SseEmitter emitter) {
        List<SseEmitter> emitters = userEmitters.get(user.getId());
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                userEmitters.remove(user.getId());
            }
        }
    }

    public void notifyAccountChange(User user) {
        List<SseEmitter> emitters = userEmitters.get(user.getId());
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("account-update")
                        .data(user));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });
        deadEmitters.forEach(e -> removeEmitter(user, e));
    }
}