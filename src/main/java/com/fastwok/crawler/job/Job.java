package com.fastwok.crawler.job;

import com.fastwok.crawler.services.isservice.CreateTaskService;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

@Component
@Slf4j
public class Job {
    @Autowired
    CreateTaskService createTaskService;
//    @Scheduled(cron = "${crawler.cron.delay}")
    public void importData() throws MessagingException, UnirestException {
        createTaskService.getData();
    }
}
