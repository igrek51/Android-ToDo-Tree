package igrek.todotree.logic.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import igrek.todotree.R;
import igrek.todotree.logger.Logs;

public abstract class BaseApp {

    public static final int FULLSCREEN_FLAG = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
    public static final boolean FULLSCREEN = false;
    public static final boolean HIDE_TASKBAR = true;
    public static final boolean KEEP_SCREEN_ON = false;

    public AppCompatActivity activity;
    private Thread.UncaughtExceptionHandler defaultUEH;
    protected Menu menu;

    boolean running = true;

    public BaseApp(AppCompatActivity aActivity) {
        this.activity = aActivity;

        //łapanie niezłapanych wyjątków
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable th) {
                Logs.errorUncaught(th);
                //przekazanie dalej do systemu operacyjnego
                defaultUEH.uncaughtException(thread, th);
            }
        });

        //schowanie paska tytułu
        if (HIDE_TASKBAR) {
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().hide();
            }
        }
        //fullscreen
        if (FULLSCREEN) {
            activity.getWindow().setFlags(FULLSCREEN_FLAG, FULLSCREEN_FLAG);
        }
        if (KEEP_SCREEN_ON) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        new Logs();

        Logs.info("Inicjalizacja aplikacji...");
    }

    public void pause() {

    }

    public void resume() {

    }

    public void quit() {
        if (!running) { //próba ponownego zamknięcia
            Logs.warn("Zamykanie - próba ponownego zamknięcia");
            return;
        }
        Logs.info("Zamykanie aplikacji...");
        running = false;
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        activity.finish();
    }

    public void onResizeEvent(Configuration newConfig) {
        int screenWidthDp = newConfig.screenWidthDp;
        int screenHeightDp = newConfig.screenHeightDp;
        int orientation = newConfig.orientation;
        int densityDpi = newConfig.densityDpi;
        Logs.info("Rozmiar ekranu zmieniony na: " + screenWidthDp + "dp x " + screenHeightDp + "dp (DPI = " + densityDpi + ")");
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Logs.info("Zmiana orientacji ekranu: landscape");
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Logs.info("Zmiana orientacji ekranu: portrait");
        }
    }

    public void menuInit(Menu menu){
        this.menu = menu;
    }

    public void setMenuItemVisible(int id, boolean visibility){
        MenuItem item = menu.findItem(id);
        if(item != null) {
            item.setVisible(visibility);
        }
    }

    public boolean onKeyBack() {
        quit();
        return true;
    }

    public boolean onKeyMenu() {
        return false;
    }

    public boolean optionsSelect(int id) {
        return false;
    }

    public void minimize() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(startMain);
    }

    //TODO poprawić na jedno show Info z możliwością wyboru akcji i zachowaniem domyślnym, domyślny widok
    public void showInfo(String info, View view) {
        final Snackbar snackbar = Snackbar.make(view, info, Snackbar.LENGTH_SHORT);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
        Logs.info(info);
    }

    public void showInfoCancellable(String info, View view, View.OnClickListener cancelCallback) {
        final Snackbar snackbar = Snackbar.make(view, info, Snackbar.LENGTH_LONG);
        snackbar.setAction("Cofnij", cancelCallback);
        snackbar.setActionTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        snackbar.show();
        Logs.info(info);
    }
}
