package org.example.crudapi;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractIntegrationTest {

    // Możemy wykorzystać gotową klasę PostgreSQLContainer
    // lub naszą niestandardową konfigurację.
    public static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16.2")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    @BeforeAll
    public static void setUp() {
        // Uruchomienie kontenera
        postgreSQLContainer.start();

        // Przepisanie właściwości, by nasza aplikacja wiedziała,
        // z jakimi danymi do bazy się łączyć w trakcie testów
        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
    }
}