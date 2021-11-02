package net.yurimednikov.vertxbook.cashx.services;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.yurimednikov.vertxbook.cashx.errors.AccessDeniedException;
import net.yurimednikov.vertxbook.cashx.models.User;
import net.yurimednikov.vertxbook.cashx.repositories.UserRepository;
import net.yurimednikov.vertxbook.cashx.tokens.TokenManager;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
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

    @Override
    public void authenticate(RoutingContext context) {
       try {
           String token = context.request().getHeader("Authorization");
           if (token == null || token.isEmpty()) throw new AccessDeniedException();
           tokenManager.decodeToken(token).onFailure(context::fail).onSuccess(principal -> {
               JsonObject user = new JsonObject();
               user.put("userId", principal.getUserid());
               System.out.println("userId" + user.encode());
               JsonArray permissions = new JsonArray(principal.getPermissions());
               user.put("permissions", permissions);
               System.out.println("User: " + user.encode());
               context.setUser(io.vertx.ext.auth.User.create(user));
               context.next();
           });
       } catch (Exception ex){
           context.fail(ex);
       }
    }


}
