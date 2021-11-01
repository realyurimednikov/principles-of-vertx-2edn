package net.yurimednikov.vertxbook.cashx.web;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.yurimednikov.vertxbook.cashx.services.AuthService;

public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service){
        this.service = service;
    }

    public void signupEndpoint (RoutingContext context){
        JsonObject body = context.getBodyAsJson();
        String email = body.getString("email").trim().toLowerCase();
        String password = body.getString("password");
        service.signup(email, password).onFailure(context::fail).onSuccess(result -> {
            JsonObject response = new JsonObject();
            response.put("message", "User created");
            context.response().setStatusCode(201).end(response.encode());
        });
    }

    public void loginEndpoint (RoutingContext context){
        JsonObject body = context.getBodyAsJson();
        String email = body.getString("email");
        String password = body.getString("password");
        service.login(email, password).onFailure(context::fail).onSuccess(result -> {
            JsonObject response = new JsonObject();
            response.put("token", result);
            context.response().setStatusCode(200).end(response.encode());
        });
    }
}
