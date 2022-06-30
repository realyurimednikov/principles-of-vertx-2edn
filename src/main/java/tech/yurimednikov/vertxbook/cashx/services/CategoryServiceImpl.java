package tech.yurimednikov.vertxbook.cashx.services;

import java.util.Optional;

import io.vertx.core.Future;
import tech.yurimednikov.vertxbook.cashx.models.Category;
import tech.yurimednikov.vertxbook.cashx.models.CategoryList;
import tech.yurimednikov.vertxbook.cashx.repositories.CategoryRepository;

public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository repository;

    public CategoryServiceImpl(CategoryRepository repository){
        this.repository = repository;
    }

    @Override
    public Future<Category> createCategory(Category category) {
        return repository.saveCategory(category);
    }

    @Override
    public Future<Optional<Category>> findCategoryById(String id) {
        return repository.findCategoryById(id);
    }

    @Override
    public Future<Boolean> removeCategory(String id) {
        return repository.removeCategory(id);
    }

    @Override
    public Future<CategoryList> findCategoriesForUser(String userId) {
        return repository.findCategories(userId);
    }

    @Override
    public Future<Category> updateCategory(Category category) {
        return repository.updateCategory(category);
    }
}
