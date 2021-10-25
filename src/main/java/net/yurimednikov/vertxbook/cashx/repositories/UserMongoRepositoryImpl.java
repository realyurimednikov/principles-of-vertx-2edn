package net.yurimednikov.vertxbook.cashx.repositories;

import java.util.Optional;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import net.yurimednikov.vertxbook.cashx.models.User;

public class UserMongoRepositoryImpl implements UserRepository {

    private final MongoClient client;
    private final UserDocumentMapper mapper;

    public UserMongoRepositoryImpl(MongoClient client){
        this.client = client;
        this.mapper = new UserDocumentMapper();
    }

    @Override
    public Future<User> createUser(User user) {
        JsonObject document = new JsonObject();
        document.put("email", user.email());
        document.put("hash", user.hash());
        document.put("salt", user.salt());
        return client.insert("users", document).flatMap(id -> Future.succeededFuture(User.withId(id, user)));
    }

    @Override
    public Future<Optional<User>> findUserByEmail(String email) {
        JsonObject query = new JsonObject();
        query.put("email", email);
        return client.findOne("categories", query, null)
            .flatMap(document -> Future.succeededFuture(Optional.ofNullable(document)))
            .flatMap(result -> Future.succeededFuture(result.map(mapper::apply)));
    }

    @Override
    public Future<Boolean> setNewPasswordAndHash(String userId, String password, String hash) {
        // todo
        return null;
    }
    
}
