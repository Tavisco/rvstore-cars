package io.tavisco.rvstore.cars;

import java.util.Collections;
import java.util.Map;

import org.testcontainers.containers.PostgreSQLContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * DatabaseResource
 */
public class DatabaseResource implements QuarkusTestResourceLifecycleManager {

    private static final PostgreSQLContainer DATABSE = new PostgreSQLContainer<>("postgres:10.5")
        .withDatabaseName("rvstore-cars")
        .withUsername("postgres")
        .withPassword("example")
        .withExposedPorts(5432);

    @Override
    public Map<String, String> start() {
        DATABSE.start();
        return Collections.singletonMap("quarkus.datasource.url", DATABSE.getJdbcUrl());
    }

    @Override
    public void stop() {
        DATABSE.stop();
    }
    
}