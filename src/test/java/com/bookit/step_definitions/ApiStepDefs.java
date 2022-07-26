package com.bookit.step_definitions;

import com.bookit.utilities.BookItApiUtil;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.Map;

import static io.restassured.RestAssured.*;

public class ApiStepDefs {
    String token;
    Response response;
    String emailGlobal;
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
        Map<String, Object> student = DBUtils.getRowMap(query);


    }
}
