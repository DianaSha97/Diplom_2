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

import java.util.ArrayList;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;

@DisplayName("1. Создание пользователя")
@Link(value = "Документация", url = "https://code.s3.yandex.net/qa-automation-engineer/python-full/diploma/Api-Stellar_Burgers.pdf")
public class CreateUserTests {
    private String email, password, name;
    private ArrayList<String> tokens = new ArrayList<>();
    private final UserApiClient userApi = new UserApiClient();
    private final ResponseChecks checks = new ResponseChecks();

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        email = "e-mail_" + UUID.randomUUID() + "@mail.com";
        password = "pass_" + UUID.randomUUID();
        name = "name";
    }

    @After
    @Step("Удаление тестовых данных пользователей")
    public void deletingUser() {
        if(tokens.isEmpty())
            return;

        for (String token: tokens) {
            checks.checkStatusCode(userApi.deleteUser(token), SC_ACCEPTED);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверяет успешное создание нового уникального пользователя с валидными данными")
    public void createNewUserIsSuccess() {
        Response response = userApi.createUser(email, password, name);

        if (response.getStatusCode() == SC_OK) {
            tokens.add(userApi.getToken(response));
        }

        checks.checkStatusCode(response, SC_OK);
        checks.checkLabelSuccess(response, true);
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Проверяет, что повторное создание пользователя с теми же данными возвращает ошибку")
    public void createDuplicateUserShouldFail() {
        Response responseFirstUser = userApi.createUser(email, password, name);
        Response responseSecondUser = userApi.createUser(email, password, name);

        if (responseFirstUser.getStatusCode() == SC_OK) {
            tokens.add(userApi.getToken(responseFirstUser));
        }
        if (responseSecondUser.getStatusCode() == SC_OK) {
            tokens.add(userApi.getToken(responseSecondUser));
        }

        checks.checkStatusCode(responseSecondUser, SC_FORBIDDEN);
        checks.checkLabelSuccess(responseSecondUser, false);
        checks.checkLabelMessage(responseSecondUser, "User already exists");
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверяет, что создание пользователя без email возвращает ошибку")
    public void createNewUserMissedEmailIsFailed() {
        Response response = userApi.createUser("", password, name);

        if (response.getStatusCode() == SC_OK) {
            tokens.add(userApi.getToken(response));
        }

        checks.checkStatusCode(response, SC_FORBIDDEN);
        checks.checkLabelSuccess(response, false);
        checks.checkLabelMessage(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без password")
    @Description("Проверяет, что создание пользователя без пароля возвращает ошибку")
    public void createNewUserMissedPasswordIsFailed() {
        Response response = userApi.createUser(email, "", name);

        if (response.getStatusCode() == SC_OK) {
            tokens.add(userApi.getToken(response));
        }

        checks.checkStatusCode(response, SC_FORBIDDEN);
        checks.checkLabelSuccess(response, false);
        checks.checkLabelMessage(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без name")
    @Description("Проверяет, что создание пользователя без имени возвращает ошибку")
    public void createNewUserMissedNameIsFailed() {
        Response response = userApi.createUser(email, password, "");

        if (response.getStatusCode() == SC_OK) {
            tokens.add(userApi.getToken(response));
        }

        checks.checkStatusCode(response, SC_FORBIDDEN);
        checks.checkLabelSuccess(response, false);
        checks.checkLabelMessage(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя, когда ни одно поле не заполнено")
    @Description("Проверяет, что создание пользователя без заполнения всех полей возвращает ошибку")
    public void createNewUserMissedAllParamsIsFailed() {
        Response response = userApi.createUser("", "", "");

        if (response.getStatusCode() == SC_OK) {
            tokens.add(userApi.getToken(response));
        }

        checks.checkStatusCode(response, SC_FORBIDDEN);
        checks.checkLabelSuccess(response, false);
        checks.checkLabelMessage(response, "Email, password and name are required fields");
    }
}
