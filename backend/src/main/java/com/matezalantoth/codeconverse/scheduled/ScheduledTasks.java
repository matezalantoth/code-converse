package com.matezalantoth.codeconverse.scheduled;

import com.matezalantoth.codeconverse.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final QuestionService questionService;

    public ScheduledTasks(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Scheduled(fixedRate = 150000)
    public void checkAndHandleExpiredBounties() {
        questionService.checkAndHandleExpiredBounties();
    }
}
