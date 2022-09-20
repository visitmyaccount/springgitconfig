package com.ft.publish.controller;

import com.ft.publish.constants.AppConstants;
import com.ft.publish.service.BlueYonderBatchService;
import com.ft.publish.service.FTBatchServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@RestController
@RequestMapping("/scheduler/blueyonder/")
public class BlueYonderSchedulerController {

    @Autowired
    private BlueYonderBatchService batchServices;

    @GetMapping("/publish")
    public String generate(@RequestParam("publish") boolean publish){
        return "done";
    }

    @Scheduled(cron = "${app.config.scheduler.cron_pattern_DoorDash_Products_publish_scheduler}")
    public void doorDashProductsPublish() {
        log.info("DoorDashSchedulerController.doorDashProductsPublish.start");
        batchServices.generateProducts(true);
        log.info("DoorDashSchedulerController.doorDashProductsPublish.end");
    }
}
