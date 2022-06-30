package tech.yurimednikov.vertxbook.cashx.models;

import java.time.LocalDate;
import java.util.List;

public final class User {

    private final long userId;
    private final String email;
    private final String hash;
    private final String salt;
    private final List<String> permissions;
    private final LocalDate createdDate;

    public User(long userId, String email, String hash, String salt, List<String> permissions, LocalDate createdDate) {
        this.userId = userId;
        this.email = email;
        this.hash = hash;
        this.salt = salt;
        this.permissions = permissions;
        this.createdDate = createdDate;
    }

    public static User withId (long id, User user){
        return new User(id, user.email, user.hash, user.salt, user.permissions, user.createdDate);
    }

    public long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getHash() {
        return hash;
    }

    public String getSalt() {
        return salt;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }
}
