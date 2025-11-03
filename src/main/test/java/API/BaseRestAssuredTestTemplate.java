package API;

import dat.config.HibernateConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;
/**
     * Base template for RestAssured integration tests.
     * Provides default base URI, port, and helper methods for authenticated requests.
     */
    public abstract class BaseRestAssuredTestTemplate {

        protected static String jwtToken; // optional if auth is needed

        @BeforeAll
        static void setup() {
            // Configure RestAssured
            RestAssured.baseURI = "http://localhost";
            RestAssured.port = 7071;        // change if needed
            RestAssured.basePath = "/api";  // your global path

            // Initialize database if needed
            HibernateConfig.getEntityManagerFactory();

            // Optionally: auto-login test user to get token
            jwtToken = loginAndGetToken("user", "test123");
        }

        /** Helper method for login; returns JWT token string */
        protected static String loginAndGetToken(String username, String password) {
            return given()
                    .contentType(ContentType.JSON)
                    .body("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}")
                    .when()
                    .post("/auth/login/")            // eller "/auth/login" – vær konsistent med din routing
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getString("token");             // undgår Object -> String-cast
        }


        /** Helper: GET request with optional auth */
        protected ValidatableResponse get(String path, boolean withAuth) {
            var req = given().accept(ContentType.JSON);
            if (withAuth && jwtToken != null) {
                req.header("Authorization", "Bearer " + jwtToken);
            }
            return req.when().get(path).then();
        }

        /** Helper: POST request with JSON body */
        protected ValidatableResponse post(String path, Object body, boolean withAuth) {
            var req = given().contentType(ContentType.JSON).body(body);
            if (withAuth && jwtToken != null) {
                req.header("Authorization", "Bearer " + jwtToken);
            }
            return req.when().post(path).then();
        }

        /** Helper: PUT request */
        protected ValidatableResponse put(String path, Object body, boolean withAuth) {
            var req = given().contentType(ContentType.JSON).body(body);
            if (withAuth && jwtToken != null) {
                req.header("Authorization", "Bearer " + jwtToken);
            }
            return req.when().put(path).then();
        }

        /** Helper: DELETE request */
        protected ValidatableResponse delete(String path, boolean withAuth) {
            var req = given();
            if (withAuth && jwtToken != null) {
                req.header("Authorization", "Bearer " + jwtToken);
            }
            return req.when().delete(path).then();
        }
    }

