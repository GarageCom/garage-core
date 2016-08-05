package com.garage.core.config;

import com.garage.core.migrator.CassandraMigrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@PropertySource("classpath:database.properties")
@EnableCassandraRepositories(basePackages = "com.garage.core.repository")
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Autowired
    private Environment environment;

    protected String getKeyspaceName() {
        return environment.getProperty("cassandra.keyspace");
    }

    @Bean
    public CassandraClusterFactoryBean cluster() {
        CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
        cluster.setContactPoints(environment.getProperty("cassandra.contactpoints"));
        cluster.setPort(Integer.parseInt(environment.getProperty("cassandra.port")));
        return cluster;
    }

    @Bean
    public CassandraMappingContext cassandraMapping() throws ClassNotFoundException {
        return new BasicCassandraMappingContext();
    }

    @Bean
    CassandraMigrator migrator() {
        CassandraMigrator migrator = new CassandraMigrator();
        migrator.setContactPoints(environment.getProperty("cassandra.contactpoints"));
        migrator.setPort(Integer.parseInt(environment.getProperty("cassandra.port")));
        migrator.setKeyspace(environment.getProperty("cassandra.keyspace"));
        migrator.setSchemaPath("classpath*:migrations");
        return migrator;
    }
}
