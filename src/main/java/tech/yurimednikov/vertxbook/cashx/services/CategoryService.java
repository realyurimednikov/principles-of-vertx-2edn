package tech.yurimednikov.vertxbook.cashx.services;

import java.util.Optional;

import io.vertx.core.Future;
import tech.yurimednikov.vertxbook.cashx.models.Category;
import tech.yurimednikov.vertxbook.cashx.models.CategoryList;

public interface CategoryService {
    
    Future<Category> createCategory (Category category);

    Future<Optional<Category>> findCategoryById (String id);

    Future<Boolean> removeCategory (String id);

    Future<CategoryList> findCategoriesForUser (String userId);

    Future<Category> updateCategory (Category category);
}
