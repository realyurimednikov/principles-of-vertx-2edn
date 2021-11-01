package net.yurimednikov.vertxbook.cashx.repositories;

import io.vertx.core.Future;
import net.yurimednikov.vertxbook.cashx.models.User;

import java.util.Optional;

public interface UserRepository {

    Future<User> saveUser (User user);

    Future<Optional<User>> findUserByEmail (String email);

//    Future<Boolean> setNewPassword (long id, String newPassword, String newHash);
//
//    Future<Boolean> addPermission (long id, String permission);
//
//    Future<Boolean> removePermission (long id, String permission);

}
