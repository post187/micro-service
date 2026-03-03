package com.example.Utils;

import io.netty.util.internal.logging.FormattingTuple;
import io.netty.util.internal.logging.MessageFormatter;
import org.springframework.cglib.core.Local;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageUtils {
    static ResourceBundle resourceBundle = ResourceBundle.getBundle("Message.message.properties", Locale.getDefault());

    public static String getMessage(String errorCode, Object... var2) {
        String message;
        try {
            message = resourceBundle.getString(errorCode);
        } catch (MissingResourceException ex) {
            // case message_code is not defined.
            message = errorCode;
        }
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(message, var2);
        return formattingTuple.getMessage();
    }
}
