package com.matezalantoth.codeconverse.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.matezalantoth.codeconverse.model.notification.NotificationEvent;
import org.springframework.stereotype.Service;

@Service
public class MailgunService {

    private final String apiKey = "ae44a490864fd05f051a4595acb1623b-7113c52e-3a49cb49";
    private final String mailgunDomain = "sandbox2a9a8707cf53435386182d1c1e84e399.mailgun.org";

    public com.mashape.unirest.http.JsonNode sendSimpleMessage(NotificationEvent message) throws UnirestException {
        System.out.println("sending mail");
        HttpResponse<com.mashape.unirest.http.JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + mailgunDomain + "/messages")
                .basicAuth("api", apiKey)
                .queryString("from", "Excited User <USER@sandbox2a9a8707cf53435386182d1c1e84e399.mailgun.org>")
                .queryString("to", message.email())
                .queryString("subject", message.subject())
                .queryString("text", message.text())
                .asJson();
        return request.getBody();
    }
}
