package net.yurimednikov.vertxbook.cashx.verticles;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import net.yurimednikov.vertxbook.cashx.models.Account;
import net.yurimednikov.vertxbook.cashx.models.AccountList;
import net.yurimednikov.vertxbook.cashx.services.AccountService;
import net.yurimednikov.vertxbook.cashx.web.AccountController;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
class AccountsRestVerticleTest {
    
    @Mock
    AccountService service;

    @InjectMocks
    AccountController controller;

    WebClient client;

    @BeforeEach
    void setup (Vertx vertx, VertxTestContext context){
        // AccountController controller = new AccountController(service);
        AccountsRestVerticle verticle = new AccountsRestVerticle(controller);
        client = WebClient.create(vertx);
        vertx.deployVerticle(verticle, result -> {
            if (result.succeeded()){
                System.out.println("Verticle deployed");
                context.completeNow();
            } else {
                System.out.println("Error happened");
                context.failNow(result.cause());
            }
        });
    }

    @Test
    void createAccountEndpointTest(Vertx vertx, VertxTestContext context){
        Account account = new Account(0, "New account", "EUR", 1);
        Account accountResponse = Account.withId(100, account);
        Mockito.when(service.createAccount(Mockito.any(Account.class))).thenReturn(Future.succeededFuture(accountResponse));
        JsonObject payload = JsonObject.mapFrom(account);
        // System.out.println(payload.encode());
        context.verify(() -> {
            client.postAbs("http://localhost:8080/api/accounts/1").sendJsonObject(payload)
                .onFailure(context::failNow)
                .onSuccess(result -> {
                    // System.out.println(result.bodyAsString());
                    int responseCode = result.statusCode();
                    Assertions.assertEquals(201, responseCode);
                    JsonObject responseBody = result.bodyAsJsonObject();
                    Assertions.assertEquals(100, responseBody.getLong("id"));
                    Assertions.assertEquals(account.getName(), responseBody.getString("name"));
                    Assertions.assertEquals(account.getCurrency(), responseBody.getString("currency"));
                    Assertions.assertEquals(account.getUserId(), responseBody.getLong("userId"));
                    context.completeNow();
                });
        });
    }

    @Test
    void createAccountValidationFailedEndpointTest(Vertx vertx, VertxTestContext context){
        // Account account = new Account(0, "New account", "EUR", 1);
        // Account accountResponse = Account.withId(100, account);
        // Mockito.when(service.createAccount(Mockito.any(Account.class))).thenReturn(Future.succeededFuture(accountResponse));
        // JsonObject payload = JsonObject.mapFrom(account);
        // System.out.println(payload.encode());
        JsonObject payload = new JsonObject();
        payload.put("name", "Account name");
        payload.put("userId", 1);
        payload.put("id", 0);
        context.verify(() -> {
            client.postAbs("http://localhost:8080/api/accounts/1").sendJsonObject(payload)
                .onFailure(context::failNow)
                .onSuccess(result -> {
                    int responseCode = result.statusCode();
                    Assertions.assertEquals(400, responseCode);
                    context.completeNow();
                });
        });
    }

    @Test
    void getAccountByIdFoundEndpointTest(Vertx vertx, VertxTestContext context){
        Account account = new Account(1234, "New account", "EUR", 1);
        Mockito.when(service.findAccountById(1234)).thenReturn(Future.succeededFuture(Optional.of(account)));
        context.verify(() -> {
            client.getAbs("http://localhost:8080/api/account/1234")
                .send()
                .onFailure(context::failNow)
                .onSuccess(result -> {
                    int responseCode = result.statusCode();
                    Assertions.assertEquals(200, responseCode);
                    JsonObject responseBody = result.bodyAsJsonObject();
                    Assertions.assertEquals(account.getId(), responseBody.getLong("id"));
                    Assertions.assertEquals(account.getName(), responseBody.getString("name"));
                    Assertions.assertEquals(account.getCurrency(), responseBody.getString("currency"));
                    Assertions.assertEquals(account.getUserId(), responseBody.getLong("userId"));
                    context.completeNow();
                });
        });
    }

    @Test
    void getAccountByIdNotFoundEndpointTest(Vertx vertx, VertxTestContext context){
        Mockito.when(service.findAccountById(1234)).thenReturn(Future.succeededFuture(Optional.empty()));
        context.verify(() -> {
            client.getAbs("http://localhost:8080/api/account/1234")
                .send()
                .onFailure(context::failNow)
                .onSuccess(result -> {
                    int responseCode = result.statusCode();
                    Assertions.assertEquals(404, responseCode);
                    context.completeNow();
                });
        });
    }

    @Test
    void getAccountsForUserEndpointTest(Vertx vertx, VertxTestContext context){
        List<Account> accounts = List.of(
            new Account(1234, "New account", "EUR", 1),
            new Account(3456, "New account", "EUR", 1),
            new Account(7890, "New account", "EUR", 1),
            new Account(1020, "New account", "EUR", 1),
            new Account(3040, "New account", "EUR", 1)
        );
        AccountList accountList = new AccountList(accounts);
        Mockito.when(service.findAccounts(1)).thenReturn(Future.succeededFuture(accountList));
        context.verify(() -> {
            client.getAbs("http://localhost:8080/api/accounts/1")
                .send()
                .onFailure(context::failNow)
                .onSuccess(result -> {
                    int responseCode = result.statusCode();
                    Assertions.assertEquals(200, responseCode);
                    JsonObject responseBody = result.bodyAsJsonObject();
                    Assertions.assertNotNull(responseBody);
                    JsonArray bodyAccounts = responseBody.getJsonArray("accounts");
                    Assertions.assertNotNull(bodyAccounts);
                    Assertions.assertEquals(5, bodyAccounts.size());
                    context.completeNow();
                });
        });
    }

    @Test
    void removeAccountEndpointTest(Vertx vertx, VertxTestContext context){
        Mockito.when(service.removeAccount(1234)).thenReturn(Future.succeededFuture(Boolean.TRUE));
        context.verify(() -> {
            client.deleteAbs("http://localhost:8080/api/account/1234")
                .send()
                .onFailure(context::failNow)
                .onSuccess(result -> {
                    int responseCode = result.statusCode();
                    Assertions.assertEquals(200, responseCode);
                    JsonObject responseBody = result.bodyAsJsonObject();
                    Assertions.assertNotNull(responseBody);
                    Assertions.assertEquals(true, responseBody.getBoolean("removed"));
                    context.completeNow();
                });
        });
    }

    @Test
    void updateAccountEndpointTest(Vertx vertx, VertxTestContext context){
        Account account = new Account(1234, "New account", "EUR", 1);
        Mockito.when(service.updateAccount(Mockito.any(Account.class))).thenReturn(Future.succeededFuture(account));
        JsonObject payload = JsonObject.mapFrom(account);
        context.verify(() -> {
            client.putAbs("http://localhost:8080/api/account/1234").sendJsonObject(payload)
                .onFailure(context::failNow)
                .onSuccess(result -> {
                    int responseCode = result.statusCode();
                    Assertions.assertEquals(200, responseCode);
                    JsonObject responseBody = result.bodyAsJsonObject();
                    Assertions.assertEquals(account.getName(), responseBody.getString("name"));
                    Assertions.assertEquals(account.getCurrency(), responseBody.getString("currency"));
                    Assertions.assertEquals(account.getUserId(), responseBody.getLong("userId"));
                    context.completeNow();
                });
        });
    }
}
