package net.yurimednikov.vertxbook.cashx.repositories;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;
import net.yurimednikov.vertxbook.cashx.models.Account;
import net.yurimednikov.vertxbook.cashx.models.PaymentCategory;
import net.yurimednikov.vertxbook.cashx.models.PaymentOperation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(VertxExtension.class)
@Testcontainers
class PaymentOperationReactivePgRepositoryImplTest {

    private PaymentOperationReactivePgRepositoryImpl operationRepository;
    private SqlClient client;

    @Container
    private PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:11-alpine")
            .withDatabaseName("cashxdb").withUsername("user").withPassword("secret");

    @BeforeEach
    void setup (Vertx vertx, VertxTestContext context) {
        int port = container.getFirstMappedPort();
        String uri = "postgresql://user:secret@localhost:" + port + "/cashxdb";
        client = PgPool.client(vertx, uri);
        operationRepository = new PaymentOperationReactivePgRepositoryImpl(vertx, client);
        operationRepository.createTable().onSuccess(r -> context.completeNow()).onFailure(context::failNow);
    }

    @Test
    void createOperationTest(Vertx vertx, VertxTestContext context){
        Account account = new Account(1, "My bank account", "EUR", 1);
        PaymentCategory category = new PaymentCategory(1, 1, "Software development", "inc");
        PaymentOperation operation = new PaymentOperation(0, 1, "Lorem ipsum", account, category,
                BigDecimal.valueOf(100), "EUR", LocalDate.now());
        context.verify(() -> {
           operationRepository.createOperation(operation)
                   .onFailure(context::failNow)
                   .onSuccess(result -> {
                       Assertions.assertNotEquals(0, result.getOperationId());
                       context.completeNow();
                   });
        });
    }

    @Test
    void findOperationByIdTest(Vertx vertx, VertxTestContext context){
        Checkpoint preparationCheckpoint = context.checkpoint();
        Checkpoint createCheckpoint = context.checkpoint();
        Checkpoint retrieveCheckpoint = context.checkpoint();

        Account account = new Account(1, "My bank account", "EUR", 1);
        PaymentCategory category = new PaymentCategory(1, 1, "Software development", "inc");

        PaymentCategoryReactivePgRepositoryImpl categoryRepository = new PaymentCategoryReactivePgRepositoryImpl(vertx, client);
        AccountReactivePgRepositoryImpl accountRepository = new AccountReactivePgRepositoryImpl(vertx, client);

        Future<Account> createAccountFuture = accountRepository.createTable()
                .compose(r -> accountRepository.saveAccount(account));
        Future<PaymentCategory> createCategoryFuture = categoryRepository.createTable()
                .compose(r -> categoryRepository.savePaymentCategory(category));

        Future<CompositeFuture> preparationFuture = CompositeFuture.join(createAccountFuture, createCategoryFuture);

        context.verify(() -> {
            preparationFuture.compose(result -> {
                Account ar = (Account) result.resultAt(0);
                PaymentCategory cr = (PaymentCategory) result.resultAt(1);
                Assertions.assertNotNull(ar);
                Assertions.assertNotNull(cr);
                preparationCheckpoint.flag();
                PaymentOperation operation = new PaymentOperation(0, 1, "Lorem ipsum", ar, cr,
                        BigDecimal.valueOf(100), "EUR", LocalDate.now());
                return operationRepository.createOperation(operation);
            }).map(operation -> {
                Assertions.assertNotEquals(0, operation.getOperationId());
                createCheckpoint.flag();
                return operation.getOperationId();
            }).compose(id -> operationRepository.findOperationById(id))
                    .onFailure(context::failNow)
                    .onSuccess(result -> {
                        Assertions.assertTrue(result.isPresent());
                        retrieveCheckpoint.flag();
                        context.completeNow();
                    });
        });

    }
}
