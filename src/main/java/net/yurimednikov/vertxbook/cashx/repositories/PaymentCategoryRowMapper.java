package net.yurimednikov.vertxbook.cashx.repositories;

import io.vertx.sqlclient.Row;
import net.yurimednikov.vertxbook.cashx.models.PaymentCategory;

import java.util.function.Function;

class PaymentCategoryRowMapper implements Function<Row, PaymentCategory> {
    @Override
    public PaymentCategory apply(Row row) {
        long id = row.getLong("category_id");
        long userId = row.getLong("category_userid");
        String name = row.getString("category_name");
        String type = row.getString("category_type");
        return new PaymentCategory(id, userId, name, type);
    }
}
