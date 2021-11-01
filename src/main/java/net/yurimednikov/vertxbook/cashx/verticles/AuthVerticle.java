package net.yurimednikov.vertxbook.cashx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import net.yurimednikov.vertxbook.cashx.errors.AccessDeniedException;
import net.yurimednikov.vertxbook.cashx.errors.ValidatorException;
import net.yurimednikov.vertxbook.cashx.web.AuthController;

public class AuthVerticle extends AbstractVerticle {

    private final AuthController controller;

    public AuthVerticle(AuthController controller) {
        this.controller = controller;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route("/api/auth/*").handler(BodyHandler.create());
        router.post("/api/auth/login").handler(controller::loginEndpoint);
        router.post("/api/auth/signup").handler(controller::signupEndpoint);
        router.route("/api/*").failureHandler(context -> {
            if (context.failed()){
                Throwable reason = context.failure();
                if (reason instanceof ValidatorException){
                    ValidatorException exception = (ValidatorException) reason;
                    JsonArray validatorMessages = exception.getMessages();
                    System.out.println(validatorMessages.encode());
                    context.response().setStatusCode(400).end(validatorMessages.encode());
                } else if (reason instanceof AccessDeniedException) {
                    context.response().setStatusCode(403).end();
                } else {
                    context.response().setStatusCode(500).end("Error happened");
                }
            }
        });
        server.requestHandler(router);
        server.listen(8080).onFailure(startPromise::fail).onSuccess(result -> startPromise.complete());
    }
}
