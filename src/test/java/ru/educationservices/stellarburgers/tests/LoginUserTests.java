package ru.educationservices.stellarburgers.tests;

import io.qameta.allure.Link;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.educationservices.stellarburgers.resthandlers.apiclients.ResponseChecks;
import ru.educationservices.stellarburgers.resthandlers.apiclients.UserApiClient;

import java.util.UUID;

import static org.junit.Assert.fail;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.apache.http.HttpStatus.SC_ACCEPTED;

@DisplayName("2. Логин пользователя")
@Link(value = "Документация", url = "https://code.s3.yandex.net/qa-automation-engineer/python-full/diploma/Api-Stellar_Burgers.pdf")
public class LoginUserTests {
    private String email, password, name, token;
    private final UserApiClient userApi = new UserApiClient();
    private final ResponseChecks checks = new ResponseChecks();

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        email = "e-mail_" + UUID.randomUUID() + "@mail.com";
        password = "pass";
        name = "name";

        // Создание пользователя
        Response response = userApi.createUser(email, password, name);
        checks.checkStatusCode(response, SC_OK);

        // Получение токена авторизации
        if (response.getStatusCode() == SC_OK) {
            token = userApi.getToken(response);
        }
        if (token == null)
            fail("Не создался тестовый пользователь");
    }

    @After
    @Step("Удаление тестовых пользователей")
    public void cleanTestData() {
        if (token == null || token.isEmpty())
            return;

        checks.checkStatusCode(userApi.deleteUser(token), SC_ACCEPTED);
    }

    @Test
    @DisplayName("Авторизация существующего пользователя")
    @Description("Проверяет успешную авторизацию зарегистрированного пользователя с корректным e-mail и паролем")
    public void loginUserIsSuccess() {
        Response response = userApi.loginUser(email, password);
        checks.checkStatusCode(response, SC_OK);
        checks.checkLabelSuccess(response, true);
    }

    @Test
    @DisplayName("Авторизация пользователя с некорректным e-mail")
    @Description("Проверяет, что авторизация пользователя с неверным e-mail завершается с ошибкой")
    public void loginUserIncorrectEmailIsFailed() {
        Response response = userApi.loginUser("newE-mail_" + UUID.randomUUID() + "@mail.com", password);
        checks.checkStatusCode(response, SC_UNAUTHORIZED);
        checks.checkLabelSuccess(response, false);
        checks.checkLabelMessage(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Авторизация пользователя с некорректным паролем")
    @Description("Проверяет, что авторизация пользователя с неверным паролем завершается с ошибкой")
    public void loginUserIncorrectPasswordIsFailed() {
        Response response = userApi.loginUser(email, password  + UUID.randomUUID());
        checks.checkStatusCode(response, SC_UNAUTHORIZED);
        checks.checkLabelSuccess(response, false);
        checks.checkLabelMessage(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Авторизация пользователя без e-mail")
    @Description("Проверяет, что авторизация без e-mail завершается с ошибкой")
    public void loginUserMissedEmailIsFailed() {
        Response response = userApi.loginUser("", password);
        checks.checkStatusCode(response, SC_UNAUTHORIZED);
        checks.checkLabelSuccess(response, false);
        checks.checkLabelMessage(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Авторизация пользователя без пароля")
    @Description("Проверяет, что авторизация без пароля завершается с ошибкой")
    public void loginUserMissedPasswordIsFailed() {
        Response response = userApi.loginUser(email, "");
        checks.checkStatusCode(response, SC_UNAUTHORIZED);
        checks.checkLabelSuccess(response, false);
        checks.checkLabelMessage(response, "email or password are incorrect");
    }
}