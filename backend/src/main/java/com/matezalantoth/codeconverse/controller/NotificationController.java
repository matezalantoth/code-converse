package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PatchMapping
    public ResponseEntity<Void> updateRead(@RequestParam UUID notificationId, @RequestBody boolean read) {
        notificationService.updateRead(notificationId, read);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/markAll")
    public ResponseEntity<Void> markAllNotificationsAsRead() {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        notificationService.markAllNotificationsAsRead(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

}
