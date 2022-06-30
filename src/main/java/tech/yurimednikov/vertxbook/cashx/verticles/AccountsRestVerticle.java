package tech.yurimednikov.vertxbook.cashx.verticles;


import com.google.inject.Guice;
import com.google.inject.Injector;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import tech.yurimednikov.vertxbook.cashx.config.ApplicationConfigurationManager;
import tech.yurimednikov.vertxbook.cashx.errors.AccessDeniedException;
import tech.yurimednikov.vertxbook.cashx.errors.DependencyCreationException;
import tech.yurimednikov.vertxbook.cashx.errors.ValidatorException;
import tech.yurimednikov.vertxbook.cashx.modules.ApplicationModule;
import tech.yurimednikov.vertxbook.cashx.web.AccountController;

public class AccountsRestVerticle extends AbstractVerticle {
    
    private final AccountController controller;

    public AccountsRestVerticle(AccountController controller){
        this.controller = controller;
    }


    @Override
    public void start(Promise<Void> promise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route("/api/*").handler(BodyHandler.create());
        router.route("/api/*").handler(CorsHandler.create("*"));
        router.post("/api/accounts/:userId").handler(controller::createAccount);
        router.get("/api/accounts/:userId").handler(controller::findAccounts);
        router.put("/api/account/:id").handler(controller::updateAccount);
        router.get("/api/account/:id").handler(controller::findAccountById);
        router.delete("/api/account/:id").handler(controller::removeAccount);
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
        server.listen(8080)
            .onFailure(promise::fail)
            .onSuccess(result -> promise.complete());
    }

}
