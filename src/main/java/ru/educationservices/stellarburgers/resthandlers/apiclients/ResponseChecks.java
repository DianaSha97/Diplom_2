package ru.educationservices.stellarburgers.resthandlers.apiclients;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import ru.educationservices.stellarburgers.responseEntities.UserResponse;

import static org.hamcrest.Matchers.equalTo;

public class ResponseChecks {

    @Step("Проверка кода ответа")
    public void checkStatusCode(Response response, int code) {
        Allure.addAttachment("Ответ", response.getStatusLine());
        response.then().statusCode(code);
    }

    @Step("Проверка успешности обращения")
    public void checkLabelSuccess(Response response, boolean expectedValue) {
        MatcherAssert.assertThat(
                "Значение поля success не совпадает с ожидаемым",
                expectedValue,
                equalTo(response.body().as(UserResponse.class).isSuccess())
        );
    }
    @Step("Проверка сообщения ответа")
    public void checkLabelMessage(Response response, String expectedMessage) {
        MatcherAssert.assertThat(
                "Значение поля message не совпадает с ожидаемым",
                expectedMessage,
                equalTo(response.body().as(UserResponse.class).getMessage())
        );
    }
}
