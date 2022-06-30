package tech.yurimednikov.vertxbook.cashx.web;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import tech.yurimednikov.vertxbook.cashx.models.Account;
import tech.yurimednikov.vertxbook.cashx.models.AccountList;
import tech.yurimednikov.vertxbook.cashx.models.Pagination;
import tech.yurimednikov.vertxbook.cashx.services.AccountService;
import tech.yurimednikov.vertxbook.cashx.validators.AccountValidator;

import java.util.Optional;

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

    // without a pagination
//    public void findAccounts (RoutingContext context){
//        String idParam = context.pathParam("userId");
//        long userId = Long.valueOf(idParam);
//        service.findAccounts(userId)
//            .onFailure(context::fail)
//            .onSuccess(accounts -> {
//                JsonObject responseBody = JsonObject.mapFrom(accounts);
//                context.response().setStatusCode(200).end(responseBody.encode());
//            });
//    }

    // with a pagination
    public void findAccounts (RoutingContext context){
        String idParam = context.pathParam("userId");
        Long userId = Long.valueOf(idParam);
        Optional<Integer> page = context.queryParam("page").stream().map(Integer::valueOf).findFirst();
        Optional<Integer> limit = context.queryParam("limit").stream().map(Integer::valueOf).findFirst();
        if (page.isPresent() && limit.isPresent()) {
            // continue with a pagination
            Pagination pagination = new Pagination(page.get(), limit.get());
            service.findAccountsWithPagination(userId, pagination)
                    .onFailure(context::fail)
                    .onSuccess(pagedAccountList -> {
                        AccountList accounts = new AccountList(pagedAccountList.getAccounts());
                        JsonObject responseBody = JsonObject.mapFrom(accounts);
                        context.response()
                                .setStatusCode(200)
                                .putHeader("X-APP-PAGINATION", "true")
                                .putHeader("X-APP-PAGINATION-TOTAL-PAGES",
                                        Integer.toString(pagedAccountList.getNumberOfPages()))
                                .putHeader("X-APP-PAGINATION-TOTAL-ENTITIES",
                                        Integer.toString(pagedAccountList.getTotal()))
                                .putHeader("X-APP-PAGINATION-CURRENT-PAGE",
                                        Integer.toString(pagedAccountList.getCurrentPage()))
                                .end(responseBody.encode());
                    });
        } else {
            // continue without a pagination
            service.findAccounts(userId)
            .onFailure(context::fail)
            .onSuccess(accounts -> {
                JsonObject responseBody = JsonObject.mapFrom(accounts);
                context.response()
                        .setStatusCode(200)
                        .putHeader("X-APP-PAGINATION", "false")
                        .end(responseBody.encode());
            });
        }
    }
}
