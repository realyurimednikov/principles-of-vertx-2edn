package net.yurimednikov.vertxbook.cashx.repositories;

import java.util.Optional;

import io.vertx.core.Future;
import net.yurimednikov.vertxbook.cashx.models.User;

public interface UserRepository {
    
    Future<User> createUser (User user);

    Future<Optional<User>> findUserByEmail (String email);

    Future<Boolean> setNewPasswordAndHash (String userId, String password, String hash);
}
