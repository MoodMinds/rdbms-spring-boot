package org.moodminds.rdbms.spring.boot.config;

import org.moodminds.rdbms.traverse.ConnectionSource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * The RDBMS {@link ConnectionSource} autoconfiguration bean.
 */
@ConditionalOnBean(DataSource.class)
@ConditionalOnSingleCandidate(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class RdbmsTraverseSourceAutoConfiguration {

    /**
     * Create the {@link ConnectionSource} bean by the given {@link DataSource} bean.
     *
     * @param dataSource the given {@link DataSource} bean
     * @return the {@link ConnectionSource} bean
     */
    @Bean
    @ConditionalOnMissingBean
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
