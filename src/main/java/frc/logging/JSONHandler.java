package frc.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

public class JSONHandler implements LogHandler {
    private Gson gson;
    private BufferedWriter bufferedWriter;
    public static final String BASE_DIRECTORY = "/home/lvuser/logs";

    public JSONHandler(String fileName) {
        gson = new Gson();
        File file = new File(fileName);
        try {
            file.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(file));
        } catch(IOException e) {
            System.out.println("Failed to initialize the buffered writer" + e.getMessage());
        }
    }

    public void handleLogMessage(LogMessage logMessage) {
        if (bufferedWriter == null) {
            return;
        }
        
        String line = gson.toJson(logMessage);
        try {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch(IOException e) {
            System.out.println("Failed to write JSON to log file");
        }
    }
}