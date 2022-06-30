package tech.yurimednikov.vertxbook.cashx.common.blocking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import kong.unirest.Unirest;

@ExtendWith(VertxExtension.class)
class BlockingCodeTest {
    
    @Test
    void runningBlockingCodeTest(Vertx vertx, VertxTestContext context){
        context.verify(() -> {
            vertx.executeBlocking(promise -> {
                Unirest.get("https://jsonplaceholder.typicode.com/posts")
                        .accept("application/json")
                        .asString()
                    .ifSuccess(promise::complete)
                    .ifFailure(response -> promise.fail(new RuntimeException()));
            })
            .onFailure(context::failNow)
            .onSuccess(result -> {
                Assertions.assertNotNull(result);
                context.completeNow();
            });
        });
    }
}
