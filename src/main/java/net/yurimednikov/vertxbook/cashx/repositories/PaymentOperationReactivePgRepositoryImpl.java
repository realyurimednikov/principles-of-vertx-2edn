package net.yurimednikov.vertxbook.cashx.repositories;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import net.yurimednikov.vertxbook.cashx.models.PaymentCategoryList;
import net.yurimednikov.vertxbook.cashx.models.PaymentOperation;
import net.yurimednikov.vertxbook.cashx.models.PaymentOperationList;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PaymentOperationReactivePgRepositoryImpl implements PaymentOperationRepository{

    private final Vertx vertx;
    private final SqlClient client;
    private final PaymentOperationRowMapper mapper;

    public PaymentOperationReactivePgRepositoryImpl(Vertx vertx, SqlClient client){
        this.vertx = vertx;
        this.client = client;
        this.mapper = new PaymentOperationRowMapper();
    }

    public Future<Void> createTable(){
        return vertx.fileSystem().readFile("sql/payment_operations.sql")
                .map(Buffer::toString)
                .compose(query -> client.query(query).execute())
                .compose(result -> Future.succeededFuture());
    }

    @Override
    public Future<PaymentOperation> createOperation(PaymentOperation operation) {
        String sql = """
        INSERT INTO payment_operations (operation_name,
         operation_userid, operation_accountid, operation_categoryid,
         operation_amount, operation_currency, operation_date)
         VALUES ($1, $2, $3, $4, $5, $6, $7)
          RETURNING operation_id;
        """;
        Tuple params = Tuple.of(operation.getName(), operation.getUserId(), operation.getAccount().getId(),
                operation.getCategory().getCategoryId(), operation.getAmount(), operation.getCurrency(),
                operation.getDate());
        return client.preparedQuery(sql).execute(params).compose(row -> {
            if (row.rowCount() == 0) return Future.failedFuture(new RuntimeException());
            Row r = row.iterator().next();
            long id = r.getLong("operation_id");
            return Future.succeededFuture(PaymentOperation.withId(id, operation));
        });
    }

    @Override
    public Future<Optional<PaymentOperation>> findOperationById(long id) {
        String sql = """
        SELECT
         payment_operations.operation_name as operation_name,
         payment_operations.operation_id as operation_id,
         payment_operations.operation_userid as operation_userid,
         payment_operations.operation_date as operation_date,
         payment_operations.operation_amount as operation_amount,
         payment_operations.operation_currency as operation_currency,
         accounts.account_id as account_id,
         accounts.account_name as account_name,
         accounts.account_currency as account_currency,
         accounts.account_userid as account_userid,
         payment_categories.category_id as category_id,
         payment_categories.category_userid as category_userid,
         payment_categories.category_name as category_name,
         payment_categories.category_type as category_type
         FROM payment_operations
         INNER JOIN payment_categories ON payment_categories.category_id = payment_operations.operation_categoryid
         INNER JOIN accounts ON accounts.account_id = payment_operations.operation_accountid
         WHERE payment_operations.operation_id = $1;
        """;
        Tuple params = Tuple.of(id);
        return client.preparedQuery(sql).mapping(mapper).execute(params).map(rows -> {
            if (rows.rowCount() == 0) return Optional.empty();
            PaymentOperation operation = rows.iterator().next();
            return Optional.of(operation);
        });
    }

    @Override
    public Future<PaymentOperationList> findAll(long userId) {
        String sql = """
        SELECT
         payment_operations.operation_name as operation_name,
         payment_operations.operation_id as operation_id,
         payment_operations.operation_userid as operation_userid,
         payment_operations.operation_date as operation_date,
         payment_operations.operation_amount as operation_amount,
         payment_operations.operation_currency as operation_currency,
         accounts.account_id as account_id,
         accounts.account_name as account_name,
         accounts.account_currency as account_currency,
         accounts.account_userid as account_userid,
         payment_categories.category_id as category_id,
         payment_categories.category_userid as category_userid,
         payment_categories.category_name as category_name,
         payment_categories.category_type as category_type
         FROM payment_operations
         INNER JOIN payment_categories ON payment_categories.category_id = payment_operations.operation_categoryid
         INNER JOIN accounts ON accounts.account_id = payment_operations.operation_accountid
         WHERE payment_operations.operation_userid = $1
         ORDER BY payment_operations.operation_date DESC;
        """;
        Tuple params = Tuple.of(userId);
        return client.preparedQuery(sql)
                .mapping(mapper)
                .execute(params)
                .map(rows -> StreamSupport.stream(rows.spliterator(), false).collect(Collectors.toList()))
                .map(PaymentOperationList::new);
    }

    @Override
    public Future<Boolean> removeOperation(long id) {
        String sql = "DELETE FROM payment_operations WHERE operation_id = $1 RETURNING operation_id;";
        Tuple params = Tuple.of(id);
        return client.preparedQuery(sql).execute(params).map(rows -> rows.rowCount() != 0);
    }
}
