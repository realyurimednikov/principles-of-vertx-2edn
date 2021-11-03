package net.yurimednikov.vertxbook.cashx.web;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import net.yurimednikov.vertxbook.cashx.models.Account;
import net.yurimednikov.vertxbook.cashx.models.AccountList;
import net.yurimednikov.vertxbook.cashx.models.Pagination;
import net.yurimednikov.vertxbook.cashx.services.AccountService;
import net.yurimednikov.vertxbook.cashx.validators.AccountValidator;

import java.util.Optional;

public class SecuredAccountController {

    private final AccountService service;
    private final AccountValidator validator;

    public SecuredAccountController(AccountService service) {
        this.service = service;
        this.validator = new AccountValidator();
    }

    public void createAccount (RoutingContext context){
        JsonObject payload = context.getBodyAsJson();
        JsonObject principal = context.user().principal();
        System.out.println(principal.encode());
        long userId = principal.getLong("userId");
        payload.put("userId", userId);
        validator.validate(payload)
                .compose(service::createAccount)
                .onSuccess(account -> {
                    JsonObject responseBody = JsonObject.mapFrom(account);
                    context.response().setStatusCode(201).end(responseBody.encode());
                })
                .onFailure(context::fail);
    }

    public void findAccountById (RoutingContext context){
        String idParam = context.pathParam("id");
        long accountId = Long.parseLong(idParam);
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
        JsonObject principal = context.user().principal();
        System.out.println(principal.encode());
        long userId = principal.getLong("userId");
        payload.put("userId", userId);
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
        long accountId = Long.parseLong(idParam);
        service.removeAccount(accountId)
                .onFailure(context::fail)
                .onSuccess(result -> {
                    JsonObject responseBody = new JsonObject();
                    responseBody.put("removed", result);
                    context.response().setStatusCode(200).end(responseBody.encode());
                });
    }

    public void findAccounts (RoutingContext context){
        JsonObject principal = context.user().principal();
        System.out.println(principal.encode());
        long userId = principal.getLong("userId");
        System.out.println("Find accounts");
        Optional<Integer> page = context.queryParam("page").stream().map(Integer::valueOf).findFirst();
        Optional<Integer> limit = context.queryParam("limit").stream().map(Integer::valueOf).findFirst();
        if (page.isPresent() && limit.isPresent()) {
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
