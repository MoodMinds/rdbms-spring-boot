package org.moodminds.rdbms.spring.boot.config;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.R2dbcException;
import org.moodminds.rdbms.reactive.ConnectionFactorySource;
import org.moodminds.rdbms.reactive.ConnectionSource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.r2dbc.connection.ConnectionFactoryUtils;

/**
 * The RDBMS {@link ConnectionSource} autoconfiguration bean.
 */
@ConditionalOnBean(ConnectionFactory.class)
@ConditionalOnSingleCandidate(ConnectionFactory.class)
@AutoConfigureAfter(R2dbcAutoConfiguration.class)
public class RdbmsReactiveSourceAutoConfiguration {

    /**
     * Create the {@link ConnectionSource} bean by the given {@link ConnectionFactory} bean.
     *
     * @param connectionFactory the given {@link ConnectionFactory} bean
     * @return the {@link ConnectionSource} bean
     */
    @Bean
    @ConditionalOnMissingBean
    public ConnectionSource connectionSource(ConnectionFactory connectionFactory) {
        return new ConnectionFactorySource(connectionFactory, cf -> ConnectionFactoryUtils.getConnection(cf)
                .onErrorMap(DataAccessResourceFailureException.class, ex -> {
                    Throwable cause =  ex.getCause();
                    return cause instanceof R2dbcException ? cause
                            : new IllegalStateException(cause.getMessage(), cause);
                }));
    }
}
