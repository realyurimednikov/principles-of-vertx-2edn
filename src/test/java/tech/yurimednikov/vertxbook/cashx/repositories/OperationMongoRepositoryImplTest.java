package tech.yurimednikov.vertxbook.cashx.repositories;

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
import tech.yurimednikov.vertxbook.cashx.models.Category;
import tech.yurimednikov.vertxbook.cashx.models.Operation;
import tech.yurimednikov.vertxbook.cashx.models.OperationAmount;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(VertxExtension.class)
@Testcontainers
class OperationMongoRepositoryImplTest {

    private OperationMongoRepositoryImpl repository;
    private CategoryMongoRepositoryImpl categoryRepository;
    private Category category;

    @Container
    private MongoDBContainer mongoContainer = new MongoDBContainer(DockerImageName.parse("mongo:focal"));

    @BeforeEach
    void setup(Vertx vertx, VertxTestContext context){
        JsonObject config = new JsonObject();
        String dbHost = mongoContainer.getHost();
        Integer dbPort = mongoContainer.getFirstMappedPort();

        config.put("port", dbPort);
        config.put("host", dbHost);

        MongoClient client = MongoClient.createShared(vertx, config);
        repository = new OperationMongoRepositoryImpl(client);
        categoryRepository = new CategoryMongoRepositoryImpl(client);

        client.createCollection("categories")
                .compose(r -> {
                    Category c = new Category(null, "user", "Income from selling stuff", "income");
                    return categoryRepository.saveCategory(c);
                }).onSuccess(result -> {
                    this.category = result;
                    context.completeNow();
                }).onFailure(context::failNow);
    }
    
    @Test
    void createOperationTest(Vertx vertx, VertxTestContext context){
        Operation operation = new Operation(null,
            "user", "Just an operation", LocalDateTime.now(), 
            new OperationAmount("EUR", new BigDecimal("100")),
            this.category,
            "account");

        context.verify(() -> {
            repository.saveOperation(operation)
                .onSuccess(result -> {
                    Assertions.assertNotNull(result.id());
                    context.completeNow();
                })
                .onFailure(context::failNow);
        });
    }

    @Test
    void removeOperationTest(Vertx vertx, VertxTestContext context){
        Operation operation = new Operation(null, 
            "user", "Just an operation", LocalDateTime.now(), 
            new OperationAmount("EUR", new BigDecimal("100")), 
            this.category,
            "account");
        
        Checkpoint saveCheckpoint = context.checkpoint();
        
        context.verify(() -> {
            repository.saveOperation(operation)
                .map(saved -> {
                    Assertions.assertNotNull(saved.id());
                    saveCheckpoint.flag();
                    return saved.id();
                })
                .compose(id -> repository.removeOperation(id))
                .onFailure(context::failNow)
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
        this.category,
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
            .onFailure(context::failNow)
            .onSuccess(deleted -> {
                Assertions.assertTrue(deleted);
                context.completeNow();
            });
    });
    }

    
}
