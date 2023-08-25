package fr.owtfox.overcube.models;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IUserRepository {

    CompletableFuture<Void> addUser(User user);

    CompletableFuture<Boolean> contains(UUID uuid);

    CompletableFuture<Collection<User>> getUsers();

    CompletableFuture<Collection<User>> getOverCubeUsers();

    CompletableFuture<Boolean> getOverCubeReady(UUID uuid);

    CompletableFuture<Integer> getReportCount(UUID uuid);

    CompletableFuture<Boolean> getPermission(UUID uuid);

    CompletableFuture<Void> giveReport(UUID uuid);

    default CompletableFuture<Void> giveReport(User user) {
        return giveReport(user.getUUID());
    }

    CompletableFuture<Void> setPermission(UUID uuid, boolean allowed);

    default CompletableFuture<Void> setPermission(User user, boolean allowed) {
        return setPermission(user.getUUID(), allowed);
    }

    CompletableFuture<Void> setOverCubeReady(UUID uuid, boolean allowed);

    CompletableFuture<Collection<User>> getSpottedUsers();

    CompletableFuture<Integer> getOverCubeCount(UUID uuid);
}

