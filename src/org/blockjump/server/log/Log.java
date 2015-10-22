package org.blockjump.server.log;

import org.blockjump.server.Server;

public class Log {

    public static void log(String message, MessageState state) {
        if (canLog(state)) {
            if (state == MessageState.ERROR) {
                System.out.println("ERROR: " + message.toUpperCase());
            } else {
                System.out.println(message);
            }
        }
    }

    public static boolean canLog(MessageState state) {
        switch (state) {

            case ERROR:
                return true;

            case MESSAGE:
                if (Server.DEBUG == true) {
                    return true;
                }
                return false;

            case ENGINE:
                return true;

        }
        return false;
    }

}
