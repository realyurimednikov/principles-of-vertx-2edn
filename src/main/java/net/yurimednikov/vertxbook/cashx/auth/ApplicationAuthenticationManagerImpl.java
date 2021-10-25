package net.yurimednikov.vertxbook.cashx.auth;

import java.util.List;
import java.util.stream.Collectors;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;

public class ApplicationAuthenticationManagerImpl implements ApplicationAuthenticationManager {

    private final JWTAuth authProvider;

    public ApplicationAuthenticationManagerImpl(JWTAuth auth){
        this.authProvider = auth;
    }

    @Override
    public Future<UserPrincipal> authorize(JsonObject credentials) {
        authProvider.authenticate(credentials).onSuccess(user -> {
            // get id
            long id = user.principal().getLong("sub");
            // get permission
            JsonArray permissionsArray = user.principal().getJsonArray("permissions");
            // List<AuthenticationPermission> permissions = permissionsArray.getList().stream().map(p -> new AuthenticationPermission()).collect(Collectors.toList());
            // create UserPrincipal object
        });
        return null;
    }
    
}
