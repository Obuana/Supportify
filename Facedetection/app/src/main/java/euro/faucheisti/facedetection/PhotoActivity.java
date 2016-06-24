package euro.faucheisti.facedetection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PhotoActivity extends AppCompatActivity {

    private String path = null;
    private String uriPath = null;

    private Context context = this;

    private Bitmap photo = null;

    private myView mView;
    private ArrayAdapter<String> adapter;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mView = (myView) findViewById(R.id.view);


        // Création et assignation d'une liste à la vue
        final ArrayList<String> listPays = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listPays);
        final ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);

        // Configuration de la liste de pays
        listPays.add("France");
        listPays.add("England");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();           }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Object o = ((ListView)arg0).getItemAtPosition(position);
                mView.setPays(o.toString());
            }
        });

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

    // Rafraichie la gallerie après avoir sauvegardé l'image
    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            MediaScannerConnection.scanFile(context, new String[]{mCurrentPhotoPath}, null, new MediaScannerConnection.OnScanCompletedListener() {

                @Override
                public void onScanCompleted(String path, Uri uri) {

                }
            });
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
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
            // Chemin de l'emplacement où la photot modifiée seras enregistrée
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

            callBroadCast();

        } catch(Exception e) {
            e.printStackTrace();
        }


        // Ouvre la dernière page de l'appli permettant de partager les résultats
        Intent saveActivity = new Intent(context, SaveActivity.class);
        saveActivity.putExtra("imagePath", mCurrentPhotoPath);
        startActivity(saveActivity);
    }
}
