package euro.faucheisti.facedetection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoActivity extends AppCompatActivity {

    private String path = null;
    private String uriPath = null;

    private Context context = this;

    private Bitmap photo = null;

    private myView mView;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mView = (myView) findViewById(R.id.view);


        Intent i = getIntent();

        // Configure la vue dans le cas d'une photo prise
        if(i.getStringExtra("Path") != null){
            this.path = i.getStringExtra("Path");
            mView.setBitmapPath(path);
        }

        // Configure la vue dans le cas d'une photo du stockage de l'appareil
        if(i.getStringExtra("uriPath") != null){
            this.uriPath = i.getStringExtra("uriPath");
            mView.setBitmapUriPath(uriPath);
        }




    }


    // Code du bouton de retour au menu principal
    public void retourMenu(View view) {
        Intent mainActivity = new Intent(context, FaceDetection.class);
        startActivity(mainActivity);

    }

    // Code du bouton pour envoyer la photo à la dernière activité pour sauvegarder
    public void sauvegarderImage(View view) {

        mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY));

        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());

        photo=mView.getResult();
        photo=photo.copy(Bitmap.Config.RGB_565,false);

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        try {
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            // CHemin de l'emplacement où la photot modifiée seras enregistrée
            mCurrentPhotoPath = image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Enregistre l'image modifiée sur l'appareil
        try {
            FileOutputStream out = new FileOutputStream(mCurrentPhotoPath);
            photo.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }


        // Ouvre la dernière page de l'appli permettant de partager les résultats
        Intent saveActivity = new Intent(context, SaveActivity.class);
        saveActivity.putExtra("imagePath", mCurrentPhotoPath);
        startActivity(saveActivity);
    }
}
