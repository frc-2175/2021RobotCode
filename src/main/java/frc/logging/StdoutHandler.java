package frc.logging;

public class StdoutHandler implements LogHandler {
    public void handleLogMessage(LogMessage logMessage) {
        System.out.println("Level: " + logMessage.level);
        System.out.println("-Message: " + logMessage.message);
        System.out.println("-Fields: ");
        for(LogField field : logMessage.fields) {
            System.out.println("--" + field.name + ": " + field.value);
        }
        System.out.println("-----------------------------");
    }
}