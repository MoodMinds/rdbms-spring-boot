package org.moodminds.rdbms.spring.boot.config;

import org.moodminds.rdbms.config.Setting;
import org.moodminds.rdbms.config.Settings;
import org.moodminds.rdbms.traverse.ConnectionSource;
import org.moodminds.rdbms.traverse.route.Routes;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * The RDBMS {@link Routes} autoconfiguration bean.
 */
@ConditionalOnWebApplication(type = SERVLET)
@ConditionalOnClass(Routes.class)
@ConditionalOnBean({ConnectionSource.class, Setting.class})
@ConditionalOnSingleCandidate(ConnectionSource.class)
@AutoConfigureAfter({RdbmsTraverseSourceAutoConfiguration.class, RdbmsSettingAutoConfiguration.class})
public class RdbmsTraverseRoutesAutoConfiguration {

    /**
     * Create the RDBMS {@link Routes} bean by the given {@link ConnectionSource} and {@link Setting} beans.
     *
     * @param connectionSource the given {@link ConnectionSource} bean
     * @param setting the given {@link Setting} bean
     * @return the RDBMS {@link Routes} bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({DataSource.class, Setting.class})
    public Routes traverseRdbmsRoutes(ConnectionSource connectionSource, Setting<? super Settings> setting) {
        try {
            return new Routes(connectionSource, setting);
        } catch (Exception ex) {
            throw new BeanCreationException("Failed to create Traverse RDBMS Routes instance.", ex);
        }
    }
}
