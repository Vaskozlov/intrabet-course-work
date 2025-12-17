package org.intrabet.service.notifications;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.intrabet.bean.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class EventNotificationService {
    private final List<SseEmitter> emitters = Collections.synchronizedList(new ArrayList<>());

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        emitter.onCompletion(() -> removeEmitter(emitter));
        emitter.onTimeout(() -> removeEmitter(emitter));
        emitter.onError((e) -> removeEmitter(emitter));
    }

    public void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);
    }

    public void notify(Event event) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        emitters.forEach(emitter ->
                {
                    try {
                        emitter.send(
                                SseEmitter
                                        .event()
                                        .name("event-update")
                                        .data(event)
                        );
                    } catch (IOException e) {
                        deadEmitters.add(emitter);
                    }
                }
        );

        deadEmitters.forEach(this::removeEmitter);
    }
}
