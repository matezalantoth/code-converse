package com.matezalantoth.codeconverse.components;

import com.matezalantoth.codeconverse.model.notification.NotificationEvent;
import com.matezalantoth.codeconverse.service.MailgunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailgunEventListener {

    private final MailgunService mailgunService;

    public MailgunEventListener(MailgunService mailgunService) {
        this.mailgunService = mailgunService;
    }

    @EventListener
    public void sendEmail(NotificationEvent event) {
        try {
            mailgunService.sendSimpleMessage(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
