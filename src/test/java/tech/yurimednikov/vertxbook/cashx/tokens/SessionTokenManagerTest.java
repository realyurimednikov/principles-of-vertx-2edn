package tech.yurimednikov.vertxbook.cashx.tokens;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.UUID;

@ExtendWith(VertxExtension.class)
class SessionTokenManagerTest {

    private SessionTokenManager manager;

    @BeforeEach
    void setup (){
        manager = new SessionTokenManager();
    }

    @Test
    void denyInvalidTokenTest(Vertx vertx, VertxTestContext context){
        context.verify(() -> {
            manager.decodeToken(UUID.randomUUID().toString())
                    .onFailure(e -> context.completeNow());
        });
    }

    @Test
    void verifyValidTokenTest (Vertx vertx, VertxTestContext context){
        Checkpoint decodeTokenCheckpoint = context.checkpoint();
        Checkpoint encodeTokenCheckpoint = context.checkpoint();
        context.verify(() -> {
            manager.encodeToken(1, List.of("read", "write"))
                    .compose(token -> {
                        Assertions.assertNotNull(token);
                        encodeTokenCheckpoint.flag();
                        return manager.decodeToken(token);
                    })
                    .onFailure(context::failNow)
                    .onSuccess(principal -> {
                        Assertions.assertEquals(1, principal.getUserid());
                        Assertions.assertEquals(List.of("read", "write"), principal.getPermissions());
                        decodeTokenCheckpoint.flag();
                        context.completeNow();
                    });
        });
    }
}
