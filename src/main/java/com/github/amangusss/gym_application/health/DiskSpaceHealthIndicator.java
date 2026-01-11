package com.github.amangusss.gym_application.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
@Component
public class DiskSpaceHealthIndicator implements HealthIndicator {

    private static final long DISK_SPACE_THRESHOLD_GB = 1024 * 1024 * 1024;

    @Override
    public Health health() {
        File diskPartition = new File("/");
        long freeSpace = diskPartition.getFreeSpace();
        long totalSpace = diskPartition.getTotalSpace();
        long usedSpace = totalSpace - freeSpace;
        double usagePercentage = (double) usedSpace / totalSpace * 100;

        log.debug("Disk space usage: {}%", String.format("%.2f", usagePercentage));

        if (freeSpace >= DISK_SPACE_THRESHOLD_GB) {
            return Health.up()
                    .withDetail("freeSpace", formatBytes(freeSpace))
                    .withDetail("totalSpace", formatBytes(totalSpace))
                    .withDetail("usedSpace", formatBytes(usedSpace))
                    .withDetail("usagePercentage", String.format("%.2f%%", usagePercentage))
                    .withDetail("threshold", formatBytes(DISK_SPACE_THRESHOLD_GB))
                    .withDetail("isActive", "Healthy")
                    .build();
        } else {
            log.warn("Disk space is critically low: {} free", formatBytes(freeSpace));
            return Health.down()
                    .withDetail("freeSpace", formatBytes(freeSpace))
                    .withDetail("totalSpace", formatBytes(totalSpace))
                    .withDetail("usedSpace", formatBytes(usedSpace))
                    .withDetail("usagePercentage", String.format("%.2f%%", usagePercentage))
                    .withDetail("threshold", formatBytes(DISK_SPACE_THRESHOLD_GB))
                    .withDetail("isActive", "Critical - Low disk space")
                    .build();
        }
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        long kb = bytes / 1024;
        if (kb < 1024) {
            return kb + " KB";
        }
        long mb = kb / 1024;
        if (mb < 1024) {
            return mb + " MB";
        }
        long gb = mb / 1024;
        return gb + " GB";
    }
}

