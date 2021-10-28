package net.yurimednikov.vertxbook.cashx.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
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
        return client.save("categories", document).map(id -> Category.withId(id, category));
    }

    @Override
    public Future<Optional<Category>> findCategoryById(String id) {
        // create a query
        JsonObject query = new JsonObject();
        query.put("_id", id);   
        // assert result
        Future<Optional<Category>> future = client.find("categories", query)
            .map(list -> list.stream().findFirst())
            .map(result -> result.map(mapper));

        // alternative solution with findOne()
        // NULL fields arg means that all fields would be returned
        client.findOne("categories", query, null)
            .map(Optional::ofNullable)
            .map(result -> result.map(mapper));

        // map document to object
        return future;
    }

    @Override
    public Future<Boolean> removeCategory(String id) {
        JsonObject query = new JsonObject();
        query.put("_id", id);  
        return client.removeDocument("categories", query)
            .map(result -> result.getRemovedCount() > 0);
    }

    @Override
    public Future<CategoryList> findCategories(String userId) {
        // prepare a query
        JsonObject query = new JsonObject();
        query.put("userId", userId);
        // execute query
       return client.find("categories", query)
            .map(list -> list.stream().map(mapper).collect(Collectors.toList()))
            .map(CategoryList::new);
    }

    @Override
    public Future<Category> updateCategory(Category category) {
        return category.id() != null ? saveCategory(category) : Future.failedFuture(new RuntimeException());
    }

    @Override
    public Future<Boolean> saveManyCategories(CategoryList categoryList) {
        List<BulkOperation> bulkOperations = categoryList.categories().stream()
                .map(c -> { //1
                    JsonObject document = new JsonObject();
                    document.put("name", c.name());
                    document.put("userId", c.userId());
                    document.put("type", c.type());
                    return document;
                })
                .map(BulkOperation::createInsert) //2
                .collect(Collectors.toList());;
        return client.bulkWrite("categories", bulkOperations) //3
                .map(result -> result.getInsertedCount() == categoryList.categories().size()); //4
    }


}
