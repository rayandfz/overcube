package fr.owtfox.overcube.database;

import com.mongodb.client.*;
import org.bson.Document;

public class Database {
    private final MongoClient mongo;
    private final MongoDatabase database;

    public Database() {
        mongo = MongoClients.create("mongodb://localhost:27017");
        database = mongo.getDatabase("overcube");
    }

    public void createCollection() {
        final MongoIterable<String> collections = database.listCollectionNames();

        for (String collectionName : collections) {
            if (collectionName.equals("users")) {
                return;
            }
        }

        database.createCollection("users");
     }

    public MongoCollection<Document> getCollection(String name) {
        return database.getCollection(name);
    }
}
