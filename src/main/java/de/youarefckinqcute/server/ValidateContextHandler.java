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
public class ValidateContextHandler implements HttpHandler {

    private final LicenseServer licenseServer;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String name = (String) exchange.getAttribute("name");
        long duration = (long) exchange.getAttribute("duration");
        String key = (String) exchange.getAttribute("key");
        String response = (exchange.getAttribute("duration") == null ? licenseServer.getMongoConnection().validate(name, key) : licenseServer.getMongoConnection().validate(name, key, duration));
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(exchange.getResponseBody());
        exchange.sendResponseHeaders(200, response.length());
        outputStreamWriter.write(response);
        outputStreamWriter.close();
    }
}
