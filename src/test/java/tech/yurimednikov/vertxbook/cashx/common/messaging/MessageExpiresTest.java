package tech.yurimednikov.vertxbook.cashx.common.messaging;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

@ExtendWith(VertxExtension.class)
class MessageExpiresTest {

    @Test
    void sendExpiredMessage(Vertx vertx, VertxTestContext context) throws Exception{
        vertx.eventBus().consumer("my-consumer", message -> System.out.println("I received message"));

        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setSendTimeout(1000);

        context.awaitCompletion(3, TimeUnit.SECONDS);

        vertx.eventBus().request("my-consumer", new JsonObject(), deliveryOptions, reply -> {
            context.verify(() -> {
                Assertions.assertTrue(reply.failed());
                Assertions.assertTrue(reply.cause() instanceof ReplyException);
                ReplyException ex = (ReplyException) reply.cause();
                Assertions.assertEquals(ReplyFailure.TIMEOUT, ex.failureType());
                context.completeNow();
            });
        });
    }
}
