package org.moodminds.rdbms.spring.boot.config;

import org.moodminds.rdbms.config.Setting;
import org.moodminds.rdbms.config.Settings;
import org.moodminds.rdbms.reactive.route.Routes;
import org.moodminds.rdbms.render.Renderer;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.LinkedList;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.springframework.util.ClassUtils.forName;
import static org.moodminds.sneaky.Cast.cast;

/**
 * The RDBMS {@link Setting} autoconfiguration bean.
 */
@EnableConfigurationProperties(RdbmsProperties.class)
public class RdbmsSettingAutoConfiguration {

    /**
     * The RDBMS properties holder field.
     */
    private final RdbmsProperties properties;

    public RdbmsSettingAutoConfiguration(RdbmsProperties properties) {
        this.properties = properties;
    }

    /**
     * Create the RDBMS {@link Setting} bean.
     *
     * @return the RDBMS {@link Routes} Reactive bean
     */
    @Bean
    @ConditionalOnMissingBean
    public Setting<Settings> rdbmsSetting() {
        try {
            ClassLoader classLoader = RdbmsSettingAutoConfiguration.class.getClassLoader();
            List<Class<? extends Renderer<?, ?>>> renders = new LinkedList<>();
            if (properties.getRender() != null)
                for (String name : properties.getRender())
                    renders.add(cast(forName(name, classLoader)));
            return config -> {
                ofNullable(properties.getFetch()).ifPresent(config::fetch);
                ofNullable(properties.getBatch()).ifPresent(config::batch);
                renders.forEach(config::render);
            };
        } catch (Exception ex) {
            throw new BeanCreationException("Failed to create RDBMS Setting instance.", ex);
        }
    }
}
