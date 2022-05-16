// Created by Torben R.
package de.youarefckinqcute.server;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import de.youarefckinqcute.application.LicenseServer;
import lombok.Getter;
import org.bson.Document;

import java.util.Random;

@Getter
public class MongoConnection {

    private final LicenseServer licenseServer;
    private final MongoClient mongoClient;
    private final ClientSession clientSession;
    private final MongoDatabase mongoDatabase;

    public MongoConnection(LicenseServer licenseServer) {
        this.licenseServer = licenseServer;
        this.mongoClient = new MongoClient(new MongoClientURI("mongodb://" + licenseServer.getConfig().getMongoUser() + ":" +
                licenseServer.getConfig().getMongoPassword() + "@" + licenseServer.getConfig().getMongoHost() + ":" + licenseServer.getConfig().getMongoPort()));
        this.clientSession = mongoClient.startSession();
        this.mongoDatabase = mongoClient.getDatabase(licenseServer.getConfig().getMongoDatabase());
    }

    public String validate(String name, String key, long expireAt) {
        Document first = mongoDatabase.getCollection("keys").find(new Document("name", name).append("key", key).append("expireAt", expireAt)).first();
        if (first == null) return "invalid";
        if ((long) first.get("expireAt") >= System.currentTimeMillis()) return "expired";
        if ((long) first.get("expireAt") == -1) return "valid";
        return "valid," + first.get("expireAt");
    }

    public String validate(String name, String key) {
        Document first = mongoDatabase.getCollection("keys").find(new Document("name", name).append("key", key)).first();
        if (first == null) return "invalid";
        if ((long) first.get("expireAt") >= System.currentTimeMillis()) return "expired";
        if ((long) first.get("expireAt") == -1) return "valid";
        return "valid," + first.get("expireAt");
    }

    public String generate(String name, long duration) {
        String key = generateKey();
        if (duration == -1) {
            Document append = new Document("name", name).append("key", key).append("expireAt", -1);
            mongoDatabase.getCollection("keys").insertOne(append);
            return key;
        }
        long expireAt = System.currentTimeMillis() + duration;
        Document append = new Document("name", name).append("key", key).append("expireAt", expireAt);
        mongoDatabase.getCollection("keys").insertOne(append);
        return key + "," + expireAt;
    }

    private String generateKey() {
        StringBuilder stringBuilder = new StringBuilder(this.licenseServer.getConfig().getKeylengh());
        String chars = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
        String[] array = chars.split("");
        for (int i = 0; i <= this.licenseServer.getConfig().getKeylengh(); i++) {
            stringBuilder.append(array[new Random().nextInt(array.length)]);
        }
        return stringBuilder.toString();
    }
}
