package igrek.todotree.output;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Output {

    private static List<String> echoes;
    private static int errors = 0;

    private static final LogLevel CONSOLE_LEVEL = LogLevel.DEBUG; //widoczne w konsoli
    private static final LogLevel ECHO_LEVEL = LogLevel.OFF; //widoczne dla użytkownika (i przechowywane w historii)

    private static final String LOG_TAG = "ylog";
    private static final boolean SHOW_EXCEPTIONS_TRACE = true;
    private static final LogLevel SHOW_EXECUTION_DETAILS_LEVEL = LogLevel.DEBUG;

    public Output() {
        reset();
    }

    public static void reset() {
        echoes = new ArrayList<>();
        errors = 0;
    }

    public static void error(String message) {
        errors++;
        log(message, LogLevel.ERROR, "[ERROR] ");
    }

    public static void error(Throwable ex) {
        errors++;
        log(ex.getMessage(), LogLevel.ERROR, "[EXCEPTION - " + ex.getClass().getName() + "] ");
        printStackTrace(ex);
    }

    public static void errorUncaught(Throwable ex) {
        errors++;
        log(ex.getMessage(), LogLevel.ERROR, "[UNCAUGHT EXCEPTION - " + ex.getClass().getName() + "] ");
        printStackTrace(ex);
    }

    public static void errorCritical(final Activity activity, String e) {
        errors++;
        log(e, LogLevel.ERROR, "[CRITICAL ERROR] ");
        if (activity == null) {
            error("errorCritical: Brak activity");
            return;
        }
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(activity);
        dlgAlert.setMessage(e);
        dlgAlert.setTitle("Błąd krytyczny");
        dlgAlert.setPositiveButton("Zamknij aplikację", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    public static void errorCritical(final Activity activity, Throwable ex) {
        String e = ex.getClass().getName() + " - " + ex.getMessage();
        printStackTrace(ex);
        errorCritical(activity, e);
    }

    public static void warn(String message) {
        log(message, LogLevel.WARN, "[warn] ");
    }

    public static void info(String message) {
        log(message, LogLevel.INFO, "");
    }

    public static void debug(String message) {
        log(message, LogLevel.DEBUG, "[debug] ");
    }


    private static void log(String message, LogLevel level, String logPrefix) {

        if (level.getLevelNumber() <= CONSOLE_LEVEL.getLevelNumber()) {

            String consoleMessage;
            if (level.getLevelNumber() <= SHOW_EXECUTION_DETAILS_LEVEL.getLevelNumber()) {
                final int stackTraceIndex = 4;

                String methodName = Thread.currentThread().getStackTrace()[stackTraceIndex].getMethodName();
                String fileName = Thread.currentThread().getStackTrace()[stackTraceIndex].getFileName();
                int lineNumber = Thread.currentThread().getStackTrace()[stackTraceIndex].getLineNumber();

                consoleMessage = logPrefix + errorPrefix() + "(" + fileName + ":" + lineNumber + ")." + methodName + ": " + message;
            } else {
                consoleMessage = logPrefix + errorPrefix() + message;
            }

            if (level.equals(LogLevel.ERROR)) {
                Log.e(LOG_TAG, consoleMessage);
            } else {
                Log.i(LOG_TAG, consoleMessage);
            }
        }
        if (level.getLevelNumber() <= ECHO_LEVEL.getLevelNumber()) {
            echoes.add(logPrefix + message);
        }
    }

    private static void printStackTrace(Throwable ex) {
        if (SHOW_EXCEPTIONS_TRACE) {
            Log.e(LOG_TAG, Log.getStackTraceString(ex));
        }
    }

    private static String errorPrefix() {
        return (errors > 0) ? ("[E:" + errors + "] ") : "";
    }

    //  ECHOES - widoczne dla użytkownika

    public static List<String> getEchoes() {
        return echoes;
    }

    /**
     * zdejmuje ostatni komunikat (zgodnie z FIFO) i zwraca go
     */
    public static String echoPopLine() {
        if (echoes.isEmpty()) {
            return null;
        } else {
            String line = echoes.get(0);
            echoes.remove(0);
            return line;
        }
    }

    /**
     * @return komunikaty złączone znakami \n (od najstarszego)
     */
    public static String getEchoesMultiline() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < echoes.size(); i++) {
            builder.append(echoes.get(i));
            if (i < echoes.size() - 1) builder.append('\n');
        }
        return builder.toString();
    }

    /**
     * @return komunikaty złączone znakami \n w odwrotnej kolejności (od najnowszego)
     */
    public static String getEchoesMultilineReversed() {
        StringBuilder builder = new StringBuilder();
        for (int i = echoes.size() - 1; i >= 0; i--) {
            builder.append(echoes.get(i));
            if (i > 0) builder.append('\n');
        }
        return builder.toString();
    }

    public static void dupa() {
        log("DUPA " + System.currentTimeMillis(), LogLevel.DEBUG, "[debug] ");
    }
}
