package net.yurimednikov.vertxbook.cashx.repositories;

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
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(VertxExtension.class)
@Testcontainers
class CategoryMongoRepositoryImplTest {
    
    private CategoryMongoRepositoryImpl repository;

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
        repository = new CategoryMongoRepositoryImpl(client);
        context.completeNow();
    }

    @Test
    void createCategoryTest(Vertx vertx, VertxTestContext context){
        Category category = new Category(null, "testuser", "Salary", "income");
        context.verify(() -> {
            repository.saveCategory(category)
            .onSuccess(result -> {
                System.out.println(result.id());
                Assertions.assertNotNull(result.id());
                context.completeNow();
            })
            .onFailure(err -> context.failNow(err));
        });
    }

    @Test
    void removeCategoryTest(Vertx vertx, VertxTestContext context){
        Category category = new Category(null, "testuser", "Salary", "income");
        Checkpoint createCheckpoint = context.checkpoint();
        context.verify(() -> {
            repository.saveCategory(category)
            .compose(result -> {
                String id = result.id();
                Assertions.assertNotNull(id);
                createCheckpoint.flag();
                return Future.succeededFuture(id);
            })
            .compose(id -> repository.removeCategory(id))
            .onSuccess(result -> {
                Assertions.assertTrue(result);
                context.completeNow();
            })
            .onFailure(err -> context.failNow(err));
        });
    }

    @Test
    void findCategoryByIdTest(Vertx vertx, VertxTestContext context){
        Category category = new Category(null, "testuser", "Salary", "income");
        Checkpoint createCheckpoint = context.checkpoint();
        Checkpoint retrieveCheckpoint = context.checkpoint();
        context.verify(() -> {
            repository.saveCategory(category)
            .compose(result -> {
                String id = result.id();
                Assertions.assertNotNull(id);
                createCheckpoint.flag();
                return Future.succeededFuture(id);
            })
            .compose(id -> repository.findCategoryById(id))
            .compose(result -> {
                Assertions.assertTrue(result.isPresent());
                Category saved = result.get();
                Assertions.assertEquals("testuser", saved.userId());
                Assertions.assertEquals("Salary", saved.name());
                Assertions.assertEquals("income", saved.type());
                retrieveCheckpoint.flag();
                String id = saved.id();
                return Future.succeededFuture(id);
            })
            .compose(id -> repository.removeCategory(id))
            .onSuccess(result -> {
                Assertions.assertTrue(result);
                context.completeNow();
            })
            .onFailure(err -> context.failNow(err));
        });
    }
    
    @Test
    void findCategoriesForUserTest(Vertx vertx, VertxTestContext context){
        String userId = "user";
        context.verify(() -> {
            repository.findCategories(userId)
            .onSuccess(list -> {
                Assertions.assertNotEquals(0, list.categories());
                context.completeNow();
            })
            .onFailure(err -> context.failNow(err));
        });
    }

    @Test
    void updateCategoryTest(Vertx vertx, VertxTestContext context){
        Category category = new Category(null, "testuser", "Salary", "income");
        Checkpoint createCheckpoint = context.checkpoint();
        Checkpoint retrieveCheckpoint = context.checkpoint();
        Checkpoint updateCheckpoint = context.checkpoint();
        context.verify(() -> {
            repository.saveCategory(category)
            .compose(result -> {
                String id = result.id();
                Assertions.assertNotNull(id);
                createCheckpoint.flag();
                return Future.succeededFuture(id);
            })
            .compose(id -> repository.findCategoryById(id))
            .compose(result -> {
                Assertions.assertTrue(result.isPresent());
                Category saved = result.get();
                Assertions.assertEquals("testuser", saved.userId());
                Assertions.assertEquals("Salary", saved.name());
                Assertions.assertEquals("income", saved.type());
                retrieveCheckpoint.flag();
                return Future.succeededFuture(saved);
            })
            .compose(old -> {
                Category update = new Category(old.id(), old.userId(), "Income from stocks", old.type());
                return repository.updateCategory(update);
            })
            .compose(updated -> {
                Assertions.assertEquals("Income from stocks", updated.name());
                updateCheckpoint.flag();
                return Future.succeededFuture(updated.id());
            })
            .compose(id -> repository.removeCategory(id))
            .onSuccess(result -> {
                Assertions.assertTrue(result);
                context.completeNow();
            })
            .onFailure(err -> context.failNow(err));
        });
    }
}
