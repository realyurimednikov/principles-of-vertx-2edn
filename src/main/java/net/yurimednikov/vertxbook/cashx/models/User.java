package net.yurimednikov.vertxbook.cashx.models;

public record User(String id, String email, String hash, String salt) {
    
    public static User withId (String newId, User old){
        return new User(newId, old.email, old.hash, old.salt);
    }
}
