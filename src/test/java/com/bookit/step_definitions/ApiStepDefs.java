package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtil;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.Map;

import static io.restassured.RestAssured.*;

public class ApiStepDefs {
    String token;
    Response response;
    String emailGlobal;

    Map<String,String> globalStudentInfo;
    @Given("I logged Bookit api using {string} and {string}")
    public void i_logged_Bookit_api_using_and(String email, String password) {

        token = BookItApiUtil.generateToken(email,password);
        //response= given().accept(ContentType.JSON)
                //.when().get("https://cybertek-reservation-api-qa2.herokuapp.com/sign?email=wcanadinea@ihg.com&password=waverleycanadine");
        // token = response.path("accessToken");

        emailGlobal = email;

    }

    @When("I get the current user information from api")
    public void i_get_the_current_user_information_from_api() {
        response = given().accept(ContentType.JSON)
                .and().header("Authorization",token)
                .when().get(ConfigurationReader.get("qa2api.url")+"/api/users/me");



    }

    @Then("status code should be {int}")
    public void status_code_should_be(int statusCode) {
        Assert.assertEquals(statusCode,response.statusCode());

    }


    @Then("the information about current user from api and database should match")
    public void theInformationAboutCurrentUserFromApiAndDatabaseShouldMatch() {
        String query = "select firstname,lastname,role from users\n" +
                "where email = '"+emailGlobal+"'";
        Map<String, Object> dbMap = DBUtils.getRowMap(query);
        String expectedFirstName = (String) dbMap.get("firstname");
        String expectedLastName = (String) dbMap.get("lastname");
        String expectedRole = (String) dbMap.get("role");


        JsonPath jsonPath = response.jsonPath();
        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");

        Assert.assertEquals(expectedFirstName,actualFirstName);
        Assert.assertEquals(expectedLastName,actualLastName);
        Assert.assertEquals(expectedRole,actualRole);

    }

    @Then("UI,API and Database user information must be match")
    public void uiAPIAndDatabaseUserInformationMustBeMatch() {
        String query = "select firstname,lastname,role from users\n" +
                "where email = '"+emailGlobal+"'";
        Map<String, Object> dbMap = DBUtils.getRowMap(query);
        String expectedFirstName = (String) dbMap.get("firstname");
        String expectedLastName = (String) dbMap.get("lastname");
        String expectedRole = (String) dbMap.get("role");


        JsonPath jsonPath = response.jsonPath();
        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");

        Assert.assertEquals(expectedFirstName,actualFirstName);
        Assert.assertEquals(expectedLastName,actualLastName);
        Assert.assertEquals(expectedRole,actualRole);


        SelfPage selfPage = new SelfPage();

        String actualUIName = selfPage.name.getText();
        String actualUIRole = selfPage.role.getText();

        // UI vs DB
        // create a full name for db
        String expectedFullName = expectedFirstName+" "+expectedLastName;
        Assert.assertEquals(expectedFullName,actualUIName);
        Assert.assertEquals(expectedRole,actualUIRole);


        //UI vs Api
        // Create a full name for api

        String actualFullNameApi = actualFirstName+" "+actualLastName;
        Assert.assertEquals(actualFullNameApi,actualUIName);
        Assert.assertEquals(actualRole,actualUIRole);

    }

    @When("I send POST request to {string} endpoint with following information")
    public void iSendPOSTRequestToEndpointWithFollowingInformation(String path,Map<String,String> studentInfo) {

        response = given().accept(ContentType.JSON)
                .and().header("Authorization", token)
                .and().queryParams(studentInfo)
                .log().all()
                .post(ConfigurationReader.get("qa2api.url") + path);

        globalStudentInfo = studentInfo;

    }

    /*@Then("I delete previously added student")
    public void i_delete_previously_added_student() {
        String studentToken = BookItApiUtil.generateToken(globalStudentInfo.get("email"),globalStudentInfo.get("password"));

        int idToDelete = given().accept(ContentType.JSON)
                .and().header("Authorization", studentToken)
                .get(ConfigurationReader.get("qa2api.url")+"/api/users/me")
                .then().statusCode(200).extract().jsonPath().getInt("id");

        String teacherToken = BookItApiUtil.generateToken(ConfigurationReader.get("teacher_email"),ConfigurationReader.get("teacher_password"));
        given()
                .and().header("Authorization", teacherToken)
                .and().pathParam("id",idToDelete)
                .when().delete(ConfigurationReader.get("qa2api.url")+"/api/students/{id}")
                .then().statusCode(204);

    }*/

    @Then("I delete previously added student")
    public void i_delete_previously_added_student() {
        BookItApiUtil.deleteStudent(globalStudentInfo.get("email"),globalStudentInfo.get("password"));

    }



}
