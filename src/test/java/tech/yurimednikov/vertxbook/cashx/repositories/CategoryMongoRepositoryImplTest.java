package tech.yurimednikov.vertxbook.cashx.repositories;

import tech.yurimednikov.vertxbook.cashx.models.CategoryList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

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
                Assertions.assertNotNull(result.id());
                context.completeNow();
            })
            .onFailure(context::failNow);
        });
    }

    @Test
    void removeCategoryTest(Vertx vertx, VertxTestContext context){
        Category category = new Category(null, "testuser", "Salary", "income");
        Checkpoint createCheckpoint = context.checkpoint();
        context.verify(() -> {
            repository.saveCategory(category)
            .map(result -> {
                String id = result.id();
                Assertions.assertNotNull(id);
                createCheckpoint.flag();
                return id;
            })
            .compose(id -> repository.removeCategory(id))
            .onSuccess(result -> {
                Assertions.assertTrue(result);
                context.completeNow();
            })
            .onFailure(context::failNow);
        });
    }

    @Test
    void findCategoryByIdTest(Vertx vertx, VertxTestContext context){
        Category category = new Category(null, "testuser", "Salary", "income");
        Checkpoint createCheckpoint = context.checkpoint();
        Checkpoint retrieveCheckpoint = context.checkpoint();
        context.verify(() -> {
            repository.saveCategory(category)
            .map(result -> {
                String id = result.id();
                Assertions.assertNotNull(id);
                createCheckpoint.flag();
                return id;
            })
            .compose(id -> repository.findCategoryById(id))
            .map(result -> {
                Assertions.assertTrue(result.isPresent());
                Category saved = result.get();
                Assertions.assertEquals("testuser", saved.userId());
                Assertions.assertEquals("Salary", saved.name());
                Assertions.assertEquals("income", saved.type());
                retrieveCheckpoint.flag();
                return saved.id();
            })
            .compose(id -> repository.removeCategory(id))
            .onSuccess(result -> {
                Assertions.assertTrue(result);
                context.completeNow();
            })
            .onFailure(context::failNow);
        });
    }
    
    @Test
    void findCategoriesForUserTest(Vertx vertx, VertxTestContext context){
        String userId = "user";
        context.verify(() -> {
            repository.findCategories(userId)
            .onSuccess(list -> {
                Assertions.assertEquals(0, list.categories().size());
                context.completeNow();
            })
            .onFailure(context::failNow);
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
            .map(result -> {
                String id = result.id();
                Assertions.assertNotNull(id);
                createCheckpoint.flag();
                return id;
            })
            .compose(id -> repository.findCategoryById(id))
            .map(result -> {
                Assertions.assertTrue(result.isPresent());
                Category saved = result.get();
                Assertions.assertEquals("testuser", saved.userId());
                Assertions.assertEquals("Salary", saved.name());
                Assertions.assertEquals("income", saved.type());
                retrieveCheckpoint.flag();
                return saved;
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
            .onFailure(context::failNow);
        });
    }

    @Test
    void saveManyCategoriesTest(Vertx vertx, VertxTestContext context){
        Checkpoint saveCheckpoint = context.checkpoint();
        List<Category> categories = new ArrayList<>();
        String userId = "user";
        for (int i=0; i<50; i++){
            Category category = new Category(null, userId, "Salary", "income");
            categories.add(category);
        }
        CategoryList categoryList = new CategoryList(categories);
        context.verify(() -> {
            repository.saveManyCategories(categoryList)
                    .map(result -> {
                        Assertions.assertTrue(result);
                        saveCheckpoint.flag();
                        return result;
                    }).compose(r -> repository.findCategories(userId))
                    .onFailure(context::failNow)
                    .onSuccess(result -> {
                        Assertions.assertEquals(50, result.categories().size());
                        context.completeNow();
                    });
        });
    }
}
