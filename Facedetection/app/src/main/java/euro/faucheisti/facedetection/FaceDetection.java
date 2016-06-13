package euro.faucheisti.facedetection;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class FaceDetection extends AppCompatActivity {

    Context test = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new myView(this));
    }

    public void openGallery(View view) {
    }

    private class myView extends View {

        private int imageWidth;
        private int imageHeight;
        private int numberOfFace =5;
        private FaceDetector myFaceDetect;
        private FaceDetector.Face[] myFace;
        float myEyesDistance;
        int numberOfFaceDetected;

        Bitmap myBitmap;
        Bitmap myBitmap2;
        private String userChoosenTask;

        public myView(Context context){
            super(context);

            BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
            BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);


            myBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.face,BitmapFactoryOptionsbfo);
            myBitmap2 = BitmapFactory.decodeResource(getResources(),R.drawable.perruque,BitmapFactoryOptionsbfo);

        }



        @Override
        protected void onDraw(Canvas canvas) {

            myBitmap = Bitmap.createScaledBitmap(myBitmap, this.getWidth(), this.getHeight(),true);
            imageWidth = myBitmap.getWidth();
            imageHeight = myBitmap.getHeight();
            myFace = new FaceDetector.Face[numberOfFace];
            myFaceDetect = new FaceDetector(imageWidth, imageHeight, numberOfFace);
            numberOfFaceDetected = myFaceDetect.findFaces(myBitmap, myFace);
            canvas.drawBitmap(myBitmap, 0, 0, null);
            Paint myPaint = new Paint();
            myPaint.setColor(Color.GREEN);
            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(3);

                FaceDetector.Face face = myFace[0];
                PointF myMidPoint = new PointF();
                face.getMidPoint(myMidPoint);
                myEyesDistance = face.eyesDistance();

                canvas.drawRect((int) (myMidPoint.x - myEyesDistance * 2), (int) (myMidPoint.y - myEyesDistance * 2), (int) (myMidPoint.x + myEyesDistance * 2), (int) (myMidPoint.y + myEyesDistance * 2), myPaint);
                canvas.drawBitmap(myBitmap2,myMidPoint.x - myEyesDistance * 2,myMidPoint.y - myEyesDistance * 2,null);





        }


    }


}
