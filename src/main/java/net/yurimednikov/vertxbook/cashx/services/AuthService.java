package net.yurimednikov.vertxbook.cashx.services;

import io.vertx.core.Future;

public interface AuthService {
    
    Future<Boolean> signup (String email, String password);

    Future<String> login (String email, String password);
    
}
