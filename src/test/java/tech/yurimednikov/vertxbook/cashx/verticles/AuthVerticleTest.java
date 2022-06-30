package tech.yurimednikov.vertxbook.cashx.verticles;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import tech.yurimednikov.vertxbook.cashx.errors.AccessDeniedException;
import tech.yurimednikov.vertxbook.cashx.services.AuthService;
import tech.yurimednikov.vertxbook.cashx.web.AuthController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(VertxExtension.class)
class AuthVerticleTest {

    @Mock
    private AuthService service;

    @InjectMocks
    private AuthController controller;

    private WebClient client;

    @BeforeEach
    void setup (Vertx vertx, VertxTestContext context){
        AuthVerticle verticle = new AuthVerticle(controller);
        client = WebClient.create(vertx);
        vertx.deployVerticle(verticle).onFailure(context::failNow).onSuccess(r -> context.completeNow());
    }

    @Test
    void signupEndpointTest (Vertx vertx, VertxTestContext context){
        String email = "john.doe@email.com";
        String password = "secret";
        Mockito.when(service.signup(email, password)).thenReturn(Future.succeededFuture(Boolean.TRUE));
        JsonObject payload = new JsonObject();
        payload.put("email", email);
        payload.put("password", password);
        context.verify(() -> {
            client.postAbs("http://localhost:8080/api/auth/signup")
                    .expect(ResponsePredicate.SC_CREATED)
                    .sendJsonObject(payload)
                    .onFailure(context::failNow)
                    .onSuccess(response -> {
                        context.completeNow();
                    });
        });
    }

    @Test
    void loginOkEndpointTest (Vertx vertx, VertxTestContext context){
        String email = "john.doe@email.com";
        String password = "secret";
        String token = "my-token";
        Mockito.when(service.login(email, password)).thenReturn(Future.succeededFuture(token));
        JsonObject payload = new JsonObject();
        payload.put("email", email);
        payload.put("password", password);
        context.verify(() -> {
            client.postAbs("http://localhost:8080/api/auth/login")
                    .expect(ResponsePredicate.SC_OK)
                    .sendJsonObject(payload)
                    .onFailure(context::failNow)
                    .onSuccess(response -> {
                        JsonObject responseBody = response.bodyAsJsonObject();
                        Assertions.assertEquals(token, responseBody.getString("token"));
                        context.completeNow();
                    });
        });
    }

    @Test
    void loginDeniedEndpointTest (Vertx vertx, VertxTestContext context){
        String email = "john.doe@email.com";
        String password = "secret";
        Mockito.when(service.login(email, password)).thenReturn(Future.failedFuture(new AccessDeniedException()));
        JsonObject payload = new JsonObject();
        payload.put("email", email);
        payload.put("password", password);
        context.verify(() -> {
            client.postAbs("http://localhost:8080/api/auth/login")
                    .expect(ResponsePredicate.SC_FORBIDDEN)
                    .sendJsonObject(payload)
                    .onFailure(context::failNow)
                    .onSuccess(response -> {
                        context.completeNow();
                    });
        });
    }
}
