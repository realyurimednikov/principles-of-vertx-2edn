package net.yurimednikov.vertxbook.cashx.verticles;

import java.util.UUID;

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
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import net.yurimednikov.vertxbook.cashx.files.FileUploadService;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
class FileUploadVerticleTest {

    WebClient client;

    @Mock
    FileUploadService service;

    @InjectMocks
    FileUploadVerticle verticle;

    @BeforeEach
    void setup(Vertx vertx, VertxTestContext context){
        client = WebClient.create(vertx);
        vertx.deployVerticle(verticle)
            .onSuccess(r -> context.completeNow())
            .onFailure(context::failNow);
    }
    
    @Test
    void uploadFileEndpointTest(Vertx vertx, VertxTestContext context){
        JsonObject payload = new JsonObject();
        payload.put("name", UUID.randomUUID().toString());
        payload.put("format", "txt");
        payload.put("content", "SGVsbG8sIFdvcmxk");

        Mockito.when(service.upload(payload.getString("name"), payload.getString("format"), payload.getString("content")))
            .thenReturn(Future.succeededFuture(true));

        context.verify(() -> {
            client.postAbs("http://localhost:8080/api/upload").sendJsonObject(payload)
                .onSuccess(response -> {
                    JsonObject responseBody = response.bodyAsJsonObject();
                    Assertions.assertTrue(responseBody.getBoolean("success"));
                    context.completeNow();
                })
                .onFailure(context::failNow);
        });
    }
}
