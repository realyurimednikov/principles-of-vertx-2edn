package net.yurimednikov.vertxbook.cashx.models;

public record Category(String id, String userId, String name, String type) {
    
    public static Category withId(String id, Category old){
        return new Category(id, old.userId, old.name, old.type);
    }
}
