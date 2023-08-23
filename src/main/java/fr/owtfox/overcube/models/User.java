package fr.owtfox.overcube.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import fr.owtfox.overcube.database.Database;
import org.bson.Document;

import java.util.Objects;

public class User {
    private final Database _database;
    private final MongoCollection<Document> _users;

    public User(Database database) {
        _database = database;
        _users = _database.getCollection("users");
    }

    public void giveReport(String targetPlayerName) {
        Document suspectUserAlreadyExist = _users.find(new Document("name", targetPlayerName)).first();

        if (suspectUserAlreadyExist == null) {
            Document newSuspectUser = new Document("name", targetPlayerName).append("report", 1);
            _users.insertOne(newSuspectUser);
        } else {
            int currentReports = suspectUserAlreadyExist.getInteger("report", 0);
            int updatedReports = currentReports + 1;
            _users.updateOne(
                    Filters.eq("name", targetPlayerName),
                    new Document("$set", new Document("report", updatedReports))
            );
        }
    }

    public void grantUser(String targetPlayerName) {
        Document userExist = _users.find(new Document("name", targetPlayerName)).first();

        if (userExist == null) {
            Document user = new Document("name", targetPlayerName).append("is_over_cube", true);
            _users.insertOne(user);
        } else {
            boolean isOverCube = userExist.getBoolean("is_over_cube", false);
            if (!isOverCube) {
                _users.updateOne(
                        Filters.eq("name", targetPlayerName),
                        new Document("$set", new Document("is_over_cube", true))
                );
            }
        }
    }

    public void ungrantUser(String targetPlayerName) {
        Document userExist = _users.find(new Document("name", targetPlayerName)).first();

        if (userExist != null) {
            boolean userIsGrant = userExist.getBoolean("is_over_cube", false);
            if (userIsGrant) {
                _users.updateOne(
                        Filters.eq("name", targetPlayerName),
                        new Document("$set", new Document("is_over_cube", false))
                );
            }
        }
    }
}
