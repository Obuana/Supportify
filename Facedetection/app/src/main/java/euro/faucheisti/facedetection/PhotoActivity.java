package euro.faucheisti.facedetection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PhotoActivity extends AppCompatActivity {

    private String path = null;
    private String uriPath = null;

    private Context context = this;

    private Bitmap photo;

    private myView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mView = (myView) findViewById(R.id.view);


        Intent i = getIntent();

        if(i.getStringExtra("Path") != null){
            this.path = i.getStringExtra("Path");
            mView.setBitmapPath(path);
            //photo = Bitmap.createBitmap(mView.getMeasuredWidth(), mView.getMeasuredHeight(),Bitmap.Config.RGB_565);
        }

        if(i.getStringExtra("uriPath") != null){
            this.uriPath = i.getStringExtra("uriPath");
            mView.setBitmapUriPath(uriPath);
            //photo = Bitmap.createBitmap(width, height,Bitmap.Config.RGB_565);
        }

        //Canvas canvas = new Canvas(photo);

        //mView.draw(canvas);

    }

    public void retourMenu(View view) {
        Intent mainActivity = new Intent(context, FaceDetection.class);
        startActivity(mainActivity);

    }

    public void sauvegarderImage(View view) {
        Intent saveActivity = new Intent(context, SaveActivity.class);
        saveActivity.putExtra("image", photo);
        startActivity(saveActivity);
    }
}
