package com.yvolabs.hogwartsartifactsapi.system.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Yvonne N
 * Spring boot will detect this bean and add this result to the actuator health endpoint
 */
@Component
public class UsableMemoryHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        File path = new File("."); // Path used to compute available disk space  - (".") = root
        long diskUsableInBytes = path.getUsableSpace();

         long threshold = 10 * 1024 * 1024; // 10MB
//        long threshold = 1000000L * 1024 * 1024; // super large threshold to test DOWN status

        boolean isHealthy = diskUsableInBytes >= threshold;
        Status status = isHealthy ? Status.UP : Status.DOWN; // Up means there is enough usable memory

        return Health
                .status(status)
                .withDetail("usable memory", diskUsableInBytes)
                .withDetail("threshold", threshold)
                .build();
        // In addition to reporting the status, we can attach additional key-value details using the withDetail(key, value)

    }
}
