package org.moodminds.rdbms.spring.boot.config;

import org.moodminds.rdbms.traverse.ConnectionSource;
import org.moodminds.rdbms.traverse.route.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;

import java.sql.SQLException;

import static java.util.Optional.ofNullable;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * The RDBMS {@link Routes} Traverse autoconfiguration bean.
 */
@ConditionalOnWebApplication(type = SERVLET)
@ConditionalOnClass(Routes.class)
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties(RdbmsProperties.class)
public class TraverseAutoConfiguration {

    /**
     * The RDBMS properties holder field.
     */
    @Autowired
    protected RdbmsProperties properties;

    /**
     * Create the RDBMS {@link Routes} Traverse bean by the given {@link ConnectionSource} bean.
     *
     * @param connectionSource the given {@link ConnectionSource} bean.
     * @return the RDBMS {@link Routes} Traverse bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(DataSource.class)
    public Routes traverseRdbmsRoutes(ConnectionSource connectionSource) {
        return new Routes(connectionSource, config -> {
            ofNullable(properties.getFetch()).ifPresent(config::fetch);
            ofNullable(properties.getBatch()).ifPresent(config::batch);
        });
    }

    /**
     * Create the {@link ConnectionSource} bean by the given {@link DataSource} bean.
     *
     * @param dataSource the given {@link DataSource} bean
     * @return the {@link ConnectionSource} bean
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(DataSource.class)
    public ConnectionSource connectionSource(DataSource dataSource) {
        return () -> {
            try {
                return DataSourceUtils.getConnection(dataSource);
            } catch (CannotGetJdbcConnectionException ex) {
                Throwable cause =  ex.getCause();
                if (cause instanceof SQLException) throw (SQLException) cause;
                else throw new IllegalStateException(cause.getMessage(), cause);
            }
        };
    }
}
