package fr.owtfox.overcube.models;

import java.util.UUID;

public record User(UUID getUUID, int getReportCount, boolean isOverCube, int getOverCubeCount, boolean getOverCubeReady) {
    public static User empty(UUID uuid) {
        return new User(uuid, 0, false, 0, false);
    }
}