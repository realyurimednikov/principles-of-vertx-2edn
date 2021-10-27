package net.yurimednikov.vertxbook.cashx.repositories;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import net.yurimednikov.vertxbook.cashx.models.PaymentCategory;
import net.yurimednikov.vertxbook.cashx.models.PaymentCategoryList;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PaymentCategoryReactivePgRepositoryImpl implements  PaymentCategoryRepository{

    private final SqlClient client;
    private final PaymentCategoryRowMapper mapper;
    private final Vertx vertx;

    public PaymentCategoryReactivePgRepositoryImpl(Vertx vertx, SqlClient client) {
        this.client = client;
        this.mapper = new PaymentCategoryRowMapper();
        this.vertx = vertx;
    }

    public Future<Void> createTable(){
//        String createTableQuery = """
//                CREATE TABLE "payment_categories" (
//                category_id serial PRIMARY KEY,
//                 category_name varchar(200) NOT NULL,
//                 category_type varchar(3) NOT NULL,
//                 category_userid INTEGER
//                 );
//                """;
//
//        return client.query(createTableQuery).execute().compose(r -> Future.succeededFuture());
        return vertx.fileSystem().readFile("sql/payment_categories.sql")
                .map(Buffer::toString)
                .compose(query -> client.query(query).execute())
                .compose(result -> Future.succeededFuture());
    }

    @Override
    public Future<PaymentCategory> savePaymentCategory(PaymentCategory category) {
        String sql = """
        INSERT INTO payment_categories (category_name, category_type, category_userid)
         VALUES ($1, $2, $3)
          RETURNING category_id;
        """;
        Tuple params = Tuple.of(category.getName(), category.getType(), category.getUserId());
        return client.preparedQuery(sql).execute(params).compose(row -> {
            if (row.rowCount() == 0) return Future.failedFuture(new RuntimeException());
            Row r = row.iterator().next();
            long id = r.getLong("category_id");
            return Future.succeededFuture(PaymentCategory.withId(id, category));
        });
    }

    @Override
    public Future<Optional<PaymentCategory>> findCategoryById(long id) {
        String sql = "SELECT * FROM payment_categories WHERE category_id = $1;";
        Tuple params = Tuple.of(id);
        return client.preparedQuery(sql)
                .mapping(mapper)
                .execute(params)
                .map(rows -> {
                    if (rows.rowCount() == 0) return Optional.empty();
                    return Optional.of(rows.iterator().next());
                });
    }

    @Override
    public Future<PaymentCategoryList> findAll(long userId) {
        String sql = "SELECT * FROM payment_categories WHERE category_userid = $1 ORDER BY category_id;";
        Tuple params = Tuple.of(userId);
        return client.preparedQuery(sql)
                .mapping(mapper)
                .execute(params)
                .map(rows -> StreamSupport.stream(rows.spliterator(), false).collect(Collectors.toList()))
                .map(PaymentCategoryList::new);
    }

    @Override
    public Future<Boolean> removePaymentCategory(long id) {
        String sql = "DELETE FROM payment_categories WHERE category_id = $1 RETURNING category_id;";
        Tuple params = Tuple.of(id);
        return client.preparedQuery(sql).execute(params).map(rows -> rows.rowCount() != 0);
    }
}
