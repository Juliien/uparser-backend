package fr.esgi.grp9.uparserbackend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import fr.esgi.grp9.uparserbackend.authentication.login.LoginDTO;
import fr.esgi.grp9.uparserbackend.authentication.login.LoginResponseDTO;
import fr.esgi.grp9.uparserbackend.authentication.web.AuthenticationController;
import fr.esgi.grp9.uparserbackend.user.domain.User;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@ActiveProfiles("integration")
@RunWith(SpringRunner.class)
@TestPropertySource(locations="classpath:test.properties")
@SpringBootTest(webEnvironment = DEFINED_PORT, properties = "server.port=8999")
public abstract class AbstractBigTest {

    @Autowired
    AuthenticationController authenticationController;

    private static final Logger LOGGER = getLogger(AbstractBigTest.class);

    private final User user = User.builder()
            .email("test@gmail.com")
            .password("test2658")
            .firstName("Tester")
            .lastName("Tester")
            .build();

    private final LoginDTO loginDTO = LoginDTO.builder()
            .email("test@gmail.com")
            .password("test2658")
            .build();

    @Autowired
    ObjectMapper objectMapper;

    @Value("${server.port}")
    private Integer port;

    @Before
    public void setupRestassured() {
        RestAssured.port = port;
    }

    public <T> String toJson(T entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            LOGGER.error("", e);
            return null;
        }
    }

//    public void registerTestUser() {
//        given()
//                .contentType(JSON)
//                .body(toJson(this.user))
//                .when()
//                .post("/api/v1/auth/register");
//    }

    public String tokenProvider() {
        ResponseEntity<LoginResponseDTO> responseEntity = this.authenticationController.login(this.loginDTO);
        return responseEntity.getBody().getToken();
    }
}

