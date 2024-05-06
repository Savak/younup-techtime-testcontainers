package com.example.accessingdatamysql;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MainControllerTest {

    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(
            "mysql:latest"
    );

    @Autowired
    UserRepository userRepository;

    @LocalServerPort
    private Integer port;

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        userRepository.deleteAll();
    }

    @Test
    public void addAndGetAllUsersIT() {

        // Add first user
        given()
                .when()
                .param("name", "John")
                .param("email", "john@mail.com")
                .post("/demo/add")
                .then()
                .statusCode(200);

        // Add second user
        given()
                .when()
                .param("name", "Dennis")
                .param("email", "dennis@mail.com")
                .post("/demo/add")
                .then()
                .statusCode(200);

        // Check that 2 users are returned by the API
        given()
                .when()
                .get("/demo/all")
                .then()
                .statusCode(200)
                .body(".", hasSize(2));
    }
}