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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.Random;

public class SaveActivity extends AppCompatActivity {

    private Bitmap photo;

    private ImageView imgView;
    private String imagePath;
    private Context context = this;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        // Crée une pub en plein écran
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6592999730904348/1703391513");
        AdRequest adRequestInt = new AdRequest.Builder()
                .addTestDevice("B98205274F0FFB976C9A1618B7784EA0")
                .build();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Random gen = new Random();
                System.out.println(gen.nextDouble());
                if (gen.nextDouble() < 0.6){
                    mInterstitialAd.show();
                }
            }
        });
        mInterstitialAd.loadAd(adRequestInt);

        // Crée une bannière sur la page d'acceuil
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("B98205274F0FFB976C9A1618B7784EA0")
                .build();
        if (mAdView != null) {
            mAdView.loadAd(adRequest);
        }

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
