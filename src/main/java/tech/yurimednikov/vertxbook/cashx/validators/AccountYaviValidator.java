package tech.yurimednikov.vertxbook.cashx.validators;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.Constraint;
import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validator;
import io.vertx.core.Future;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import tech.yurimednikov.vertxbook.cashx.errors.ValidatorException;
import tech.yurimednikov.vertxbook.cashx.models.Account;

import java.util.List;
import java.util.stream.Collectors;

public class AccountYaviValidator {

    private Validator<Account> validator = ValidatorBuilder.<Account>of()
            .constraint(Account::getCurrency, "currency", c -> c.notNull().fixedSize(3))
            .constraint(Account::getName, "name", Constraint::notNull)
            .constraint(Account::getUserId, "userId", Constraint::notNull)
            .build();

    public Future<Account> validate (JsonObject json){
        try {
            Account account = Json.decodeValue(json.toBuffer(), Account.class);
            ConstraintViolations violations = validator.validate(account);
            if (!violations.isValid()) {
                List<JsonObject> list = violations.stream()
                        .map(cv -> new JsonObject().put("name", cv.name()).put("message", cv.message()))
                        .collect(Collectors.toList());
                JsonArray messages = new JsonArray(list);
                return Future.failedFuture(new ValidatorException(messages));
            }
            return Future.succeededFuture(account);

        } catch (DecodeException ex){
            JsonArray messages = new JsonArray();
            messages.add(new JsonObject().put("message", "Invalid format"));
            return Future.failedFuture(new ValidatorException(messages));
        }
    }
}
