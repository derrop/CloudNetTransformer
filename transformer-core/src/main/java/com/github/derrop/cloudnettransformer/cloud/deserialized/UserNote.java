package com.github.derrop.cloudnettransformer.cloud.deserialized;

public class UserNote {

    private final Level level;
    private final String message;

    public UserNote(Level level, String message) {
        this.level = level;
        this.message = message;
    }

    public static UserNote normal(String message) {
        return of(Level.NORMAL, message);
    }

    public static UserNote important(String message) {
        return of(Level.IMPORTANT, message);
    }

    public static UserNote unnecessary(String message) {
        return of(Level.UNNECESSARY, message);
    }

    public static UserNote upgrade(String message) {
        return of(Level.UPGRADE, message);
    }

    public static UserNote of(Level level, String message) {
        return new UserNote(level, message);
    }

    public Level getLevel() {
        return this.level;
    }

    public String getMessage() {
        return this.message;
    }

    public String format() {
        return String.format("[%s] %s", this.level.getPrefix(), this.message);
    }

    public enum Level {

        IMPORTANT("Important"),
        NORMAL("Info"),
        UNNECESSARY("Unnecessary Info"),
        UPGRADE("Upgrade Info");

        private final String prefix;

        Level(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return this.prefix;
        }
    }

}
