package tech.yurimednikov.vertxbook.cashx.errors;

import io.vertx.core.json.JsonArray;

public class ValidatorException extends RuntimeException {
    
    private final JsonArray reasons;

    public ValidatorException(JsonArray messages){
        this.reasons = messages;
    }

    public JsonArray getMessages(){
        return reasons;
    }
}
