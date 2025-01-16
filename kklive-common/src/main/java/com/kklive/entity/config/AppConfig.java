package com.kklive.entity.config;

import com.kklive.utils.StringTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${project.folder:}")
    private String projectFolder;

    @Value("${admin.account:admin}")
    private String adminAccount;

    @Value("${admin.password:admin123456}")
    private String adminPassword;

    @Value("${showFFmegLog:true}")
    private Boolean showFFmpegLog;

    @Value("${es.host.port:127.0.0.1:9200}")
    private String esHostPort;

    @Value("${es.index.video.name:kklive_video}")
    private String esIndexVideoName;

    public String getEsIndexVideoName() {
        return esIndexVideoName;
    }

    public String getEsHostPort() {
        return esHostPort;
    }

    public Boolean getShowFFmpegLog() {
        return showFFmpegLog;
    }

    public String getAdminAccount() {
        return adminAccount;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public String getProjectFolder() {
        if (!StringTools.isEmpty(projectFolder) && !projectFolder.endsWith("/")) {
            projectFolder = projectFolder + "/";
        }
        return projectFolder;
    }


}
