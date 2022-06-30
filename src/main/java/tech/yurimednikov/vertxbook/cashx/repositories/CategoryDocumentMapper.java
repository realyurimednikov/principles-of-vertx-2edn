package tech.yurimednikov.vertxbook.cashx.repositories;

import java.util.function.Function;

import io.vertx.core.json.JsonObject;
import tech.yurimednikov.vertxbook.cashx.models.Category;

class CategoryDocumentMapper implements Function<JsonObject, Category> {

    @Override
    public Category apply(JsonObject document) {
        return new Category(
                    document.getString("_id"), 
                    document.getString("userId"), 
                    document.getString("name"), 
                    document.getString("type"));
    }
    
}
