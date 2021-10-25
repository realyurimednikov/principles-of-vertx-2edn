package net.yurimednikov.vertxbook.cashx.common.futures;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

@ExtendWith(VertxExtension.class)
class FuturesTest {

    @Test
    void nullResultTest(Vertx vertx, VertxTestContext context){
        context.verify(() -> {
            Future<Object> future = Future.succeededFuture(null);
            future.onSuccess(r -> {
                Assertions.assertNull(r);
                context.completeNow();
            });
        });
    }

    @Test
    void nullWitHOptionalTest(Vertx vertx, VertxTestContext context){
        context.verify(() -> {
            Future<Object> future = Future.succeededFuture(null);
            future.map(r -> Optional.ofNullable(r))
                .onSuccess(r -> {
                    Assertions.assertTrue(r.isEmpty());
                    context.completeNow();
                });
        });
    }

    @Test
    void fromCompletableFutureTest(Vertx vertx, VertxTestContext context){
        CompletableFuture<HttpResponse<String>> request = Unirest.get("https://jsonplaceholder.typicode.com/posts").asStringAsync();
        Future<HttpResponse<String>> future = Future.fromCompletionStage(request);
        context.verify(() -> {
            future.onSuccess(result -> {
                Assertions.assertEquals(200, result.getStatus());
                context.completeNow();
            });
        });
    }
    
}
