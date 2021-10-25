package net.yurimednikov.vertxbook.cashx.webclient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
class WebClientTest {

    WebClient client;

    @BeforeEach
    void setup(Vertx vertx, VertxTestContext context){
        client = WebClient.create(vertx);
        context.completeNow();
    }
    
    @Test
    void requestWithoutBody(Vertx vertx, VertxTestContext context){
        context.verify(() -> {
            String url = "https://jsonplaceholder.typicode.com/posts";

            client.getAbs(url).as(BodyCodec.jsonArray()).send()
                .onFailure(context::failNow)
                .onSuccess(result -> {
                    int statusCode = result.statusCode();
                    Assertions.assertEquals(200, statusCode);
                    JsonArray body = result.body();
                    int length = body.size();
                    Assertions.assertEquals(100, length);
                    context.completeNow();
                });
        });
    }

    @Test
    void requestWithBody(Vertx vertx, VertxTestContext context){
        JsonObject payload = new JsonObject();
        payload.put("userId", 1);
        payload.put("title", "Title");
        payload.put("body", "Body");

        String url = "https://jsonplaceholder.typicode.com/posts";

        context.verify(() -> {
            client.postAbs(url).as(BodyCodec.jsonObject()).sendJsonObject(payload)
                .onFailure(context::failNow)
                .onSuccess(result -> {
                    JsonObject body = result.body();
                    int statusCode = result.statusCode();
                    int id = body.getInteger("id");
                    Assertions.assertEquals(101, id);
                    Assertions.assertEquals(201, statusCode);
                    context.completeNow();
                });
        });
    }
}
