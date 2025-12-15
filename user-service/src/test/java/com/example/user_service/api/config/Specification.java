package com.example.user_service.api.config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class Specification {

    public static RequestSpecification requestSpecification(String url) {
        return new RequestSpecBuilder()
                .setBaseUri(url)
                .setContentType(ContentType.JSON)
                .build();
    }
    public static ResponseSpecification responseSpecificationOk() {
        return new ResponseSpecBuilder().expectStatusCode(200).build();
    }
    public static ResponseSpecification responseSpecificationError400() {
        return new ResponseSpecBuilder().expectStatusCode(400).build();
    }
    public static ResponseSpecification responseSpecificationError401() {
        return new ResponseSpecBuilder().expectStatusCode(401).build();
    }
    public static ResponseSpecification responseSpecificationError403() {
        return new ResponseSpecBuilder().expectStatusCode(403).build();
    }
    public static ResponseSpecification responseSpecificationError500() {
        return new ResponseSpecBuilder().expectStatusCode(500).build();
    }
    public static void installRequestSpecification(RequestSpecification request) {
        RestAssured.requestSpecification = request;
    }
    public static void installSpecifications(RequestSpecification request, ResponseSpecification response) {
        RestAssured.requestSpecification = request;
        RestAssured.responseSpecification = response;
    }
}
