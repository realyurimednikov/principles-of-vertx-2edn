package net.yurimednikov.vertxbook.cashx.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import net.yurimednikov.vertxbook.cashx.models.SimpleOperation;

@ExtendWith(VertxExtension.class)
@Disabled
class SimpleOperationReactivePgRepositoryImplTest {

    SimpleOperationReactivePgRepositoryImpl repository;
    
    @BeforeEach
    void setup (Vertx vertx, VertxTestContext context) {
        // todo refactor with uri
        PgConnectOptions connectOptions = new PgConnectOptions()
        .setHost("localhost")
        .setUser("cashxuser")
        .setPassword("secret")
        .setDatabase("cashx");

        PoolOptions poolOptions = new PoolOptions().setMaxSize(1);

        SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);
        repository = new SimpleOperationReactivePgRepositoryImpl(client);
        context.completeNow();
    }

    @Test
    void createSimpleOperationTest(Vertx vertx, VertxTestContext context){
        // Account account = new Account(1, "My bank account", "EUR", 1);
        SimpleOperation operation = new SimpleOperation(0, 1, "Lorem ispum", "expenses", "EUR", BigDecimal.valueOf(10), LocalDateTime.now(), 1);
        context.verify(() -> {
            repository.saveOperation(operation)
            .onFailure(e -> context.failNow(e))
            .onSuccess(result -> {
                Assertions.assertNotEquals(0, result.getId());
                context.completeNow();
            });
        });
    }

    @Test
    void removeSimpleOperationTest(Vertx vertx, VertxTestContext context){
        SimpleOperation operation = new SimpleOperation(0, 1, "Lorem ispum", "expenses", "EUR", BigDecimal.valueOf(10), LocalDateTime.now(), 1);
        Checkpoint savedCheckpoint = context.checkpoint();
        Checkpoint removeCheckpoint = context.checkpoint();
        context.verify(() -> {
            repository.saveOperation(operation)
            .compose(result -> {
                Assertions.assertNotEquals(0, result.getId());
                savedCheckpoint.flag();
                return Future.succeededFuture(result.getId());
            })
            .compose(id -> repository.removeOperation(id))
            .onSuccess(result -> {
                removeCheckpoint.flag();
                Assertions.assertTrue(result);
                context.completeNow();
            })
            .onFailure(err -> context.failNow(err));
        });
    }

    @Test
    void findSimpleOperationByIdTest (Vertx vertx, VertxTestContext context){
        // assume that account does exist in db
        SimpleOperation operation = new SimpleOperation(0, 1, "New operation", "income", "RSD", BigDecimal.valueOf(10000), LocalDateTime.now(), 1);
        Checkpoint savedCheckpoint = context.checkpoint();
        Checkpoint retrievedCheckpoint = context.checkpoint();
        Checkpoint removeCheckpoint = context.checkpoint();

        context.verify(() -> {
            repository.saveOperation(operation)
            .compose(saved -> {
                savedCheckpoint.flag();
                Assertions.assertNotEquals(0, saved.getId());
                System.out.println("Saved");
                return Future.succeededFuture(saved.getId());
            })
            .compose(id -> repository.findOperationById(id))
            .compose(retrieved -> {
                Assertions.assertTrue(retrieved.isPresent());
                
                SimpleOperation result = retrieved.get();
                Assertions.assertNotEquals(0, result.getId());
                Assertions.assertEquals(operation.getAmount(), result.getAmount());
                Assertions.assertEquals(operation.getCurrency(), result.getCurrency());
                Assertions.assertEquals(operation.getDateTime(), result.getDateTime());
                Assertions.assertEquals(operation.getAccount().getId(), 1);
                retrievedCheckpoint.flag();
                System.out.println("Retrieved");
                return Future.succeededFuture(result.getId());
            })
            .compose(id -> repository.removeOperation(id))
            .onSuccess(result -> {
                Assertions.assertTrue(result);
                System.out.println("Removed");
                removeCheckpoint.flag();
                context.completeNow();
            })
            .onFailure(err -> context.failNow(err));
        });
    }

    @Test
    @Disabled
    void findSimpleOperationsForUserTest(Vertx vertx, VertxTestContext context){
        long userId = 1;
        context.verify(() ->{
            repository.findOperations(userId)
            .onFailure(err -> context.failNow(err))
            .onSuccess(result -> {
                Assertions.assertEquals(5, result.getOperations().size());
                context.completeNow(); 
            });
        });
    }
}
