package euro.faucheisti.facedetection;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FaceDetection extends AppCompatActivity {


    private Context context = this;

    String mCurrentPhotoPath;


    private int OUVRIR_GALERIE = 1;
    private int PRENDRE_PHOTO = 2;



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // Demande la permission de lire des fichiers du stockage de l'appareil
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        // Demande la permission d'écrire dans des fichiers du stockage de l'appareil
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);


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
