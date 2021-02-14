package frc.logging;

import java.util.ArrayList;

public class LogMessage {
    public int level;
    public String message;
    public ArrayList<LogField> fields;

    public LogMessage(int level, String message, ArrayList<LogField> fields) {
        this.level = level;
        this.message = message;
        this.fields = fields;
    }
}
