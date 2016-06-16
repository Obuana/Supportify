package euro.faucheisti.facedetection;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FaceDetection extends AppCompatActivity {


    private Context context = this;

    private Bitmap myBitmap;

    String mCurrentPhotoPath;
    private Uri fileUri;


    private int OUVRIR_GALERIE = 1;
    private int PRENDRE_PHOTO = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);

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
                System.out.println(photoFile);
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

            try {

                    // Création du bitmap à partir de l'uri
                Bitmap tmpBitmap;
                BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
                BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
                tmpBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                myBitmap = tmpBitmap.copy(Bitmap.Config.RGB_565, true);

                // Code récupérer permettant de tourner la photo suivant l'orientation de l'appareil
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(getPath(context,uri));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int orientation = ExifInterface.ORIENTATION_NORMAL;

                if (exif != null)
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        myBitmap = rotateBitmap(myBitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        myBitmap = rotateBitmap(myBitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        myBitmap = rotateBitmap(myBitmap, 270);
                        break;
                }


                // Crée la vue contenant la photo modifiée et lui passe le bitmap de la photo choisie
                myView mView = new myView(context, myBitmap);
                setContentView(mView);


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PRENDRE_PHOTO ){
            if (resultCode == RESULT_OK  ){

                // Récupère la photo créée par l'appareil dans un bitamp pour la passée à la vue
                BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
                BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
                BitmapFactoryOptionsbfo.inJustDecodeBounds = false;
                System.out.println(mCurrentPhotoPath);
                myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, BitmapFactoryOptionsbfo);

                // Code récupérer permettant de tourner la photo suivant l'orientation de l'appareil
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(mCurrentPhotoPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int orientation = ExifInterface.ORIENTATION_NORMAL;

                if (exif != null)
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        myBitmap = rotateBitmap(myBitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        myBitmap = rotateBitmap(myBitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        myBitmap = rotateBitmap(myBitmap, 270);
                        break;
                }

                // Crée la vue contenant la photo modifiée et lui passe le bitmap de la photo prise
                myView mView = new myView(context, myBitmap);
                setContentView(mView);

            }else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                Toast.makeText(this, "Prise de photo échouée", Toast.LENGTH_LONG).show();

            }

        }
    }


    // Code récupérer permettant de tourner la photo suivant l'orientation de l'appareil
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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
