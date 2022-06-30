package tech.yurimednikov.vertxbook.cashx.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Function;

import io.vertx.core.json.JsonObject;
import tech.yurimednikov.vertxbook.cashx.models.Category;
import tech.yurimednikov.vertxbook.cashx.models.Operation;
import tech.yurimednikov.vertxbook.cashx.models.OperationAmount;

class OperationDocumentMapper implements Function<JsonObject, Operation> {

    @Override
    public Operation apply(JsonObject json) {
        String id = json.getString("_id");
        String userId = json.getString("userId");
        String name = json.getString("name");
        JsonObject amount = json.getJsonObject("amount");
        OperationAmount operationAmount = new OperationAmount(amount.getString("currency"),
            new BigDecimal(amount.getString("value")));
        String dt = json.getString("dateTime");
        LocalDateTime dateTime = LocalDateTime.parse(dt);
        String accountId = json.getString("accountId");
        // deal with category
        Category category = null;
        if (json.getJsonArray("category") != null) {
            JsonObject rawCategory = json.getJsonArray("category").getJsonObject(0);
            CategoryDocumentMapper categoryMapper  = new CategoryDocumentMapper();
            // JsonObject c = json.getJsonObject("category");
            category = categoryMapper.apply(rawCategory);
        } else {
            if (json.getString("categoryId") != null) {
                String categoryId = json.getString("categoryId");
                category = new Category(categoryId, userId, null, null);
            }
        }
        return new Operation(id, userId, name, dateTime, operationAmount, category, accountId);
    }
    
}
