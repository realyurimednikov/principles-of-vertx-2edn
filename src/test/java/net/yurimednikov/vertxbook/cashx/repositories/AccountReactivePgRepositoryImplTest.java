package net.yurimednikov.vertxbook.cashx.repositories;


import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;
import net.yurimednikov.vertxbook.cashx.models.Account;
import net.yurimednikov.vertxbook.cashx.models.AccountList;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(VertxExtension.class)
class AccountReactivePgRepositoryImplTest {

    private AccountReactivePgRepositoryImpl repository;

    @Container
    private PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:11-alpine")
            .withDatabaseName("cashxdb").withUsername("user").withPassword("secret");

    @BeforeEach
    void setup (Vertx vertx, VertxTestContext context) {
         int port = container.getFirstMappedPort();
         String uri = "postgresql://user:secret@localhost:" + port + "/cashxdb";
         SqlClient client = PgPool.client(vertx, uri);
         repository = new AccountReactivePgRepositoryImpl(client);
         repository.createTable().onSuccess(r -> context.completeNow()).onFailure(context::failNow);
    }

    @Test
    void containerIsRunningTest(){
        Assertions.assertTrue(container.isRunning());
    }

    @Test
    void createAccountTest(Vertx vertx, VertxTestContext context){
        Account data = new Account(0, "My bank account", "EUR", 1);
        context.verify(() -> {
            Future<Account> result = repository.saveAccount(data);
            result.onFailure(err -> context.failNow(err));
            result.onSuccess(account -> {
                Assertions.assertNotEquals(0, account.getId());
                context.completeNow();
            });
        });
    }

    @Test
    void createManyAccountsTest(Vertx vertx, VertxTestContext context){
        List<Account> accounts = List.of(
            new Account(0, "My paypal account", "RSD", 3),
            new Account(0, "My stripe account", "AUD", 3),
            new Account(0, "My credit card account", "GBP", 3),
            new Account(0, "My wise account", "SEK", 3),
            new Account(0, "My revolut account", "USD", 3)
        );
        AccountList accountList = new AccountList(accounts);

        Checkpoint savedCheckpoint = context.checkpoint();

        context.verify(() -> {
            repository.saveManyAccounts(accountList)
            .onSuccess(result -> {
                savedCheckpoint.flag();
                Assertions.assertEquals(accounts.size(), result.getAccounts().size());
                context.completeNow(); 
            })
            .onFailure(err -> context.failNow(err));
        });
    }

    @Test
    void findAccountByIdTest(Vertx vertx, VertxTestContext context){
        Account data = new Account(0, "My bank account", "EUR", 1);
        Checkpoint savedCheckpoint = context.checkpoint();
        Checkpoint retrievedCheckpoint = context.checkpoint();
        context.verify(() -> {
            repository.saveAccount(data)
            .compose(saved -> {
                long id = saved.getId();
                savedCheckpoint.flag();
                return Future.succeededFuture(id);
            })
            .compose(id -> repository.findAccountById(id))
            .onSuccess(account -> {
                retrievedCheckpoint.flag();
                Assertions.assertTrue(account.isPresent());
                context.completeNow();
            })
            .onFailure(err -> context.failNow(err));
        });
        
    }

   @Test
   void removeAccountTest(Vertx vertx, VertxTestContext context){
    Account data = new Account(0, "My bank account", "EUR", 1);
    Checkpoint savedCheckpoint = context.checkpoint();
    Checkpoint retrievedCheckpoint = context.checkpoint();
    Checkpoint deletedCheckpoint = context.checkpoint();
    context.verify(() -> {
        repository.saveAccount(data)
        .compose(saved -> {
            long id = saved.getId();
            Assertions.assertNotEquals(0, id);
            savedCheckpoint.flag();
            return Future.succeededFuture(id);
        })
        .compose(id -> repository.findAccountById(id))
        .compose(account -> {
            Assertions.assertTrue(account.isPresent());
            retrievedCheckpoint.flag();
            return Future.succeededFuture(account.get().getId());
        })
        .compose(id -> repository.removeAccount(id))
        .onSuccess(result -> {
            Assertions.assertTrue(result);
            deletedCheckpoint.flag();
            context.completeNow();
        })
        .onFailure(err -> context.failNow(err));
    });
   } 

   @Test
   void updateAccountTest(Vertx vertx, VertxTestContext context){
        Account initial = new Account(0, "My bank account", "EUR", 1);
        Checkpoint createCheckpoint = context.checkpoint();
        Checkpoint updateCheckpoint = context.checkpoint();
        Checkpoint retrieveCheckpoint = context.checkpoint();
        Checkpoint deleteCheckpoint = context.checkpoint();
        context.verify(() -> {
            repository.saveAccount(initial)
            .compose(saved -> {
                long id = saved.getId();
                Assertions.assertNotEquals(0, id);
                createCheckpoint.flag();
                return Future.succeededFuture(saved);
            })
            .compose(account -> {
                Account modified = new Account(account.getId(), "My wise account", "CHF", account.getUserId());
                return Future.succeededFuture(modified);
            })
            .compose(account -> repository.updateAccount(account))
            .compose(result -> {
                updateCheckpoint.flag();
                return Future.succeededFuture(result.getId());
            })
            .compose(id -> repository.findAccountById(id))
            .compose(retrieved -> {
                Assertions.assertTrue(retrieved.isPresent());
                retrieveCheckpoint.flag();
                Account account = retrieved.get();
                Assertions.assertEquals("CHF", account.getCurrency());
                Assertions.assertEquals("My wise account", account.getName());
                return Future.succeededFuture(account.getId());
            })
            .compose(id -> repository.removeAccount(id))
            .onSuccess(deleted -> {
                Assertions.assertTrue(deleted);
                deleteCheckpoint.flag();
                context.completeNow();
            })
            .onFailure(err -> context.failNow(err));
        });
   }
}
