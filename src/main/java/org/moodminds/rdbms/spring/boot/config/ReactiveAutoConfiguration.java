package org.moodminds.rdbms.spring.boot.config;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.R2dbcException;
import org.moodminds.rdbms.reactive.ConnectionFactorySource;
import org.moodminds.rdbms.reactive.ConnectionSource;
import org.moodminds.rdbms.reactive.route.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.r2dbc.connection.ConnectionFactoryUtils;

import static java.util.Optional.ofNullable;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE;

/**
 * The RDBMS {@link Routes} Reactive autoconfiguration bean.
 */
@ConditionalOnWebApplication(type = REACTIVE)
@ConditionalOnClass({Routes.class, ConnectionFactory.class})
@ConditionalOnSingleCandidate(ConnectionFactory.class)
@EnableConfigurationProperties(RdbmsProperties.class)
public class ReactiveAutoConfiguration {

    /**
     * The RDBMS properties holder field.
     */
    @Autowired
    protected RdbmsProperties properties;

    /**
     * Create the RDBMS {@link Routes} Reactive bean by the given {@link ConnectionSource} bean.
     *
     * @param connectionSource the given {@link ConnectionSource} bean.
     * @return the RDBMS {@link Routes} Reactive bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ConnectionFactory.class)
    public Routes reactiveRdbmsRoutes(ConnectionSource connectionSource) {
        return new Routes(connectionSource, config -> {
            ofNullable(this.properties.getFetch()).ifPresent(config::fetch);
            ofNullable(this.properties.getBatch()).ifPresent(config::batch);
        });
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
