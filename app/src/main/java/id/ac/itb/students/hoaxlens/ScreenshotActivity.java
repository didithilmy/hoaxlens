package id.ac.itb.students.hoaxlens;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.File;
import java.io.IOException;

public class ScreenshotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot);

        FirebaseApp.initializeApp(this);

        try {
            Uri uri = Uri.fromFile(new File(getIntent().getStringExtra(ScreenshotListenerService.SS_PATH)));
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

            FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();

            Task<FirebaseVisionText> result = detector.detectInImage(image)
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
