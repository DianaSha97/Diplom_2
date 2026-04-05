package ru.educationservices.stellarburgers.tests;

import io.qameta.allure.Link;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.educationservices.stellarburgers.response.entities.Ingredient;
import ru.educationservices.stellarburgers.response.entities.IngredientsResponse;
import ru.educationservices.stellarburgers.resthandlers.apiclients.ResponseChecks;
import ru.educationservices.stellarburgers.resthandlers.apiclients.OrderApiClient;
import ru.educationservices.stellarburgers.resthandlers.apiclients.UserApiClient;

import java.util.*;

import static org.junit.Assert.fail;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_ACCEPTED;

@DisplayName("3. Создание заказа")
@Link(value = "Документация", url = "https://code.s3.yandex.net/qa-automation-engineer/python-full/diploma/Api-Stellar_Burgers.pdf")
public class CreateOrderTests {

    private String email, password, name, token;
    private List<Ingredient> ingredient;
    private final OrderApiClient orderApi = new OrderApiClient();
    private final UserApiClient userApi = new UserApiClient();
    private final ResponseChecks checks = new ResponseChecks();

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        email = "e-mail_" + UUID.randomUUID() + "@mail.com";
        password = "pass_" + UUID.randomUUID();
        name = "name";

        // Создание пользователя
        Response response = userApi.createUser(email, password, name);
        checks.checkStatusCode(response, SC_OK);

        // Получение токена
        if (response.getStatusCode() == SC_OK) {
            token = userApi.getToken(response);
        }

        // Получение списка ингредиентов
        response = orderApi.getIngredientList();
        checks.checkStatusCode(response, SC_OK);

        ingredient = response.body().as(IngredientsResponse.class).getData();

        if(token == null || ingredient.isEmpty())
            fail("Отсутствует токен или не получен список ингредиентов");
    }

    @After
    @Step("Удаление тестовых пользователей")
    public void cleanTestData() {
        if(token == null)
            return;

        checks.checkStatusCode(userApi.deleteUser(token), SC_ACCEPTED);
    }

    @Test
    @DisplayName("Создание заказа: с авторизацией и с ингредиентами")
    @Description("Проверяет успешное создание заказа с авторизацией и корректными ингредиентами")
    public void createOrderWithAuthAndIngredientsIsSuccess() {
        Response response = orderApi.createOrder(
                List.of(ingredient.get(0).getId(), ingredient.get(ingredient.size() - 1).getId()),
                token
        );
        checks.checkStatusCode(response, SC_OK);
        checks.checkLabelSuccess(response, true);
    }

    @Test
    @DisplayName("Создание заказа: без авторизации и с ингредиентами")
    @Description("Проверяет, что создание заказа без авторизации, но с ингредиентами, завершается с ошибкой")
    public void createOrderWithoutAuthAndWithIngredientsIsFailed() {
        Response response = orderApi.createOrder(
                List.of(ingredient.get(0).getId(), ingredient.get(ingredient.size() - 1).getId()),
                ""
        );
        checks.checkStatusCode(response, SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Создание заказа: с авторизацией и без ингредиентов")
    @Description("Проверяет, что создание заказа с авторизацией, но без ингредиентов, завершается с ошибкой")
    public void createOrderWithAuthAndWithoutIngredientsIsSuccess() {
        Response response = orderApi.createOrder(
                List.of(),
                token
        );
        checks.checkStatusCode(response, SC_BAD_REQUEST);
        checks.checkLabelSuccess(response, false);
        checks.checkLabelMessage(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа: без авторизации и без ингредиентов")
    @Description("Проверяет, что создание заказа без авторизации и без ингредиентов, завершается с ошибкой")
    public void createOrderWithoutAuthAndWithoutIngredientsIsFailed() {
        Response response = orderApi.createOrder(
                List.of(),
                ""
        );
        checks.checkStatusCode(response, SC_BAD_REQUEST);
        checks.checkLabelMessage(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа: с неверным хешем ингредиентов")
    @Description("Проверяет, что создание заказа с авторизацией, но с некорректным ID ингредиента, возвращает ошибку сервера")
    public void createOrderWithAuthAndIncorrectIngredientsIsFailed() {
        Response response = orderApi.createOrder(
                List.of(ingredient.get(0).getId(), UUID.randomUUID().toString()),
                token
        );
        checks.checkStatusCode(response, SC_INTERNAL_SERVER_ERROR);
    }
}