package com.github.amangusss.gym_application.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

@Slf4j
@Component
public class MemoryHealthIndicator implements HealthIndicator {

    private static final double MEMORY_THRESHOLD = 0.85;

    @Override
    public Health health() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();

        long maxMemory = heapMemoryUsage.getMax();
        long usedMemory = heapMemoryUsage.getUsed();
        double memoryUsage = (double) usedMemory / maxMemory;

        log.debug("Memory usage: {}%", String.format("%.2f", memoryUsage * 100));

        if (memoryUsage < MEMORY_THRESHOLD) {
            return Health.up()
                    .withDetail("memoryUsage", String.format("%.2f%%", memoryUsage * 100))
                    .withDetail("usedMemory", formatBytes(usedMemory))
                    .withDetail("maxMemory", formatBytes(maxMemory))
                    .withDetail("freeMemory", formatBytes(maxMemory - usedMemory))
                    .withDetail("isActive", "Healthy")
                    .build();
        } else {
            log.warn("Memory usage is critically high: {}%", String.format("%.2f", memoryUsage * 100));
            return Health.down()
                    .withDetail("memoryUsage", String.format("%.2f%%", memoryUsage * 100))
                    .withDetail("usedMemory", formatBytes(usedMemory))
                    .withDetail("maxMemory", formatBytes(maxMemory))
                    .withDetail("freeMemory", formatBytes(maxMemory - usedMemory))
                    .withDetail("isActive", "Critical - Memory usage too high")
                    .build();
        }
    }

    private String formatBytes(long bytes) {
        long mb = bytes / (1024 * 1024);
        return mb + " MB";
    }
}
