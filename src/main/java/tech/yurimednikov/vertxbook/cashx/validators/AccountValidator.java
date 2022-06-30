package tech.yurimednikov.vertxbook.cashx.validators;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import tech.yurimednikov.vertxbook.cashx.errors.ValidatorException;
import tech.yurimednikov.vertxbook.cashx.models.Account;

public class AccountValidator {

    
    public Future<Account> validate (JsonObject json){
        JsonArray messages = new JsonArray();
        
        try {
            if (json.isEmpty()) {
                JsonObject message = new JsonObject();
                message.put("message", "Invalid or empty data");
                messages.add(message);
                throw new ValidatorException(messages);
            }

            long id;

            if (json.getLong("id") == null) {
                id = 0;
            } else {
                id = json.getLong("id");
            }

            if (json.getLong("userId") == null) {
                JsonObject message = new JsonObject();
                message.put("userId", "The field is required");
                messages.add(message);
                throw new ValidatorException(messages);
            }
            long userId = json.getLong("userId");

            if (json.getString("currency") == null) {
                JsonObject message = new JsonObject();
                message.put("currency", "The field is required");
                messages.add(message);
                throw new ValidatorException(messages);
            }
            String currency = json.getString("currency");

            if (json.getString("name") == null) {
                JsonObject message = new JsonObject();
                message.put("name", "The field is required");
                messages.add(message);
                throw new ValidatorException(messages);
            }
            String name = json.getString("name");

            Account account = new Account(id, name, currency, userId);
            return Future.succeededFuture(account);
        } catch (ValidatorException exception){
            return Future.failedFuture(new ValidatorException(exception.getMessages()));
        }
                 
    }
}
