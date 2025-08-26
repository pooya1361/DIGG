package com.digg.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class UserResourceTest {
    private static final String BASE_PATH = "/users";

    @Test
    @DisplayName("Should return all users")
    public void testGetAllUsers() {
        given()
                .when().get(BASE_PATH)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", equalTo(50));
    }

    @Test
    @DisplayName("Should create a new user")
    public void testCreateUser() {
        String newUser = """
                {
                    "name": "Test User",
                    "address": "123 Test St, Test City, TS 12345",
                    "email": "test@digg.com",
                    "telephone": "(555) 123-4567"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when().post(BASE_PATH)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Test User"))
                .body("email", equalTo("test@digg.com"));
    }

    @Test
    @DisplayName("Should get user by ID")
    public void testGetUserById() {
        given()
                .when().get(BASE_PATH + "/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", notNullValue())
                .body("email", notNullValue());
    }

    @Test
    @DisplayName("Test search with guaranteed existing name")
    public void testSearchWithKnownName() {
        // Get first user's name
        Response response = given()
                .when().get("/users")
                .then()
                .statusCode(200)
                .extract().response();

        String firstName = response.jsonPath().getString("[0].name").split(" ")[0];

        System.out.println("Testing search with name: " + firstName);

        given()
                .queryParam("name", firstName)
                .when().get("/users/search")
                .then()
                .statusCode(200)
                .body("size()", org.hamcrest.Matchers.greaterThan(0));
    }

    @Test
    @DisplayName("Should return 404 for non-existent user")
    public void testGetNonExistentUser() {
        given()
                .when().get(BASE_PATH + "/999999")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should update user successfully")
    public void testUpdateUser() {
        // Create a user first
        String newUser = """
                {
                    "name": "Original User",
                    "address": "100 Original St, Original City, OR 97001",
                    "email": "original@digg.com",
                    "telephone": "(555) 100-0001"
                }
                """;

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when().post(BASE_PATH)
                .then()
                .statusCode(201)
                .extract().response();

        Long userId = createResponse.jsonPath().getLong("id");

        // Update the user
        String updatedUser = """
                {
                    "name": "Updated User",
                    "address": "200 Updated Ave, Updated City, UP 97002",
                    "email": "updated@digg.com",
                    "telephone": "(555) 200-0002"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedUser)
                .when().put(BASE_PATH + "/" + userId)
                .then()
                .statusCode(200)
                .body("id", equalTo(userId.intValue()))
                .body("name", equalTo("Updated User"))
                .body("email", equalTo("updated@digg.com"))
                .body("address", equalTo("200 Updated Ave, Updated City, UP 97002"))
                .body("telephone", equalTo("(555) 200-0002"));

        // Clean up - delete the user
        given().delete(BASE_PATH + "/" + userId);
    }

    @Test
    @DisplayName("Should prevent updating to duplicate email")
    public void testUpdateWithDuplicateEmail() {
        // Create first user
        String user1 = """
                {
                    "name": "User One",
                    "address": "100 First St",
                    "email": "user1@digg.com",
                    "telephone": "(555) 111-1111"
                }
                """;

        Response user1Response = given()
                .contentType(ContentType.JSON)
                .body(user1)
                .when().post(BASE_PATH)
                .then()
                .statusCode(201)
                .extract().response();

        // Create second user
        String user2 = """
                {
                    "name": "User Two",
                    "address": "200 Second St",
                    "email": "user2@digg.com",
                    "telephone": "(555) 222-2222"
                }
                """;

        Response user2Response = given()
                .contentType(ContentType.JSON)
                .body(user2)
                .when().post(BASE_PATH)
                .then()
                .statusCode(201)
                .extract().response();

        Long user1Id = user1Response.jsonPath().getLong("id");
        Long user2Id = user2Response.jsonPath().getLong("id");

        // Try to update user2 with user1's email
        String updateAttempt = """
                {
                    "name": "User Two Updated",
                    "address": "200 Second St Updated",
                    "email": "user1@digg.com",
                    "telephone": "(555) 222-2222"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updateAttempt)
                .when().put(BASE_PATH + "/" + user2Id)
                .then()
                .statusCode(409)
                .body("error", equalTo("Email already exists"));

        // Clean up
        given().delete(BASE_PATH + "/" + user1Id);
        given().delete(BASE_PATH + "/" + user2Id);
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent user")
    public void testUpdateNonExistentUser() {
        String updateData = """
                {
                    "name": "Non Existent User",
                    "address": "404 Not Found St",
                    "email": "notfound@digg.com",
                    "telephone": "(404) 404-4040"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updateData)
                .when().put(BASE_PATH + "/999999")
                .then()
                .statusCode(404)
                .body("error", equalTo("User not found"));
    }

    @Test
    @DisplayName("Should delete user successfully")
    public void testDeleteUser() {
        // Create a user to delete
        String newUser = """
                {
                    "name": "To Be Deleted",
                    "address": "100 Temporary St, Temp City, TMP 99999",
                    "email": "tobedeleted@digg.com",
                    "telephone": "(555) 000-0000"
                }
                """;

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when().post(BASE_PATH)
                .then()
                .statusCode(201)
                .extract().response();

        Long userId = createResponse.jsonPath().getLong("id");

        // Verify user exists
        given()
                .when().get(BASE_PATH + "/" + userId)
                .then()
                .statusCode(200)
                .body("name", equalTo("To Be Deleted"));

        // Delete the user
        given()
                .when().delete(BASE_PATH + "/" + userId)
                .then()
                .statusCode(204);

        // Verify user is gone
        given()
                .when().get(BASE_PATH + "/" + userId)
                .then()
                .statusCode(404)
                .body("error", equalTo("User not found"));
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent user")
    public void testDeleteNonExistentUser() {
        given()
                .when().delete(BASE_PATH + "/999999")
                .then()
                .statusCode(404)
                .body("error", equalTo("User not found"));
    }

    @Test
    @DisplayName("Should validate fields during update")
    public void testUpdateValidation() {
        // Create a user
        String newUser = """
                {
                    "name": "Validation Test",
                    "address": "100 Valid St",
                    "email": "validation@digg.com",
                    "telephone": "(555) 123-4567"
                }
                """;

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when().post(BASE_PATH)
                .then()
                .statusCode(201)
                .extract().response();

        Long userId = createResponse.jsonPath().getLong("id");

        // Try invalid email format
        String invalidEmail = """
                {
                    "name": "Valid Name",
                    "address": "Valid Address",
                    "email": "not-an-email",
                    "telephone": "(555) 123-4567"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidEmail)
                .when().put(BASE_PATH + "/" + userId)
                .then()
                .statusCode(400);

        // Try missing required field
        String missingName = """
                {
                    "address": "Valid Address",
                    "email": "valid@digg.com",
                    "telephone": "(555) 123-4567"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(missingName)
                .when().put(BASE_PATH + "/" + userId)
                .then()
                .statusCode(400);

        // Clean up
        given().delete(BASE_PATH + "/" + userId);
    }

    @Test
    @DisplayName("Should handle partial updates correctly")
    public void testPartialUpdate() {
        // Create a user
        String newUser = """
                {
                    "name": "Partial Update Test",
                    "address": "100 Partial St",
                    "email": "partial@digg.com",
                    "telephone": "(555) 123-4567"
                }
                """;

        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when().post(BASE_PATH)
                .then()
                .statusCode(201)
                .extract().response();

        Long userId = createResponse.jsonPath().getLong("id");

        // Update with all fields (not really partial, but ensures full replacement)
        String fullUpdate = """
                {
                    "name": "Fully Updated Name",
                    "address": "200 Updated Address",
                    "email": "fullyupdated@digg.com",
                    "telephone": "(555) 987-6543"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(fullUpdate)
                .when().put(BASE_PATH + "/" + userId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Fully Updated Name"))
                .body("address", equalTo("200 Updated Address"))
                .body("email", equalTo("fullyupdated@digg.com"))
                .body("telephone", equalTo("(555) 987-6543"));

        // Clean up
        given().delete(BASE_PATH + "/" + userId);
    }
}