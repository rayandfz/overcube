package fr.owtfox.overcube.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Objects;

public class Database {
    private MongoClient _mongo;
    private MongoDatabase _database;

    public Database() {
        _mongo = MongoClients.create("mongodb://localhost:27017");
        _database = _mongo.getDatabase("overcube");
    }

    public void createCollection() {
        var collections = _database.listCollectionNames();

        for (var collection : collections) {
            if (!Objects.equals(collection, "user"))  {
                _database.createCollection("users");
            }
        }
    }

    public MongoCollection<Document> getCollection(String name) {
        return _database.getCollection(name);
    }
}
