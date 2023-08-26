package fr.owtfox.overcube.database;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.RowData;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;
import fr.owtfox.overcube.models.IUserRepository;
import fr.owtfox.overcube.models.User;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class SQLUserRepository implements IUserRepository {

    private Connection databaseConnection;

    public SQLUserRepository(Connection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    private User parseUserRow(RowData row) {
        return new User(
                (UUID) row.get("uuid"),
                row.getInt("report"),
                row.getBoolean("is_over_cube"),
                row.getInt("overcube_count"),
                row.getBoolean("overcube_ready")
        );
    }

    @Override
    public CompletableFuture<Boolean> getPermission(UUID uuid) {
        return databaseConnection
                .sendPreparedStatement("SELECT is_over_cube FROM users WHERE uuid = ?", singletonList(uuid))
                .thenApply(result -> result.getRows().get(0).getBoolean("is_over_cube"));
    }

    @Override
    public CompletableFuture<Void> addUser(User user) {
        return databaseConnection
                .sendPreparedStatement("INSERT INTO users VALUES (?, ?, ?, ?, ?)", asList(
                        user.getUUID(),
                        user.getReportCount(),
                        user.isOverCube(),
                        user.getOverCubeCount(),
                        user.getOverCubeReady()
                ))
                .thenRun(DO_NOTHING);
    }

    @Override
    public CompletableFuture<Boolean> contains(UUID uuid) {
        return databaseConnection
                .sendPreparedStatement("SELECT * FROM users WHERE uuid = ?", singletonList(uuid))
                .thenApply(result -> !result.getRows().isEmpty());
    }

    @Override
    public CompletableFuture<Collection<User>> getUsers() {
        return databaseConnection
                .sendQuery("SELECT * FROM users")
                .thenApply(result -> result
                        .getRows()
                        .stream()
                        .map(this::parseUserRow)
                        .collect(Collectors.toList())
                );
    }

    @Override
    public CompletableFuture<Collection<User>> getOverCubeUsers() {
        return databaseConnection
                .sendQuery("SELECT * FROM users WHERE is_over_cube = true")
                .thenApply(result -> result
                        .getRows()
                        .stream()
                        .map(this::parseUserRow)
                        .collect(Collectors.toList())
                );
    }

    @Override
    public CompletableFuture<Integer> getReportCount(UUID uuid) {
        return databaseConnection
                .sendPreparedStatement("SELECT report FROM users WHERE uuid = ?", singletonList(uuid))
                .thenApply(result -> result.getRows().get(0).getInt("report"));
    }

    @Override
    public CompletableFuture<Void> giveReport(UUID uuid) {
        return databaseConnection
                .sendPreparedStatement("UPDATE users SET report = report+1 WHERE uuid = ?", singletonList(uuid))
                .thenRun(DO_NOTHING);
    }

    @Override
    public CompletableFuture<Void> setPermission(UUID uuid, boolean allowed) {
        return databaseConnection
                .sendPreparedStatement("UPDATE users SET is_over_cube = ? WHERE uuid = ?", asList(allowed, uuid))
                .thenRun(DO_NOTHING);
    }

    @Override
    public CompletableFuture<User> getSingleSpottedUser(UUID uuid) {
        return databaseConnection
                .sendPreparedStatement("SELECT * FROM users WHERE report >= 5 AND uuid = ?", singletonList(uuid))
                .thenApply(result -> result.getRows()
                        .stream()
                        .map(this::parseUserRow)
                        .findFirst()
                        .orElse(null)
                );
    }

    @Override
    public CompletableFuture<Collection<User>> getSpottedUser() {
        return databaseConnection
                .sendQuery("SELECT * FROM users WHERE report >= 5")
                .thenApply(result -> result
                        .getRows()
                        .stream()
                        .map(this::parseUserRow)
                        .collect(Collectors.toList())
                );
    }

    @Override
    public CompletableFuture<Integer> getOverCubeCount(UUID uuid) {
        return databaseConnection
                .sendPreparedStatement("SELECT overcube_count FROM users WHERE uuid = ?", singletonList(uuid))
                .thenApply(result -> result.getRows().get(0).getInt("overcube_count"));
    }

    @Override
    public CompletableFuture<Boolean> getOverCubeReady(UUID uuid) {
        return databaseConnection
                .sendPreparedStatement("SELECT overcube_ready FROM users WHERE uuid = ?", singletonList(uuid))
                .thenApply(result -> result.getRows().get(0).getBoolean("overcube_ready"));
    }

    @Override
    public CompletableFuture<Void> setOverCubeReady(UUID uuid, boolean allowed) {
        return databaseConnection
                .sendPreparedStatement("UPDATE users SET overcube_ready = ? WHERE uuid = ?", asList(allowed, uuid))
                .thenRun(DO_NOTHING);
    }

    private static final Runnable DO_NOTHING = () -> {
    };

    public static SQLUserRepository openPostgres(String url, String database, String username, String password) {
        String finalUrl = url +
                database +
                "?username=" +
                username +
                "&password=" +
                password;

        return new SQLUserRepository(PostgreSQLConnectionBuilder.createConnectionPool(finalUrl));
    }
}
