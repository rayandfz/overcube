package fr.owtfox.overcube.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import fr.owtfox.overcube.database.Database;
import org.bson.Document;

public class User {
    private final Database database;
    private final MongoCollection<Document> users;

    public User(Database database) {
        this.database = database;
        users = database.getCollection("users");
    }

    public void giveReport(String targetPlayerName) {
        final Document suspectUserAlreadyExist = users.find(new Document("name", targetPlayerName)).first();

        if (suspectUserAlreadyExist == null) {
            final Document newSuspectUser = new Document("name", targetPlayerName).append("report", 1);
            users.insertOne(newSuspectUser);
        } else {
            final int currentReports = suspectUserAlreadyExist.getInteger("report", 0);
            final int updatedReports = currentReports + 1;
            users.updateOne(
                    Filters.eq("name", targetPlayerName),
                    new Document("$set", new Document("report", updatedReports))
            );
        }
    }

    public void grantUser(String targetPlayerName) {
        final Document userExist = users.find(new Document("name", targetPlayerName)).first();

        if (userExist == null) {
            final Document user = new Document("name", targetPlayerName).append("is_over_cube", true);
            users.insertOne(user);
        } else {
            final boolean isOverCube = userExist.getBoolean("is_over_cube", false);
            if (!isOverCube) {
                users.updateOne(
                        Filters.eq("name", targetPlayerName),
                        new Document("$set", new Document("is_over_cube", true))
                );
            }
        }
    }

    public void ungrantUser(String targetPlayerName) {
        final Document userExist = users.find(new Document("name", targetPlayerName)).first();

        if (userExist != null) {
            final boolean userIsGrant = userExist.getBoolean("is_over_cube", false);
            if (userIsGrant) {
                users.updateOne(
                        Filters.eq("name", targetPlayerName),
                        new Document("$set", new Document("is_over_cube", false))
                );
            }
        }
    }

    public int getOverNumber(String targetPlayerName) {
        final Document user = users.find(new Document("name", targetPlayerName)).first();

        if (user != null) return user.getInteger("over_number", 0);

        return 0;
    }

    public int getReport(String targetPlayerName) {
        final Document user = users.find(new Document("name", targetPlayerName)).first();

        if (user != null) return user.getInteger("report");

        return 0;
    }

    public MongoIterable<String> getAllPlayerReport() {
        final Document query = new Document("report", new Document("$gt", 5));
        return users.find(query).map(document -> document.get("name").toString());
    }

    public MongoIterable<String> getOvercubeUser() {
        final Document query = new Document("is_over_cube", true);
        return users.find(query).map(document -> document.get("name").toString());
    }
}
