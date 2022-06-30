package tech.yurimednikov.vertxbook.cashx.schemas;

public interface Schema<T> {
    
    String getInsertQuery(T entity);

    String getUpdateQuery(T entity);

    String getDeleteQuery(T entity);

    String getSelectOneByIdQuery(T entity);

    String getSelectManyByUserQuery (T entity);
}
