package net.yurimednikov.vertxbook.cashx.tokens;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@ExtendWith(VertxExtension.class)
class SessionTokenManagerTest {

    private SessionTokenManager manager;

    @BeforeEach
    void setup (){
        manager = new SessionTokenManager();
    }

    @Test
    void verifyToken (Vertx vertx, VertxTestContext context){
        context.verify(() -> {
            manager.encodeToken(1, List.of("read", "write"))
                    .compose(token -> manager.decodeToken(token))
                    .onFailure(context::failNow)
                    .onSuccess(principal -> {
                        Assertions.assertEquals(1, principal.getUserid());
                        Assertions.assertEquals(List.of("read", "write"), principal.getPermissions());
                        context.completeNow();
                    });
        });
    }
}
