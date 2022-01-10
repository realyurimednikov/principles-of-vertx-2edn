package net.yurimednikov.vertxbook.cashx.models;

public class Sort {

    private final String fieldName;
    private final String order;

    private Sort(String fieldName, String order){
        this.fieldName = fieldName;
        this.order = order;
    }

    public static Sort asc(String field){
        return new Sort(field, "ASC");
    }

    public static Sort desc(String field){
        return new Sort(field, "DESC");
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getOrder() {
        return order;
    }
}
