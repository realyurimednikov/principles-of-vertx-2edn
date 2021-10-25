package net.yurimednikov.vertxbook.cashx.common.messaging;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class InvalidMessagesTest {
    private final static int ERROR_CODE = 500;
    private final static String ERROR_MESSAGE = "Invalid message";

    @Test
    void sendInvalidMessage(Vertx vertx, VertxTestContext context){
        vertx.eventBus().consumer("my-consumer", message -> {
            message.fail(ERROR_CODE, ERROR_MESSAGE);
        });

        vertx.eventBus().request("my-consumer", new JsonObject(), reply -> {
            context.verify(() -> {
                Assertions.assertTrue(reply.failed());
                Assertions.assertTrue(reply.cause() instanceof ReplyException);
                ReplyException ex = (ReplyException)  reply.cause();
                Assertions.assertEquals(ERROR_MESSAGE, ex.getMessage());
                Assertions.assertEquals(ERROR_CODE, ex.failureCode());
                ReplyFailure failure = ex.failureType();
                Assertions.assertEquals(ReplyFailure.RECIPIENT_FAILURE, failure);
                context.completeNow();
            });
        });
    }
}
