package org.lrth.fsassistant.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.integration.jmx.config.EnableIntegrationMBeanExport;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.jmx.support.MBeanServerFactoryBean;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Map;

// @ManagedResource//(objectName = "Examples:type=JMX,name=Resource")
@Configuration @ConfigurationProperties(prefix="fs-assistant")
public class AppConfig {
    private Map<String, VolumeConfig> volumes;
    private Map<String, PipeConfig> pipes;

//    @Bean
//    public MBeanServerFactoryBean mbeanServer() {
//        return new MBeanServerFactoryBean();
//    }

    @Bean
    public MBeanServer mbeanServer() throws MalformedObjectNameException {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        new ObjectName("com.baeldung.tutorial:type=basic,name=game");

        return server;
    }


//    @Bean
//    public MBeanExporter createJmxExporter() {
//        MBeanExporter exporter = new AnnotationMBeanExporter();
//        exporter.setAutodetect(true);
//
//        AnnotationJmxAttributeSource jmxSource = new AnnotationJmxAttributeSource();
//        //exporter.setAssembler(new MetadataMBeanInfoAssembler(jmxSource));
//        exporter.setNamingStrategy(new MetadataNamingStrategy(jmxSource));
//
//        return exporter;
//    }

    // @ManagedAttribute
    public Map<String, VolumeConfig> getVolumes() {
        return volumes;
    }

    // @ManagedAttribute
    public Map<String, PipeConfig> getPipes() {
        return pipes;
    }

    // @ManagedAttribute
    public void setPipes(Map<String, PipeConfig> pipes) {
        this.pipes = pipes;
        attachVolumes();
    }

    // @ManagedAttribute
    public void setVolumes(Map<String, VolumeConfig> volumes) {
        this.volumes = volumes;
        attachVolumes();;
    }

    private void attachVolumes() {
        if (pipes != null && volumes != null) {
            pipes.entrySet().forEach(e -> {
                PipeConfig pipeConfig = e.getValue();
                VolumeConfigTaskMeta meta;

                meta = pipeConfig.getSourceVolumeMeta();
                if (meta != null) {
                    meta.setVolumeDef(volumes.get(meta.getVolumeRef()));
                }

                meta = pipeConfig.getTargetVolumeMeta();
                if (meta != null) {
                    meta.setVolumeDef(volumes.get(meta.getVolumeRef()));
                }
            });
        }
    }
}
