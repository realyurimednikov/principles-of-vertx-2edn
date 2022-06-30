package tech.yurimednikov.vertxbook.cashx.repositories;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import tech.yurimednikov.vertxbook.cashx.models.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserReactivePgRepositoryImpl implements UserRepository{

    private final SqlClient client;
    private final Vertx vertx;

    public UserReactivePgRepositoryImpl(Vertx vertx, SqlClient client) {
        this.client = client;
        this.vertx = vertx;
    }

    public Future<Void> createTable(){
        return vertx.fileSystem().readFile("sql/users.sql")
                .map(Buffer::toString)
                .compose(query -> client.query(query).execute())
                .compose(result -> Future.succeededFuture());
    }

    @Override
    public Future<User> saveUser(User user) {
        String sql = """
         INSERT INTO users (user_email, user_hash, user_salt, user_created)
         VALUES ($1, $2, $3, $4)
          RETURNING user_id;
                """;
        Tuple params = Tuple.of(user.getEmail(), user.getHash(), user.getSalt(), user.getCreatedDate());
        return client.preparedQuery(sql).execute(params).compose(rows -> {
            if (rows.rowCount() == 0) return Future.failedFuture(new RuntimeException());
            Row row = rows.iterator().next();
            long userId = row.getLong("user_id");
            return Future.succeededFuture(User.withId(userId, user));
        });
    }

    @Override
    public Future<Optional<User>> findUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE user_email = $1 LIMIT 1;";
        Tuple params = Tuple.of(email);
        return client.preparedQuery(sql).execute(params).map(rows -> {
            if (rows.rowCount() == 0) return Optional.empty();
            Row row = rows.iterator().next();
            long userId = row.getLong("user_id");
            String hash = row.getString("user_hash");
            String salt = row.getString("user_salt");
            LocalDate created = row.getLocalDate("user_created");
            List<String> permissions = row.getArrayOfStrings("user_permissions") != null ?
                            Arrays.asList(row.getArrayOfStrings("user_permissions"))
                            : List.of();
            User user = new User(userId, email, hash, salt, permissions, created);
            return Optional.of(user);
        });
    }

}
