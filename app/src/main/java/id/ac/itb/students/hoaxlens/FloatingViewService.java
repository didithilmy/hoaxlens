package id.ac.itb.students.hoaxlens;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

public class FloatingViewService extends Service implements FloatingViewListener {

    private static final String TAG = "ChatHeadService";

    private static final int NOTIFICATION_ID = 9083150;

    private FloatingViewManager mFloatingViewManager;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (mFloatingViewManager != null) {
            return START_STICKY;
        }

        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final ImageView iconView = (ImageView) inflater.inflate(R.layout.widget_chathead, null, false);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, getString(R.string.chathead_click_message));
                Intent dialogIntent = new Intent(FloatingViewService.this, ScreenshotActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogIntent.putExtra(ScreenshotListenerService.SS_FILENAME, intent.getStringExtra(ScreenshotListenerService.SS_FILENAME));
                dialogIntent.putExtra(ScreenshotListenerService.SS_PATH, intent.getStringExtra(ScreenshotListenerService.SS_PATH));
                startActivity(dialogIntent);
                destroy();
            }
        });

        mFloatingViewManager = new FloatingViewManager(this, this);
        mFloatingViewManager.setFixedTrashIconImage(R.drawable.ic_trash_fixed);
        mFloatingViewManager.setActionTrashIconImage(R.drawable.ic_trash_action);
        final FloatingViewManager.Options options = new FloatingViewManager.Options();
        options.floatingViewWidth = (int) (64 * metrics.density);
        options.floatingViewHeight = (int) (64 * metrics.density);

        options.overMargin = (int) (-16 * metrics.density);
        options.floatingViewY = (int) (metrics.heightPixels /2);
        options.floatingViewX = (int) (metrics.widthPixels);

        options.shape = FloatingViewManager.SHAPE_CIRCLE;
        mFloatingViewManager.addViewToWindow(iconView, options);

        startForeground(NOTIFICATION_ID, createNotification(this));

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        destroy();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onFinishFloatingView() {
        stopSelf();
        //Log.d(TAG, getString(R.string.finish_deleted));
    }

    @Override
    public void onTouchFinished(boolean isFinishing, int x, int y) {
        if (isFinishing) {
           // Log.d(TAG, getString(R.string.deleted_soon));
        } else {
            //Log.d(TAG, getString(R.string.touch_finished_position, x, y));
        }
    }

    private void destroy() {
        if (mFloatingViewManager != null) {
            mFloatingViewManager.removeAllViewToWindow();
            mFloatingViewManager = null;
        }
    }

    private static Notification createNotification(Context context) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_generic");
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText(context.getString(R.string.app_name));
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setCategory(NotificationCompat.CATEGORY_SERVICE);

        return builder.build();
    }
}
