package net.yurimednikov.vertxbook.cashx.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import net.yurimednikov.vertxbook.cashx.models.Account;
import net.yurimednikov.vertxbook.cashx.models.AccountList;
import net.yurimednikov.vertxbook.cashx.models.PagedAccountList;

public class AccountReactivePgRepositoryImpl implements AccountRepository{

    private final SqlClient client;
    private final Vertx vertx;

    public AccountReactivePgRepositoryImpl(Vertx vertx, SqlClient client){
        this.client = client;
        this.vertx = vertx;
    }

    public Future<Void> createTable(){
        return vertx.fileSystem().readFile("sql/accounts.sql")
                .map(Buffer::toString)
                .compose(query -> client.query(query).execute())
                .compose(result -> Future.succeededFuture());
    }

    @Override
    public Future<Account> saveAccount(Account account) {
        // 1. Create prepared statement
        String sql = "INSERT INTO accounts (account_name, account_currency, account_userid) VALUES ($1, $2, $3) RETURNING account_id;";
        // 2. Create tuple
        Tuple tuple = Tuple.of(account.getName(), account.getCurrency(), account.getUserId());

        // 3. Run query
        Future<Account> result = client
            .preparedQuery(sql)
            .execute(tuple)
            .flatMap(rows -> {
                // 4. Assert results
                if (rows.rowCount() == 0) {
                    return Future.failedFuture(new RuntimeException());
                }
                // 5. Return an account with generated id
                Row first = rows.iterator().next();
                long id = first.getLong("account_id");
                return Future.succeededFuture(Account.withId(id, account));
            });        
        return result;
    }

    @Override
    public Future<Optional<Account>> findAccountById(Long accountId) {
        AccountRowMapper mapper = new AccountRowMapper();
        // Create a prepared statement
        String sql = "SELECT * FROM accounts WHERE account_id = $1";
        // Set up a tuple
        Tuple tuple = Tuple.of(accountId);
        // Run query
        return client.preparedQuery(sql)
            .mapping(mapper)
            .execute(tuple)
            .map(rows -> {
                // Assert results
                if (rows.rowCount() == 0) {
                    return Optional.empty();
                }
                Account account = rows.iterator().next();
                return Optional.of(account);
            });
    }

    @Override
    public Future<AccountList> findAccounts(Long userId) {
        // 1. Create prepared statement
        String sql = "SELECT * FROM accounts WHERE account_userid = $1 ORDER BY account_id;";

        // 2, Set up tuple
        Tuple tuple = Tuple.of(userId);

        AccountRowMapper rowMapper = new AccountRowMapper();

        // 3. Run query
        return client.preparedQuery(sql)
            .mapping(rowMapper)
            .execute(tuple)
            .map(rows -> StreamSupport.stream(rows.spliterator(), false).collect(Collectors.toList()))
                .map(AccountList::new);
    }

    @Override
    public Future<Boolean> removeAccount(Long accountId) {
        // 1. Create prepared statement
        String sql = "DELETE FROM accounts WHERE account_id = $1 RETURNING account_id;";

        // 2, Set up tuple
        Tuple tuple = Tuple.of(accountId);
        
        // 3. run query
        return client.preparedQuery(sql)
            .execute(tuple)
            .map(rows ->rows.rowCount() != 0);
    }

    @Override
    public Future<Account> updateAccount(Account account) {
        // Create statement
        String sql = "UPDATE accounts SET account_name = $1, account_currency = $2 WHERE account_id = $3;";

        // Set up tuple
        Tuple tuple = Tuple.of(account.getName(), account.getCurrency(), account.getId());

        // run query
        return client.preparedQuery(sql).execute(tuple).flatMap(rows -> {
            if (rows.rowCount() == 1) {
                return Future.succeededFuture(account);
            } else {
                return Future.failedFuture(new RuntimeException());
            }
        });
    }

    @Override
    public Future<AccountList> saveManyAccounts(AccountList accounts) {
        // Prepare a statement
        String sql = """
        INSERT INTO accounts (account_name, account_currency, account_userid) 
        VALUES ($1, $2, $3) RETURNING account_name, account_currency, account_userid, account_id;
        """;
        // Prepare a list of tuples with params
        List<Tuple> tuples = accounts.getAccounts()
            .stream()
            .map(account ->Tuple.of(account.getName(), account.getCurrency(), account.getUserId()))
            .collect(Collectors.toList());
        // Create a mapper
        AccountRowMapper mapper = new AccountRowMapper();
        // Execute query
        return client.preparedQuery(sql).mapping(mapper).executeBatch(tuples)
            .map(rows -> {
                // 4. Assert results and map rows
                List<Account> items = new ArrayList<>();
                rows.iterator().forEachRemaining(items::add);
                return items;
            })
            .map(AccountList::new);
    }

    @Override
    public Future<PagedAccountList> findAndPaginate(Long userId, Integer page, Integer limit) {
        return findAccounts(userId).map(AccountList::getAccounts).map(list -> {
            int totalAccounts = list.size();
            int start = (page - 1) * limit;
            int numberOfPages =(totalAccounts / limit) + 1;
            List<Account> accounts = list.stream().skip(start).limit(limit).collect(Collectors.toList());
            return new PagedAccountList(accounts, numberOfPages, page, totalAccounts);
        });
    }

}
