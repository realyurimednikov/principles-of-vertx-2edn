package tech.yurimednikov.vertxbook.cashx.services;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public interface AuthService {
    
    Future<Boolean> signup (String email, String password);

    Future<String> login (String email, String password);

    void authenticate (RoutingContext context);
}
