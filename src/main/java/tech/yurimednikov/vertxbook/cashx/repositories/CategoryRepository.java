package tech.yurimednikov.vertxbook.cashx.repositories;

import java.util.Optional;

import io.vertx.core.Future;
import tech.yurimednikov.vertxbook.cashx.models.Category;
import tech.yurimednikov.vertxbook.cashx.models.CategoryList;

public interface CategoryRepository {
    
    Future<Category> saveCategory (Category category);

    Future<Optional<Category>> findCategoryById (String id);

    Future<Boolean> removeCategory (String id);

    Future<CategoryList> findCategories (String userId);

    Future<Category> updateCategory (Category category);

    Future<Boolean> saveManyCategories(CategoryList categoryList);
}
