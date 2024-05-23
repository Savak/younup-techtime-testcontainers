package com.example.accessingdatamysql;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class MainControllerTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(
            "mysql:latest"
    );

    @Autowired
    UserRepository userRepository;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
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