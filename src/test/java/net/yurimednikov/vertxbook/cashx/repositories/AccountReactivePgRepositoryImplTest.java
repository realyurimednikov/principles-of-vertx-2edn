package net.yurimednikov.vertxbook.cashx.repositories;


import java.util.ArrayList;
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
         repository = new AccountReactivePgRepositoryImpl(vertx, client);
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
            result.onFailure(context::failNow);
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
            .onFailure(context::failNow);
        });
    }

    @Test
    void findAccountByIdTest(Vertx vertx, VertxTestContext context){
        Account data = new Account(0, "My bank account", "EUR", 1);
        Checkpoint savedCheckpoint = context.checkpoint();
        Checkpoint retrievedCheckpoint = context.checkpoint();
        context.verify(() -> {
            repository.saveAccount(data)
            .map(saved -> {
                long id = saved.getId();
                savedCheckpoint.flag();
                return id;
            })
            .compose(id -> repository.findAccountById(id))
            .onSuccess(account -> {
                retrievedCheckpoint.flag();
                Assertions.assertTrue(account.isPresent());
                context.completeNow();
            })
            .onFailure(context::failNow);
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
        .map(saved -> {
            long id = saved.getId();
            Assertions.assertNotEquals(0, id);
            savedCheckpoint.flag();
            return id;
        })
        .compose(id -> repository.findAccountById(id))
        .map(account -> {
            Assertions.assertTrue(account.isPresent());
            retrievedCheckpoint.flag();
            return account.get().getId();
        })
        .compose(id -> repository.removeAccount(id))
        .onSuccess(result -> {
            Assertions.assertTrue(result);
            deletedCheckpoint.flag();
            context.completeNow();
        })
        .onFailure(context::failNow);
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
            .map(saved -> {
                long id = saved.getId();
                Assertions.assertNotEquals(0, id);
                createCheckpoint.flag();
                return saved;
            })
            .map(account -> {
                return new Account(account.getId(), "My wise account", "CHF", account.getUserId());
            })
            .compose(account -> repository.updateAccount(account))
            .map(result -> {
                updateCheckpoint.flag();
                return result.getId();
            })
            .compose(id -> repository.findAccountById(id))
            .map(retrieved -> {
                Assertions.assertTrue(retrieved.isPresent());
                Account account = retrieved.get();
                Assertions.assertEquals("CHF", account.getCurrency());
                Assertions.assertEquals("My wise account", account.getName());
                retrieveCheckpoint.flag();
                return account.getId();
            })
            .compose(id -> repository.removeAccount(id))
            .onSuccess(deleted -> {
                Assertions.assertTrue(deleted);
                deleteCheckpoint.flag();
                context.completeNow();
            })
            .onFailure(context::failNow);
        });
   }

   @Test
    void findAndPaginateTest(Vertx vertx, VertxTestContext context){
        Checkpoint saveCheckpoint = context.checkpoint();

        List<Account> accounts = new ArrayList<>();
        long userId = 1;

        for (int i = 0; i<51; i ++) {
            Account account = new Account(0, "Account " + i, "EUR", userId);
            accounts.add(account);
        }

        context.verify(() -> {
            repository.saveManyAccounts(new AccountList(accounts))
                    .compose(result -> {
                        Assertions.assertEquals(51, result.getAccounts().size());
                        saveCheckpoint.flag();
                        return repository.findAndPaginate(userId, 4, 10);
                    })
                    .onFailure(context::failNow)
                    .onSuccess(result -> {
                        Assertions.assertEquals(51, result.getTotal());
                        Assertions.assertEquals(4, result.getCurrentPage());
                        Assertions.assertEquals(6, result.getNumberOfPages());
                        Assertions.assertEquals(10, result.getAccounts().size());
                        context.completeNow();
                    });
        });
   }
}
