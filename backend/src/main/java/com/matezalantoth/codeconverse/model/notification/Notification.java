package com.matezalantoth.codeconverse.model.notification;

import com.matezalantoth.codeconverse.model.notification.dtos.NotificationDTO;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @Setter
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Setter
    private String title;

    @Setter
    private String content;

    @Setter
    private String link;

    @Setter
    private boolean hasBeenRead;

    @Setter
    private Date sentAt;

    public NotificationDTO dto() {
        return new NotificationDTO(id, user.getId(), title, content, link, hasBeenRead, sentAt);
    }


}
