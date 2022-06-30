package tech.yurimednikov.vertxbook.cashx.validators;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import tech.yurimednikov.vertxbook.cashx.errors.ValidatorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class AccountValidatorTest {

    @Test
    void validationPassedTest(Vertx vertx, VertxTestContext context){
        JsonObject body = new JsonObject();
        body.put("userId", 1);
        body.put("currency", "EUR");
        body.put("name", "Bank account");

        AccountValidator validator = new AccountValidator();

        context.verify(() -> {
            validator.validate(body).onSuccess(account -> {
                Assertions.assertEquals(body.getLong("userId"), account.getUserId());
                Assertions.assertEquals(body.getString("currency"), account.getCurrency());
                Assertions.assertEquals(body.getString("name"), account.getName());
                Assertions.assertEquals(0, account.getId());
                context.completeNow();
            });
        });
    }

    @Test
    void validationFailedTest(Vertx vertx, VertxTestContext context){
        JsonObject body = new JsonObject();
        body.put("userId", 1);
        body.put("name", "Bank account");

        AccountValidator validator = new AccountValidator();

        context.verify(() -> {
            validator.validate(body).onFailure(err -> {
                Assertions.assertTrue(err instanceof ValidatorException);
                ValidatorException ve = (ValidatorException)  err;
                JsonArray messages = ve.getMessages();
                Assertions.assertEquals(1, messages.size());
                Assertions.assertEquals(new JsonObject().put("currency", "The field is required"),
                        messages.getJsonObject(0));
                context.completeNow();
            });
        });
    }
}
