package net.yurimednikov.vertxbook.cashx.repositories;

import java.util.function.Function;

import io.vertx.core.json.JsonObject;
import net.yurimednikov.vertxbook.cashx.models.User;

class UserDocumentMapper implements Function<JsonObject, User>{

    @Override
    public User apply(JsonObject jd) {
        String id = jd.getString("_id");
        String email = jd.getString("email");
        String hash = jd.getString("hash");
        String salt = jd.getString("salt");
        return new User(id, email, hash, salt);
    }
    
}
