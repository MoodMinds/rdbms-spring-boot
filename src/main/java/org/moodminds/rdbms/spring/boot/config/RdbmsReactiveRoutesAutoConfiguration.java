package org.moodminds.rdbms.spring.boot.config;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.R2dbcException;
import org.moodminds.rdbms.config.Setting;
import org.moodminds.rdbms.config.Settings;
import org.moodminds.rdbms.reactive.ConnectionFactorySource;
import org.moodminds.rdbms.reactive.ConnectionSource;
import org.moodminds.rdbms.reactive.route.Routes;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.r2dbc.connection.ConnectionFactoryUtils;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE;

/**
 * The RDBMS {@link Routes} autoconfiguration bean.
 */
@ConditionalOnWebApplication(type = REACTIVE)
@ConditionalOnClass({Routes.class, ConnectionFactory.class})
@ConditionalOnSingleCandidate(ConnectionFactory.class)
@EnableConfigurationProperties(RdbmsProperties.class)
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
    @ConditionalOnBean({ConnectionFactory.class, Setting.class})
    public Routes reactiveRdbmsRoutes(ConnectionSource connectionSource, Setting<? super Settings> setting) {
        try {
            return new Routes(connectionSource, setting);
        } catch (Exception ex) {
            throw new BeanCreationException("Failed to create Reactive RDBMS Routes instance.", ex);
        }
    }

    /**
     * Create the {@link ConnectionSource} bean by the given {@link ConnectionFactory} bean.
     *
     * @param connectionFactory the given {@link ConnectionFactory} bean
     * @return the {@link ConnectionSource} bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ConnectionFactory.class)
    public ConnectionSource connectionSource(ConnectionFactory connectionFactory) {
        return new ConnectionFactorySource(connectionFactory, cf -> ConnectionFactoryUtils.getConnection(cf)
                .onErrorMap(DataAccessResourceFailureException.class, ex -> {
                    Throwable cause =  ex.getCause();
                    return cause instanceof R2dbcException ? cause
                            : new IllegalStateException(cause.getMessage(), cause);
                }));
    }
}
