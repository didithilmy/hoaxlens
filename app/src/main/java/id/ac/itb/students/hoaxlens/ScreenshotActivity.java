package id.ac.itb.students.hoaxlens;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.io.IOException;

public class ScreenshotActivity extends AppCompatActivity {

    RelativeLayout rlLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot);

        rlLoading = findViewById(R.id.rl_loading);

        rlLoading.setVisibility(View.VISIBLE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        FirebaseApp.initializeApp(this);

        try {
            Uri uri = Uri.fromFile(new File(getIntent().getStringExtra(ScreenshotListenerService.SS_PATH)));
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

            FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();

            final Task<FirebaseVisionText> result = detector.detectInImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            // Task completed successfully
                            for (FirebaseVisionText.Block block: firebaseVisionText.getBlocks()) {
                                Rect boundingBox = block.getBoundingBox();
                                Point[] cornerPoints = block.getCornerPoints();
                                String text = block.getText();

                                Log.d("SSA", text);
                            }

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 5s
                                }
                            }, 5000);

                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                    finish();
                                }
                            });
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }
}
