package net.yurimednikov.vertxbook.cashx.repositories;

import java.util.Optional;
import java.util.stream.Collectors;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import net.yurimednikov.vertxbook.cashx.models.Category;
import net.yurimednikov.vertxbook.cashx.models.CategoryList;

public class CategoryMongoRepositoryImpl implements CategoryRepository {

    private final MongoClient client;
    private final CategoryDocumentMapper mapper;

    public CategoryMongoRepositoryImpl(MongoClient client){
        this.client = client;
        this.mapper = new CategoryDocumentMapper();
    }

    @Override
    public Future<Category> saveCategory(Category category) {
        JsonObject document = new JsonObject();
        document.put("name", category.name());
        document.put("userId", category.userId());
        document.put("type", category.type());
        return client.save("categories", document).flatMap(id -> Future.succeededFuture(Category.withId(id, category)));
    }

    @Override
    public Future<Optional<Category>> findCategoryById(String id) {
        // create a query
        JsonObject query = new JsonObject();
        query.put("_id", id);   
        // assert result
        Future<Optional<Category>> future = client.find("categories", query)
            .flatMap(list -> Future.succeededFuture(list.stream().findFirst()))
            .flatMap(result -> Future.succeededFuture(result.map(mapper::apply)));

        // alternative solution with findOne()
        // NULL fields arg means that all fields would be returned
        client.findOne("categories", query, null)
            .flatMap(document -> Future.succeededFuture(Optional.ofNullable(document)))
            .flatMap(result -> Future.succeededFuture(result.map(mapper::apply)));

        // map document to object
        return future;
    }

    @Override
    public Future<Boolean> removeCategory(String id) {
        JsonObject query = new JsonObject();
        query.put("_id", id);  
        Future<Boolean> future = client.removeDocument("categories", query)
            .flatMap(result -> Future.succeededFuture(result.getRemovedCount() > 0));
        return future;
    }

    @Override
    public Future<CategoryList> findCategories(String userId) {
        // prepare a query
        JsonObject query = new JsonObject();
        query.put("userId", userId);
        // execute query
       return client.find("categories", query)
            .flatMap(list -> Future.succeededFuture(
                list.stream().map(mapper::apply).collect(Collectors.toList())
            ))
            .flatMap(list -> Future.succeededFuture(new CategoryList(list)));
    }

    @Override
    public Future<Category> updateCategory(Category category) {
        return category.id() != null ? saveCategory(category) : Future.failedFuture(new RuntimeException());
    }
    
}
