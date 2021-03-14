package frc.logging;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import io.javalin.Javalin;

public class LogServer {
    private boolean isRunning;

    public static class ServerRunnable implements Runnable {
        @Override
        public void run() {
            Javalin server = Javalin.create().start(9000);
            server.before(ctx -> {
                ctx.header("Access-Control-Allow-Origin", "*");
                System.out.println("Access Header Set");
            });

            server.get("/", ctx -> {
                File baseDirectory = new File(JSONHandler.BASE_DIRECTORY);
                ArrayList<String> directoryContents = new ArrayList<>();
                for(File logFile : baseDirectory.listFiles()) {
                    directoryContents.add(logFile.getName());
                }

                String filesString = String.join("\n", directoryContents);
                ctx.header("Content-Type", "text/plain");
                ctx.result(filesString);
            });

            server.get("/:name", ctx -> {
                File logFile = new File(JSONHandler.BASE_DIRECTORY + ctx.pathParam("name"));
                if(!logFile.exists()) {
                    ctx.status(400);
                    return;
                }

                ctx.header("Content-Type", "application/json");
                ctx.result(new String(Files.readAllBytes(Paths.get(logFile.getAbsolutePath()))));
            });
        }
    }

    public void startServer() {
        if(!isRunning) {
            (new Thread(new ServerRunnable())).start();
            isRunning = true;
        }
    }
}
