package euro.faucheisti.facedetection;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FaceDetection extends AppCompatActivity {


    private Context context = this;

    private InterstitialAd mInterstitialAd;
    String mCurrentPhotoPath;


    private int OUVRIR_GALERIE = 1;
    private int PRENDRE_PHOTO = 2;

    private ImageView flag;
    private ImageView flag2;



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // Crée une pub en plein écran
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6592999730904348/2235101914");
        AdRequest adRequestInt = new AdRequest.Builder()
                .addTestDevice("B98205274F0FFB976C9A1618B7784EA0")
                .build();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Random gen = new Random();
                if (gen.nextDouble() < 0.5){
                    mInterstitialAd.show();
                }
            }
        });
        mInterstitialAd.loadAd(adRequestInt);

        BitmapFactory.Options BitmapFactoryOptionsbfo;

        BitmapFactoryOptionsbfo = new BitmapFactory.Options();
        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap flags = BitmapFactory.decodeResource(getResources(),R.drawable.flags,BitmapFactoryOptionsbfo);
        Bitmap flags2 = BitmapFactory.decodeResource(getResources(),R.drawable.flags,BitmapFactoryOptionsbfo);

        flag = (ImageView) findViewById(R.id.flag);
        flag.setImageBitmap(flags);

        flag2 = (ImageView) findViewById(R.id.flag2);
        flag2.setImageBitmap(flags2);


        // Demande la permission de lire des fichiers du stockage de l'appareil
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET},
                1);

        // Demande la permission d'écrire dans des fichiers du stockage de l'appareil
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                2);


        // Crée une bannière sur la page d'acceuil
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("B98205274F0FFB976C9A1618B7784EA0")
                .build();
        if (mAdView != null) {
            mAdView.loadAd(adRequest);
        }

    }

    @Override
    public void onResume(){
        super.onResume();

        // Animation de défilement des drapeaux
        TranslateAnimation anim = new TranslateAnimation(0.0f,(float) flag.getWidth(), 0.0f, 0.0f);
        anim.setDuration(10000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.REVERSE);
        flag.startAnimation(anim);

        TranslateAnimation anim2 = new TranslateAnimation(-((float) flag.getWidth()), 0.0f, 0.0f, 0.0f);
        anim2.setDuration(10000);
        anim2.setRepeatCount(Animation.INFINITE);
        anim2.setRepeatMode(Animation.REVERSE);
        flag2.startAnimation(anim2);
    }


    public void ouvrirGalerie(View view) {
        // Ouvre l'intent permettant à l'utilisateur de choisir une photo
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"), OUVRIR_GALERIE);

    }


    public void prendrePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }

            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(intent, PRENDRE_PHOTO);
            }
        }


    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //  Fonction executé quand une photo à été choisie
        if (requestCode == OUVRIR_GALERIE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            // Données récupérée de l'intent
            Uri uri = data.getData();

            String path = uri.toString();

            // Crée la vue contenant la photo modifiée et lui passe le chemin du bitmap de la photo choisie
            Intent photoActivity = new Intent(context, PhotoActivity.class);
            photoActivity.putExtra("uriPath", path);
            startActivity(photoActivity);

        //Fonction éxécutée quand une photo à été prise
        } else if (requestCode == PRENDRE_PHOTO ){
            if (resultCode ==  RESULT_OK  ){

                // Crée la vue contenant la photo modifiée et lui passe le  chemin du bitmap de la photo prise
                Intent photoActivity = new Intent(context, PhotoActivity.class);
                photoActivity.putExtra("Path", mCurrentPhotoPath);
                startActivity(photoActivity);

            }else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                Toast.makeText(this, "Prise de photo échouée", Toast.LENGTH_LONG).show();

            }

        }
    }




    // Code récupérer pour enregister une photo dans un fichier
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
