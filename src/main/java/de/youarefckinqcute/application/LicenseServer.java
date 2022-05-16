// Created by Torben R.
package de.youarefckinqcute.application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import de.youarefckinqcute.server.Config;
import de.youarefckinqcute.server.MongoConnection;
import de.youarefckinqcute.server.GenerateContextHandler;
import de.youarefckinqcute.server.ValidateContextHandler;
import lombok.Getter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Getter
public class LicenseServer {

    private static final Gson GSON = new GsonBuilder().setLenient().serializeNulls().setPrettyPrinting().create();

    private final GenerateContextHandler generateContextHandler;
    private final MongoConnection mongoConnection;
    private Config config;

    public LicenseServer() throws IOException {
        loadConfig();
        this.mongoConnection = new MongoConnection(this);
        this.generateContextHandler = new GenerateContextHandler(this);
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(getConfig().getServerPort()), 0);
        httpServer.createContext("/generate", new GenerateContextHandler(this));
        httpServer.createContext("/validate", new ValidateContextHandler(this));
        httpServer.setExecutor(null);
        httpServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            getMongoConnection().getMongoClient().close();
            httpServer.stop(200);
        }));
    }

    private void loadConfig() throws IOException {
        File file = new File("config.json");
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            if (!newFile) {
                System.exit(1);
            }
            config = new Config();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(GSON.toJson(config));
            fileWriter.close();
        } else {
            config = GSON.fromJson(String.join("", Files.readAllLines(Paths.get(file.getPath()), StandardCharsets.UTF_8)), Config.class);
        }
    }
}
