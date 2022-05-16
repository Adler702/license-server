// Created by Torben R.
package de.youarefckinqcute.server;

import lombok.Getter;

import java.util.Map;

@Getter
public class Config {

    int serverPort = 5005;
    int keylengh = 8;
    String mongoHost = "localhost";
    String mongoPort = "27017";
    String mongoUser = "user";
    String mongoPassword = "password";
    String mongoDatabase = "database";
    Map<String, String> adminAccounts = Map.of("admin1", "password1", "admin2", "password2");
}
