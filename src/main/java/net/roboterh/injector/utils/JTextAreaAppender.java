package net.roboterh.injector.utils;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;

public class JTextAreaAppender extends AppenderSkeleton {
    private static JTextArea textArea;

    public static void setTextArea(JTextArea textArea) {
        JTextAreaAppender.textArea = textArea;
    }

    @Override
    protected void append(LoggingEvent event) {
//        if (textArea != null && event.getMessage() != null) {
//            String message = event.getMessage().toString();
//            textArea.append(message + "\n");
//        }
        if (textArea != null && event.getMessage() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(event.getLevel().toString()).append("] ");
            sb.append(event.getLoggerName()).append(":");
            sb.append(event.getLocationInformation().getLineNumber()).append(" - ");
            sb.append(event.getMessage().toString());
            sb.append(Layout.LINE_SEP);
            textArea.append(sb.toString());
        }
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
