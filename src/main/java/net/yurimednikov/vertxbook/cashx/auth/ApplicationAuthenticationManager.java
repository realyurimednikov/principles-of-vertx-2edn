package net.yurimednikov.vertxbook.cashx.auth;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface ApplicationAuthenticationManager {
    
    Future<UserPrincipal> authorize (JsonObject credentials);
    
}
