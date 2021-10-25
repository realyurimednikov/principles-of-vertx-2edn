package net.yurimednikov.vertxbook.cashx.common.messaging;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class DeadMessagesTest {

    @Test
    void sendDeadMessageFuture(Vertx vertx, VertxTestContext context){
        Future<Message<Object>> sr = vertx.eventBus().request("dead-consumer", new JsonObject());
        sr.onSuccess(message -> context.failNow("Should not be successful!"))
                .onFailure(err -> {
                    context.verify(() -> {
                        Assertions.assertTrue(err instanceof ReplyException);
                        ReplyException exception = (ReplyException) err;
                        ReplyFailure failure = exception.failureType();
                        Assertions.assertEquals(ReplyFailure.NO_HANDLERS, failure);
                        context.completeNow();
                    });
                });
    }
}
