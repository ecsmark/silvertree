package com.silvertree.tombstone;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.*;

public class GameLogging {
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
    private final static Logger LOGGER = Logger.getLogger("InfoLogger");

    public static Logger setup() {
        try {
            FileHandler fileHandler = new FileHandler("tombstonecity.log");
            Formatter formatter = new Formatter() {
                @Override
                public String format(LogRecord record) {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(LocalTime.ofInstant(new Date(record.getMillis()).toInstant(), ZoneId.systemDefault()).format(timeFormatter));
                    buffer.append(",");
                    buffer.append(record.getLoggerName());
                    buffer.append(",");
                    buffer.append(record.getLevel().getName());
                    buffer.append(",[");
                    buffer.append(Thread.currentThread().getName());
                    buffer.append("],");
                    buffer.append(record.getMessage());
                    buffer.append("\r\n");
                    return buffer.toString();

                }
            };

            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return LOGGER ;
    }
    public static Logger getLogger() {return LOGGER;}

    public static void debug(String message){
        LOGGER.info(message);
    }
    public static void warning(String message){
        LOGGER.log(Level.WARNING, message);
    }
}
