package com.yvolabs.hogwartsartifactsapi.system.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;

/**
 * @author Yvonne N
 * This class is for info purposes, to show we can configure info details in application.yml/properties or via class.
 * note: @Component has been disabled
 */
//@Component
public class SystemInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder
                .withDetail("build", System.getProperty("build"))
                .withDetail("buildDate", System.getProperty("build.date"))
                .withDetail("os", System.getProperty("os.name"))
                .withDetail("osVersion", System.getProperty("os.version"))
                .withDetail("osArch", System.getProperty("os.arch"))
                .withDetail("javaVersion", System.getProperty("java.version"))
                .withDetail("javaVendor", System.getProperty("java.vendor"))
                .withDetail("javaHome", System.getProperty("java.home"))
                .withDetail("javaOpts", System.getProperty("java.specification.version"))
                .withDetail("javaVendorOpts", System.getProperty("java.specification.vendor"));
    }
}
