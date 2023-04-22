package me.jics;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.beam.sdk.transforms.SerializableFunction;

import javax.sql.DataSource;

public class DataSourceProvider implements SerializableFunction<Void, DataSource> {

    private static volatile DataSource dataSource;
    private final AuthDatabaseConfig config;

    private DataSourceProvider(AuthDatabaseConfig config) {
        this.config = config;
    }

    public static SerializableFunction<Void, DataSource> of(AuthDatabaseConfig hikariDataSourceConfig) {
        return new DataSourceProvider(hikariDataSourceConfig);
    }

    @Override
    public DataSource apply(Void input) {
        DataSource localDataSource = dataSource;
        if (localDataSource == null) {
            synchronized (DataSourceProvider.class) {
                localDataSource = dataSource;
                if (localDataSource == null) {
                    dataSource = localDataSource = createDataSource();
                }
            }
        }
        return localDataSource;
    }

    private DataSource createDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(config.getUrl());
        hikariDataSource.setUsername(config.getUsername());
        hikariDataSource.setPassword(config.getPassword());
        return hikariDataSource;
    }
}
