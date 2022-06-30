package tech.yurimednikov.vertxbook.cashx.common.messaging;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class MessagingPatternsTest {

    @Test
    void publishSubscribeTest(Vertx vertx, VertxTestContext context){
        EventBus eventBus = vertx.eventBus();
        JsonObject payload = new JsonObject().put("message", "Hello, Publish-Subscribe pattern");

        eventBus.consumer("receivers", message -> {
            System.out.println("Message received: " + message.body().toString());
            context.completeNow();
        });

        eventBus.publish("receivers", payload);
    }

    @Test
    void pointToPointTest(Vertx vertx, VertxTestContext context){
        EventBus eventBus = vertx.eventBus();
        JsonObject payload = new JsonObject().put("message", "Hello, Point to Point pattern");

        eventBus.consumer("receivers", message -> {
            System.out.println("Message received: " + message.body().toString());
            context.completeNow();
        });

        eventBus.send("receivers", payload);
    }

    @Test
    void requestResponseTest(Vertx vertx, VertxTestContext context){
        EventBus eventBus = vertx.eventBus();
        JsonObject payload = new JsonObject().put("message", "Hello, Request Response pattern");

        eventBus.consumer("receivers", message -> {
            System.out.println("Message received: " + message.body().toString());
            message.reply(new JsonObject().put("reply", "This is a reply!"));
        });

        eventBus.request("receivers", payload, result -> {
            System.out.println("Reply received: " + result.result().body());
            context.completeNow();
        });
    }
}
