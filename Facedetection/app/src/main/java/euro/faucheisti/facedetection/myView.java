package euro.faucheisti.facedetection;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Bundle;
import android.view.View;

public class myView extends View {

    private int imageWidth;
    private int imageHeight;
    private int numberOfFace = 5;
    private FaceDetector myFaceDetect;
    private FaceDetector.Face[] myFace;
    private float myEyesDistance;
    int numberOfFaceDetected;
    private Bundle data = new Bundle();

    Bitmap myBitmap;
    Bitmap myBitmap2;



    public myView(Context context, Bitmap myBitmap){
        super(context);

        BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
        this.myBitmap = myBitmap;

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

        if(numberOfFaceDetected!=0) {
            FaceDetector.Face face = myFace[0];
            PointF myMidPoint = new PointF();
            face.getMidPoint(myMidPoint);
            myEyesDistance = face.eyesDistance();
            myBitmap2 = Bitmap.createScaledBitmap(myBitmap2,(int) myEyesDistance * 5, (int)( myEyesDistance * 5),true);

            canvas.drawBitmap(myBitmap2,(float) (myMidPoint.x - myEyesDistance * 2.5),(float) (myMidPoint.y - myEyesDistance * 3.4), null);
        }

    }


}

