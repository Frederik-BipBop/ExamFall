package API;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.config.Populator;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("integration")
class TripApiTest {

    private Javalin app;
    private EntityManagerFactory emf;
    private String jwt;

    @BeforeAll
    void start() {
        // DB + server
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        app = ApplicationConfig.startServer(7007);

        // RestAssured base
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7007;
        RestAssured.basePath = "/api";

        // 1) Login – hvis 401, så register + login
        var login = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"user\",\"password\":\"test123\"}")
                .when().post("/auth/login/")
                .then();

        if (login.extract().statusCode() != 200) {
            given().contentType(ContentType.JSON)
                    .body("{\"username\":\"user\",\"password\":\"test123\"}")
                    .when().post("/auth/register/")
                    .then().statusCode(201);

            login = given()
                    .contentType(ContentType.JSON)
                    .body("{\"username\":\"user\",\"password\":\"test123\"}")
                    .when().post("/auth/login/")
                    .then().statusCode(200);
        }

        jwt = login.extract().jsonPath().getString("token");
        // SIKKERHED: tjek at vi faktisk har en token
        if (jwt == null || jwt.isBlank()) throw new IllegalStateException("Login returned no JWT");

        // 2) Giv user admin-rolle – MED Authorization-header
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
                .body("{\"role\":\"admin\"}")
                .when().post("/auth/user/role/")
                .then().statusCode(anyOf(is(200), is(204)));

        // 3) Login igen for at få token med ny rolle i claims (hvis din backend koder roller i token)
        jwt = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"user\",\"password\":\"test123\"}")
                .when().post("/auth/login/")
                .then().statusCode(200)
                .extract().jsonPath().getString("token");
    }

    @BeforeEach
    void seed() {
        truncateAll();
        Populator.populate(emf); // <- bruger din Populator med test-EMF
    }

    @AfterAll
    void stop() {
        if (app != null) ApplicationConfig.stopServer(app);
        if (emf != null) emf.close();
    }

    private void truncateAll() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createNativeQuery("TRUNCATE TABLE trips RESTART IDENTITY CASCADE").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE guides RESTART IDENTITY CASCADE").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    // ---------- US-3 ----------

    @Test
    void read_trip_by_id() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/trips/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", not(emptyOrNullString()))
                .body("category", not(emptyOrNullString()));
    }

    @Test
    void read_all_trips() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/trips")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()));
    }

    @Test
    void create_trip_requires_auth_and_returns_201() {
        var body = """
        {
          "country": "Greece",
          "name": "Island Hopping Adventure",
          "category": "SEA",
          "price": 2800.0,
          "start": "2025-12-20T09:00:00",
          "end": "2025-12-25T18:00:00",
          "guideId": 1
        }
        """;

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
                .body(body)
                .when()
                .post("/trips")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("name", equalTo("Island Hopping Adventure"))
                .body("category", equalTo("SEA"));
    }

    @Test
    void update_trip() {
        var body = """
        { "name": "Updated Name", "price": 1999.5 }
        """;

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwt)
                .body(body)
                .when()
                .put("/trips/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", equalTo("Updated Name"))
                .body("price", is(1999.5f));
    }

    @Test
    void delete_trip() {
        given()
                .header("Authorization", "Bearer " + jwt)
                .when()
                .delete("/trips/2")
                .then()
                .statusCode(204);
    }

    @Test
    void link_guide_to_trip() {
        given()
                .header("Authorization", "Bearer " + jwt)
                .when()
                .put("/trips/2/guides/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("guideId", is(2));
    }

    // ---------- US-4 ----------

    @Test
    void filter_by_category() {
        given()
                .accept(ContentType.JSON)
                .pathParam("category", "forest")
                .when()
                .get("/trips/category/{category}")   // basePath = /api, så fuld sti = /api/trips/category/forest
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()))
                .body("category.flatten()", everyItem(equalTo("FOREST")));
    }

    // ---------- US-6 ----------

    @Test
    void get_packing_items_for_trip() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/trips/1/packing")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("items", notNullValue());
    }

    @Test
    void get_total_packing_weight_in_grams() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/trips/1/packing/weight")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", instanceOf(Number.class));
    }

    // ---------- US-5 ----------

    @Test
    void get_total_price_per_guide() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/trips/guides/totalprice")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()))
                .body("[0].id", notNullValue())
                .body("[0].total", instanceOf(Number.class));
    }
}
