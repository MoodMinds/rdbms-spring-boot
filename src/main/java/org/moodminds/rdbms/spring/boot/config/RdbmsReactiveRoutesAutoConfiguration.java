package org.moodminds.rdbms.spring.boot.config;

import org.moodminds.rdbms.config.Setting;
import org.moodminds.rdbms.config.Settings;
import org.moodminds.rdbms.reactive.ConnectionSource;
import org.moodminds.rdbms.reactive.route.Routes;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE;

/**
 * The RDBMS {@link Routes} autoconfiguration bean.
 */
@ConditionalOnWebApplication(type = REACTIVE)
@ConditionalOnClass(Routes.class)
@ConditionalOnBean({ConnectionSource.class, Setting.class})
@ConditionalOnSingleCandidate(ConnectionSource.class)
@AutoConfigureAfter({RdbmsReactiveSourceAutoConfiguration.class, RdbmsSettingAutoConfiguration.class})
public class RdbmsReactiveRoutesAutoConfiguration {

    /**
     * Create the RDBMS {@link Routes} bean by the given {@link ConnectionSource} and {@link Setting} beans.
     *
     * @param connectionSource the given {@link ConnectionSource} bean
     * @param setting the given {@link Setting} bean
     * @return the RDBMS {@link Routes} bean
     */
    @Bean
    @ConditionalOnMissingBean
    public Routes reactiveRdbmsRoutes(ConnectionSource connectionSource, Setting<? super Settings> setting) {
        try {
            return new Routes(connectionSource, setting);
        } catch (Exception ex) {
            throw new BeanCreationException("Failed to create Reactive RDBMS Routes instance.", ex);
        }
    }
}
