package id.ac.itb.students.hoaxlens;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.abangfadli.shotwatch.ScreenshotData;
import com.abangfadli.shotwatch.ShotWatch;

import java.net.URI;

public class ScreenshotListenerService extends Service {
    private Context appContext;
    private ShotWatch shotWatch;

    public final static String SS_FILENAME = "SS_FILENAME";
    public final static String SS_PATH = "SS_PATH";

    public ScreenshotListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d("ScreenshotListener", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ScreenshotListener", "onStartCommand");
        appContext = getBaseContext(); //Get the context here
        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            shotWatch = new ShotWatch(getContentResolver(), new ShotWatch.Listener() {
                @Override
                public void onScreenShotTaken(ScreenshotData screenshotData) {
                    onScreenshot(screenshotData);
                }
            });

            shotWatch.register();

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(shotWatch != null) shotWatch.unregister();
        super.onDestroy();
    }

    private void onScreenshot(ScreenshotData screenshotData) {
        if(null != appContext){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run()
                {
                    Toast.makeText(appContext, "Screenshot taken!", Toast.LENGTH_SHORT).show();
                }
            });

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(appContext)) {
                    // If permitted, show bubble overlay
                    final Intent intent = new Intent(appContext, FloatingViewService.class);

                    intent.putExtra(SS_FILENAME, screenshotData.getFileName());
                    intent.putExtra(SS_PATH, screenshotData.getPath());

                    ContextCompat.startForegroundService(appContext, intent);
                }
            } else {
                final Intent intent = new Intent(appContext, FloatingViewService.class);

                intent.putExtra(SS_FILENAME, screenshotData.getFileName());
                intent.putExtra(SS_PATH, screenshotData.getPath());

                ContextCompat.startForegroundService(appContext, intent);
            }
        }
    }
}
