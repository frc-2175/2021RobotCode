package frc.logging;

public class LogField {
    public String name;
    public Object value;
    public String[] tags;

    public LogField(String name, Object value, String... tags) {
        this.name = name;
        this.value = value;
        this.tags = tags;
    }
}