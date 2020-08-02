package com.github.derrop.cloudnettransformer.cloud.deserialized;

import org.fusesource.jansi.Ansi;

public class UserNote {

    private final Level level;
    private final String message;
    private Ansi ansi;

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

    public UserNote ansi(Ansi ansi) {
        this.ansi = ansi.a(this.message).reset();
        return this;
    }

    public Level getLevel() {
        return this.level;
    }

    public String getMessage() {
        return this.message;
    }

    public Ansi getAnsi() {
        return this.ansi;
    }

    public String format() {
        return String.format("[%s] %s", this.level.getPrefix(), this.message);
    }

    public String formatAnsi() {
        return String.format("[%s] %s", this.level.getAnsi() == null ? this.level.getPrefix() : this.level.getAnsi(), this.ansi == null ? this.message : this.ansi);
    }

    public enum Level {

        IMPORTANT("Important", Ansi.ansi().fgRed()),
        NORMAL("Info", Ansi.ansi().fgGreen()),
        UNNECESSARY("Unnecessary Info", null),
        UPGRADE("Upgrade Info", Ansi.ansi().fgBrightYellow());

        private final String prefix;
        private final Ansi ansi;

        Level(String prefix, Ansi ansi) {
            this.prefix = prefix;
            this.ansi = ansi == null ? null : ansi.a(this.prefix).reset();
        }

        public String getPrefix() {
            return this.prefix;
        }

        public Ansi getAnsi() {
            return this.ansi;
        }

    }

}
