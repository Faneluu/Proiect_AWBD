package com.example.proiect_awbd.util;

import com.example.proiect_awbd.repository.FileSharingLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LinkExpirationScheduler {

    private FileSharingLinkRepository fileSharingLinkRepository;

    @Autowired
    LinkExpirationScheduler(FileSharingLinkRepository fileSharingLinkRepository) {
        this.fileSharingLinkRepository = fileSharingLinkRepository;
    }

    // Runs every hour (adjust cron expression as needed)
    @Scheduled(cron = "0 */5 * * * *")
    public void verifyExpiredLinks() {
        LocalDateTime now = LocalDateTime.now();
        fileSharingLinkRepository.deleteByExpiresAtBefore(now);
        System.out.println("Expired links verified and removed at: " + now);
    }
}
