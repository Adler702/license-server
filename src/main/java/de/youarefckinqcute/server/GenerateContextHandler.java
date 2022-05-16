// Created by Torben R.
package de.youarefckinqcute.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.youarefckinqcute.application.LicenseServer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.io.OutputStreamWriter;

@Getter
@AllArgsConstructor
public class GenerateContextHandler implements HttpHandler {

    private final LicenseServer licenseServer;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String name = (String) exchange.getAttribute("name");
        long duration = (long) exchange.getAttribute("duration");
        String adminName = (String) exchange.getAttribute("adminname");
        String adminPassword = (String) exchange.getAttribute("adminpassword");
        String response;
        int responseCode = 200;
        if (licenseServer.getConfig().getAdminAccounts().containsKey(adminName)) {
            if (licenseServer.getConfig().getAdminAccounts().get(adminPassword).equals(adminPassword)) {
                response = licenseServer.getMongoConnection().generate(name, duration);
            } else {
                response = "unauthorized";
                responseCode = 401;
            }
        } else {
            response = "unauthorized";
            responseCode = 401;
        }
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(exchange.getResponseBody());
        exchange.sendResponseHeaders(responseCode, response.length());
        outputStreamWriter.write(response);
        outputStreamWriter.close();
    }
}
