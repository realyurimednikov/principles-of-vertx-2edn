package net.yurimednikov.vertxbook.cashx.repositories;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;
import net.yurimednikov.vertxbook.cashx.models.PaymentCategory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(VertxExtension.class)
class PaymentCategoryReactivePgRepositoryImplTest {

    private PaymentCategoryReactivePgRepositoryImpl repository;

    @Container
    private PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:11-alpine")
            .withDatabaseName("cashxdb").withUsername("user").withPassword("secret");

    @BeforeEach
    void setup (Vertx vertx, VertxTestContext context) {
        int port = container.getFirstMappedPort();
        String uri = "postgresql://user:secret@localhost:" + port + "/cashxdb";
        SqlClient client = PgPool.client(vertx, uri);
        repository = new PaymentCategoryReactivePgRepositoryImpl(client);
        repository.createTable().onSuccess(r -> context.completeNow()).onFailure(context::failNow);
    }

    @Test
    void savePaymentCategoryTest(Vertx vertx, VertxTestContext context){
        PaymentCategory category = new PaymentCategory(0, 1, "Software development", "inc");
        context.verify(() -> {
            repository.savePaymentCategory(category)
                    .onFailure(context::failNow)
                    .onSuccess(result -> {
                        Assertions.assertNotEquals(0, result.getCategoryId());
                        context.completeNow();
                    });
        });
    }

    @Test
    void findCategoryByIdSuccessTest(Vertx vertx, VertxTestContext context){
        Checkpoint saveCheckpoint = context.checkpoint();
        Checkpoint findCheckpoint = context.checkpoint();
        PaymentCategory category = new PaymentCategory(0, 1, "Software development", "inc");
        context.verify(() -> {
            repository.savePaymentCategory(category).map(result -> {
                Assertions.assertNotEquals(0, result.getCategoryId());
                saveCheckpoint.flag();
                return result.getCategoryId();
            }).compose(id -> repository.findCategoryById(id)).onFailure(context::failNow)
                    .onSuccess(result -> {
                        Assertions.assertTrue(result.isPresent());
                        findCheckpoint.flag();
                        context.completeNow();
                    });

        });
    }


}
