package net.yurimednikov.vertxbook.cashx.web;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.yurimednikov.vertxbook.cashx.models.Account;
import net.yurimednikov.vertxbook.cashx.services.AccountService;
import net.yurimednikov.vertxbook.cashx.validators.AccountValidator;

public class AccountController {
    
    private final AccountService service;
    private final AccountValidator validator;

    public AccountController(AccountService service){
        this.service = service;
        this.validator = new AccountValidator();
    }

    public void createAccount (RoutingContext context){
        JsonObject payload = context.getBodyAsJson();
        // System.out.println("Body is: " + payload.encode());
        validator.validate(payload)
            .compose(service::createAccount)
            .onSuccess(account -> {
                // System.out.println("Created account");
                JsonObject responseBody = JsonObject.mapFrom(account);
                // System.out.println(responseBody.encode());
                context.response().setStatusCode(201).end(responseBody.encode());
            })
            .onFailure(context::fail);
    }

    public void findAccountById (RoutingContext context){
        String idParam = context.pathParam("id");
        long accountId = Long.valueOf(idParam);
        service.findAccountById(accountId)
            .onFailure(context::fail)
            .onSuccess(result -> {
                if (result.isPresent()){
                    Account account = result.get();
                    JsonObject responseBody = JsonObject.mapFrom(account);
                    context.response().setStatusCode(200).end(responseBody.encode());
                } else {
                    context.response().setStatusCode(404).end();
                }
            });
    }

    public void updateAccount (RoutingContext context){
        JsonObject payload = context.getBodyAsJson();
        validator.validate(payload)
            .compose(service::updateAccount)
            .onSuccess(account -> {
                JsonObject responseBody = JsonObject.mapFrom(account);
                context.response().setStatusCode(200).end(responseBody.encode());
            })
            .onFailure(context::fail);
    }

    public void removeAccount(RoutingContext context){
        String idParam = context.pathParam("id");
        long accountId = Long.valueOf(idParam);
        service.removeAccount(accountId)
            .onFailure(context::fail)
            .onSuccess(result -> {
                JsonObject responseBody = new JsonObject();
                responseBody.put("removed", result);
                context.response().setStatusCode(200).end(responseBody.encode());
            });
    }

    public void findAccounts (RoutingContext context){
        String idParam = context.pathParam("userId");
        long userId = Long.valueOf(idParam);
        service.findAccounts(userId)
            .onFailure(context::fail)
            .onSuccess(accounts -> {
                JsonObject responseBody = JsonObject.mapFrom(accounts);
                context.response().setStatusCode(200).end(responseBody.encode());
            });
    }
}
