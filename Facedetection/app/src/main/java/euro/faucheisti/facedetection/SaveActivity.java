package euro.faucheisti.facedetection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class SaveActivity extends AppCompatActivity {

    private Bitmap photo;

    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        imgView = (ImageView) findViewById(R.id.imageView);

        Intent i = getIntent();
        photo = i.getParcelableExtra("image");

        imgView.setImageBitmap(photo);
    }
}
