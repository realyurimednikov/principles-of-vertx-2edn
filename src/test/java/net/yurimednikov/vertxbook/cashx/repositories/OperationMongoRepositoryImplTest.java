package net.yurimednikov.vertxbook.cashx.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import net.yurimednikov.vertxbook.cashx.models.Category;
import net.yurimednikov.vertxbook.cashx.models.Operation;
import net.yurimednikov.vertxbook.cashx.models.OperationAmount;

@ExtendWith(VertxExtension.class)
class OperationMongoRepositoryImplTest {

    private OperationMongoRepositoryImpl repository;

    @BeforeEach
    void setup(Vertx vertx, VertxTestContext context){
        JsonObject config = new JsonObject();
        config.put("connection_string", "");

        MongoClient client = MongoClient.createShared(vertx, config);
        repository = new OperationMongoRepositoryImpl(client);
        context.completeNow();
    }
    
    @Test
    void createOperationTest(Vertx vertx, VertxTestContext context){
        Operation operation = new Operation(null, 
            "user", "Just an operation", LocalDateTime.now(), 
            new OperationAmount("EUR", new BigDecimal("100")), 
            new Category("6167fce9a2a7173c209d85d2", "user", "Income from selling stuff", "income"), 
            "account");

        context.verify(() -> {
            repository.saveOperation(operation)
                .onSuccess(result -> {
                    System.out.println(result.id());
                    Assertions.assertNotNull(result.id());
                    context.completeNow();
                })
                .onFailure(err -> context.failNow(err));
        });
    }

    @Test
    void removeOperationTest(Vertx vertx, VertxTestContext context){
        Operation operation = new Operation(null, 
            "user", "Just an operation", LocalDateTime.now(), 
            new OperationAmount("EUR", new BigDecimal("100")), 
            new Category("6167fce9a2a7173c209d85d2", "user", "Income from selling stuff", "income"), 
            "account");
        
        Checkpoint saveCheckpoint = context.checkpoint();
        
        context.verify(() -> {
            repository.saveOperation(operation)
                .compose(saved -> {
                    Assertions.assertNotNull(saved.id());
                    saveCheckpoint.flag();
                    return Future.succeededFuture(saved.id());
                })
                .compose(id -> repository.removeOperation(id))
                .onFailure(err -> context.failNow(err))
                .onSuccess(deleted -> {
                    Assertions.assertTrue(deleted);
                    context.completeNow();
                });
        });
    }

    @Test
    void findOperationByIdTest(Vertx vertx, VertxTestContext context){
        Operation operation = new Operation(null, 
        "user", "Just an operation", LocalDateTime.now(), 
        new OperationAmount("EUR", new BigDecimal("100")), 
        new Category("6167fce9a2a7173c209d85d2", "user", "Salary", "income"), 
        "account");
    
    Checkpoint saveCheckpoint = context.checkpoint();
    Checkpoint retrieveCheckpoint = context.checkpoint();
    
    context.verify(() -> {
        repository.saveOperation(operation)
            .compose(saved -> {
                Assertions.assertNotNull(saved.id());
                saveCheckpoint.flag();
                return Future.succeededFuture(saved.id());
            })
            .compose(id -> repository.findOperationById(id))
            .compose(result -> {
                Assertions.assertTrue(result.isPresent());
                Operation retrieved = result.get();
                Assertions.assertNotNull(retrieved.category());
                Assertions.assertEquals(operation.category().name(), retrieved.category().name());
                Assertions.assertEquals(operation.dateTime(), retrieved.dateTime());
                Assertions.assertEquals(operation.category().id(), retrieved.category().id());
                retrieveCheckpoint.flag();
                return Future.succeededFuture(retrieved.accountId());
            })
            .compose(id -> repository.removeOperation(id))
            .onFailure(err -> context.failNow(err))
            .onSuccess(deleted -> {
                Assertions.assertTrue(deleted);
                context.completeNow();
            });
    });
    }

    
}
