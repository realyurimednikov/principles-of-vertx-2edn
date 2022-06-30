package tech.yurimednikov.vertxbook.cashx.repositories;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;
import tech.yurimednikov.vertxbook.cashx.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

@Testcontainers
@ExtendWith(VertxExtension.class)
public class UserReactivePgRepositoryImplTest {

    private UserReactivePgRepositoryImpl repository;

    @Container
    private PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:11-alpine")
            .withDatabaseName("cashxdb").withUsername("user").withPassword("secret");

    @BeforeEach
    void setup (Vertx vertx, VertxTestContext context){
        int port = container.getFirstMappedPort();
        String uri = "postgresql://user:secret@localhost:" + port + "/cashxdb";
        SqlClient client = PgPool.client(vertx, uri);
        repository = new UserReactivePgRepositoryImpl(vertx, client);
        repository.createTable().onFailure(context::failNow).onSuccess(r -> context.completeNow());
    }

    @Test
    void saveUserTest (Vertx vertx, VertxTestContext context){
        User user = new User(0, "user@email.com", "hash", "salt", List.of(), LocalDate.now());
        context.verify(() -> {
           repository.saveUser(user).onFailure(context::failNow).onSuccess(result -> {
               Assertions.assertNotEquals(0, result.getUserId());
               context.completeNow();
           }) ;
        });
    }

    @Test
    void findUserByEmailTest(Vertx vertx, VertxTestContext context){
        Checkpoint createCheckpoint = context.checkpoint();
        Checkpoint retrieveCheckpoint = context.checkpoint();
        User user = new User(0, "user@email.com", "hash", "salt", List.of(), LocalDate.now());
        context.verify(() -> {
           repository.saveUser(user).map(u -> {
               Assertions.assertNotEquals(0, u.getUserId());
               createCheckpoint.flag();
               return u.getEmail();
           }).compose(email -> repository.findUserByEmail(email)).onFailure(context::failNow)
           .onSuccess(result -> {
                Assertions.assertTrue(result.isPresent());
                retrieveCheckpoint.flag();
                context.completeNow();
           });
        });
    }
}
