package org.eclipse.persistence.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.logging.Level;

/**
 * @author CJ
 */
public class CommonsLoggingLog extends AbstractSessionLog {

    private static final Log log = LogFactory.getLog(CommonsLoggingLog.class);

    public static final Level[] JAVA_LEVELS = new Level[]{
            Level.ALL, Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG, Level.INFO,
            Level.WARNING, Level.SEVERE, Level.OFF
    };

    @Override
    public void log(final SessionLogEntry sessionLogEntry) {
        Level level = JAVA_LEVELS[sessionLogEntry.level];

        doLog(level, sessionLogEntry);
//        BiPredicate<Integer, Level> work = (integer, level1) -> {
//            if (integer >= level1.intValue()) {
//                doLog(level1,sessionLogEntry);
//                return true;
//            }
//            return false;
//        };
//
//        for (Level level:levels){
//            if (work.test(sessionLogEntry.getLevel(),level))
//                return;
//        }
    }

    private void doLog(Level level, SessionLogEntry sessionLogEntry) {
        // parameters
        if (level == Level.SEVERE) {
            log.error(toMessage(sessionLogEntry), sessionLogEntry.getException());
        } else if (level == Level.WARNING) {
            log.warn(toMessage(sessionLogEntry), sessionLogEntry.getException());
        } else if (level == Level.INFO) {
            log.info(toMessage(sessionLogEntry), sessionLogEntry.getException());
        } else if (level == Level.CONFIG || level == Level.FINE | level == Level.FINER) {
            log.debug(toMessage(sessionLogEntry), sessionLogEntry.getException());
        } else if (level == Level.FINEST) {
            log.trace(toMessage(sessionLogEntry), sessionLogEntry.getException());
        }
    }

    private String toMessage(SessionLogEntry logEntry) {
        if (logEntry.getParameters() == null || logEntry.getParameters().length == 0)
            return logEntry.getMessage();
        StringBuilder stringBuilder = new StringBuilder(logEntry.getMessage());
        for (Object obj : logEntry.getParameters()) {
            stringBuilder.append(" ");
            stringBuilder.append(obj);
        }
        return stringBuilder.toString();
    }
}
