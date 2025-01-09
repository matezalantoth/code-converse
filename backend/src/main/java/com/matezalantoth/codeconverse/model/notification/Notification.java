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
    public UserEntity user;

    @Setter
    public String content;

    @Setter
    public String link;

    @Setter
    public boolean hasBeenRead;

    @Setter
    public Date sentAt;

    public NotificationDTO dto() {
        return new NotificationDTO(id, user.getId(), content, link, hasBeenRead, sentAt);
    }


}
