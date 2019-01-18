package org.lrth.fsassistant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.integration.jmx.config.EnableIntegrationMBeanExport;

// @EnableIntegrationMBeanExport(server = "mbeanServer", managedComponents = "AppConfig")
// @EnableMBeanExport//(server = "mbeanServer")
@SpringBootApplication
public class FileSystemManagementAssistantApplication {

	@Autowired
	private IntegrationAutoConfiguration c;

	public static void main(String[] args) {
		String[] newArgs = new String[args.length + 1];
		System.arraycopy(args, 0, newArgs, 0, args.length);
		newArgs[newArgs.length - 1] = "--debug";

		SpringApplication app = new SpringApplication(FileSystemManagementAssistantApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.setLogStartupInfo(false);

		app.run(newArgs);

		// SpringApplication.run(FileSystemManagementAssistantApplication.class, args);
	}

}

