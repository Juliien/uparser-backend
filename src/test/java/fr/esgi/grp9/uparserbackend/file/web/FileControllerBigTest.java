package fr.esgi.grp9.uparserbackend.file.web;

import com.jayway.restassured.http.ContentType;
import fr.esgi.grp9.uparserbackend.AbstractBigTest;
import fr.esgi.grp9.uparserbackend.file.domain.File;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpStatus.*;

public class FileControllerBigTest extends AbstractBigTest {

    private String token;
    private String currentFileId;
    private final File file = File.builder()
            .fileName("nameTest")
            .fileContent("a/path")
            .userId("anId")
            .createDate(LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40))
            .build();

    @Before
    public void init() {
        this.token = tokenProvider();
        this.currentFileId = given()
                .headers(
                        "Authorization",
                        "Bearer " + this.token,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON
                )
                .contentType(JSON)
                .body(toJson(this.file))
                .when()
                .post("/api/v1/files")
                .then()
                .extract()
                .as(File.class)
                .getId();

        this.file.setId(currentFileId);
    }

    @Test
    public void should_create_1_file() {
        String fileId = given()
                .headers(
                        "Authorization",
                        "Bearer " + this.token,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON
                )
                .contentType(JSON)
                .body(toJson(this.file))
                .when()
                .post("/api/v1/files")
                .then()
                .log().all()
                .statusCode(CREATED.value())
                .extract()
                .as(File.class)
                .getId();

        given()
                .headers(
                        "Authorization",
                        "Bearer " + this.token
                )
                .when()
                .delete("/api/v1/files/" + fileId);
    }

    @Test
    public void should_get_list_of_1_file() {
        given()
                .headers(
                        "Authorization",
                        "Bearer " + this.token
                )
                .when()
                .get("/api/v1/files")
                .then()
                .log().all()
                .statusCode(OK.value())
                .body("$", hasSize(1));
    }

    @Test
    public void should_get_1_file_by_id() {
        File fetchedFile = given()
                .headers(
                        "Authorization",
                        "Bearer " + this.token
                )
                .when()
                .get("/api/v1/files/" + this.currentFileId)
                .then()
                .log().all()
                .statusCode(OK.value())
                .extract()
                .as(File.class);
        Assert.assertEquals(this.file, fetchedFile);
    }

    @Test
    public void should_delete_file() {
        String fileId = given()
                .headers(
                        "Authorization",
                        "Bearer " + this.token,
                        "Content-Type",
                        ContentType.JSON,
                        "Accept",
                        ContentType.JSON
                )
                .contentType(JSON)
                .body(toJson(this.file))
                .when()
                .post("/api/v1/files")
                .then()
                .extract()
                .as(File.class)
                .getId();

        given()
                .headers(
                        "Authorization",
                        "Bearer " + this.token
                )
                .when()
                .delete("/api/v1/files/" + fileId)
                .then()
                .log().all()
                .statusCode(NO_CONTENT.value());
    }

    @Test
    public void should_bad_request_when_get_file_by_id_with_wrong_id() {
        String wrongFileId = "impossible";
        given()
                .headers(
                        "Authorization",
                        "Bearer " + this.token
                )
                .when()
                .get("/api/v1/files/" + wrongFileId)
                .then()
                .log().all()
                .statusCode(BAD_REQUEST.value());
    }

    @After
    public void clear() {
        given()
                .headers(
                        "Authorization",
                        "Bearer " + this.token
                )
                .when()
                .delete("/api/v1/files/" + this.currentFileId);
    }
}
