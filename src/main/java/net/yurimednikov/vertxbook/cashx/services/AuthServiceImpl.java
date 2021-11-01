package net.yurimednikov.vertxbook.cashx.services;

import io.vertx.core.Future;
import net.yurimednikov.vertxbook.cashx.errors.AccessDeniedException;
import net.yurimednikov.vertxbook.cashx.models.User;
import net.yurimednikov.vertxbook.cashx.repositories.UserRepository;
import net.yurimednikov.vertxbook.cashx.tokens.TokenManager;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AuthServiceImpl implements AuthService{

    private final UserRepository repository;
    private final TokenManager tokenManager;

    public AuthServiceImpl(UserRepository repository,
                           TokenManager tokenManager) {
        this.repository = repository;
        this.tokenManager = tokenManager;
    }

    @Override
    public Future<Boolean> signup(String email, String password) {
        return repository.findUserByEmail(email).compose(result -> {
            if (result.isPresent()) return Future.failedFuture(new AccessDeniedException());
            String salt = BCrypt.gensalt();
            String hash = BCrypt.hashpw(password, salt);
            User user = new User(0, email, hash, salt, List.of(), LocalDate.now());
            return repository.saveUser(user);
        }).map(user -> Boolean.TRUE);
    }

    @Override
    public Future<String> login(String email, String password) {
        return repository.findUserByEmail(email).compose(result -> {
           if (result.isEmpty()) return Future.failedFuture(new AccessDeniedException());
           User user = result.get();
           String hash = user.getHash();
           String salt = user.getSalt();
           String hashedPassword = BCrypt.hashpw(password, salt);
           if (hashedPassword.contentEquals(hash)) {
               return tokenManager.encodeToken(user.getUserId(), user.getPermissions());
           } else {
               return Future.failedFuture(new AccessDeniedException());
           }
        });
    }


}
