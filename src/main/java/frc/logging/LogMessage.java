package frc.logging;

import java.util.ArrayList;

public class LogMessage {
    public String level;
    public String message;
    public ArrayList<LogField> fields;
    public int timestamp;
    private static int timestampCounter = 0;

    public LogMessage(int level, String message, ArrayList<LogField> fields) {
        this.level = Logger.fromLevelToString(level);
        this.message = message;
        this.fields = fields;
        timestamp = timestampCounter++;
    }
}