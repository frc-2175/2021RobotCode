package frc.logging;

import java.util.ArrayList;
import java.util.Arrays;

import frc.ServiceLocator;

public class Logger {
    public ArrayList<LogField> baseFields;
    public LogHandler[] handlers;
    public static final int DEBUG = 0;
    public static final int INFO = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;
    public static final String SMART_DASHBOARD_TAG = "SmartDashboard";

    /**
     * Constructs a new logger object.
     * @param handlers An array of LogHandlers meant to take each call of the log methods
     * and handle the level, fields, and tags associated with the call.
     * @param isBaseLogger whether or not this logger should be registered with the service
     * locator
     * @param baseFields a variable number of LogFields that will be appended to the fields
     * input into the log methods.
     */
    public Logger(LogHandler[] handlers, boolean isBaseLogger, LogField... baseFields) {
        if(isBaseLogger) {
            ServiceLocator.register(this);
        }
        this.handlers = handlers;
        this.baseFields = new ArrayList<>(Arrays.asList(baseFields));
    }

    /**
     * Constructs a new logger object.
     * @param handlers An array of LogHandlers meant to take each call of the log methods
     * and handle the level, fields, and tags associated with the call.
     * @param baseFields a variable number of LogFields that will be appended to the fields
     * input into the log methods.
     */
    public Logger(LogHandler[] handlers, LogField... baseFields) {
        this(handlers, false, baseFields);
    }


    /**
     * Sends a log message and a group of fields (including the base fields)
     * to the handlers defined in the constructor.
     * @param level the level at which to log. Use static constants DEBUG (0), INFO (1),
     * WARNING (2), and ERROR(3). Using a number outside the range 0-4 will display the level
     * as a "?".
     * @param message a message to display with the log.
     * @param fieldsArray a variable number of fields to display with the log message.
     */
    public void log(int level, String message, LogField... fieldsArray) {
        ArrayList<LogField> fields = new ArrayList<>(Arrays.asList(fieldsArray));
        for(int i = 0; i < baseFields.size(); i++) {
            fields.add(i, baseFields.get(i));
        }
        for(LogHandler handler : handlers) {
            handler.handleLogMessage(new LogMessage(level, message, fields));
        }
    }

    /**
     * Sends a log message and a group of fields at the DEBUG level.
     * @param message a message to display with the log.
     * @param fieldsArray a variable number of fields to display with the log message.
     * @see #log(int, String, LogField...)
     */
    public void debug(String message, LogField... fieldsArray) {
        log(DEBUG, message, fieldsArray);
    }

    /**
     * Sends a log message and a group of fields at the INFO level.
     * @param message a message to display with the log.
     * @param fieldsArray a variable number of fields to display with the log message.
     * @see #log(int, String, LogField...)
     */
    public void info(String message, LogField... fieldsArray) {
        log(INFO, message, fieldsArray);
    }

    /**
     * Sends a log message and a group of fields at the WARNING level.
     * @param message a message to display with the log.
     * @param fieldsArray a variable number of fields to display with the log message.
     * @see #log(int, String, LogField...)
     */
    public void warning(String message, LogField... fieldsArray) {
        log(WARNING, message, fieldsArray);
    }

    /**
     * Sends a log message and a group of fields at the ERROR level.
     * @param message a message to display with the log.
     * @param fieldsArray a variable number of fields to display with the log message.
     * @see #log(int, String, LogField...)
     */
    public void error(String message, LogField... fieldsArray) {
        log(ERROR, message, fieldsArray);
    }

    public Logger newWithExtraFields(LogField... newBaseFieldsArray) {
        ArrayList<LogField> newBaseFields = new ArrayList<>(Arrays.asList(newBaseFieldsArray));
        for(int i = 0; i < baseFields.size(); i++) {
            newBaseFields.add(i, baseFields.get(i));
        }

        return new Logger(handlers, newBaseFields.toArray(new LogField[0]));
    }

    /**
     * Converts from a log level integer to an appropriate string.
     */
    public static String fromLevelToString(int level) {
        switch(level) {
            case DEBUG:
                return "DEBUG";
            case INFO:
                return "INFO";
            case WARNING:
                return "WARNING";
            case ERROR:
                return "ERROR";
            default:
                return "?";
        }
    }
}
