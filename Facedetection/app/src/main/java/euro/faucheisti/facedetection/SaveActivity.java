package euro.faucheisti.facedetection;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class SaveActivity extends AppCompatActivity {

    private Bitmap photo;

    private ImageView imgView;
    private String imagePath;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        imgView = (ImageView) findViewById(R.id.imageView);

        Intent i = getIntent();
        imagePath = i.getStringExtra("imagePath");
        photo = loadFromFile(imagePath);

        imgView.setImageBitmap(photo);
    }

    public static Bitmap loadFromFile(String filename) {
        try {
            File f = new File(filename);
            if (!f.exists()) { return null; }
            return BitmapFactory.decodeFile(filename);
        } catch (Exception e) {
            return null;
        }
    }

    // Bouton pour partager l'image
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void partagerPhoto(View view) {

        File f = new File(imagePath);

        // Uri de l'image pour le transfert
        final Uri uri = FileProvider.getUriForFile(context, "euro.faucheisti.facedetection.fileprovider", f);

        // Intent de partage recevant l'uri en paramètre
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/png");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(sharingIntent, "Partager via"));

    }

    public void retourMenu(View view) {
        Intent mainActivity = new Intent(context, FaceDetection.class);
        startActivity(mainActivity);
    }
}
