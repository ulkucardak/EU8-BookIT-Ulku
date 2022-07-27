package com.bookit.utilities;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BookItApiUtil {

    public static String generateToken(String email,String password){
        Response response = given().
                accept(ContentType.JSON)
                .queryParam("email", email)
                .queryParam("password", password)
                .when().get(ConfigurationReader.get("qa2api.url")+"/sign");


        String token = response.path("accessToken");

       String finalToken= "Bearer "+token;
       return finalToken;

    }

    public static void deleteStudent(String studentEmail, String studentPassword){
        String studentToken = BookItApiUtil.generateToken(studentEmail,studentPassword);

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


    }
}
