package euro.faucheisti.facedetection;


import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;

import java.io.File;
import java.io.IOException;

public class myView extends View {

    private int imageWidth;
    private int imageHeight;
    private int numberOfFace = 5;
    private FaceDetector myFaceDetect;
    private FaceDetector.Face[] myFace;
    private float myEyesDistance;
    int numberOfFaceDetected;

    private String bitmapUriPath;
    private String bitmapPath;

    BitmapFactory.Options BitmapFactoryOptionsbfo;

    Bitmap maPhoto;
    Bitmap perruque;
    Bitmap maillot;
    Bitmap result;

    int size;

    Context context;

    public myView(Context context, AttributeSet attrs){
        super(context, attrs);

        this.context = context;

        setDrawingCacheEnabled(true);

        TypedArray attr = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.myView, 0, 0);

        BitmapFactoryOptionsbfo = new BitmapFactory.Options();
        BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmapUriPath = attr.getString(R.styleable.myView_bitmapUriPath);
        bitmapPath = attr.getString(R.styleable.myView_bitmapPath);



        perruque = BitmapFactory.decodeResource(getResources(),R.drawable.perruque,BitmapFactoryOptionsbfo);
        maillot = BitmapFactory.decodeResource(getResources(),R.drawable.maillot2,BitmapFactoryOptionsbfo);
        size = maillot.getHeight();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        ExifInterface exif = null;
        try {
            if(!bitmapUriPath.equals("")) {
                maPhoto = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(bitmapUriPath));
                maPhoto = maPhoto.copy(Bitmap.Config.RGB_565, true);
                exif = new ExifInterface(getPath(context, Uri.parse(bitmapUriPath)));
                bitmapUriPath = "";

            }else if(!bitmapPath.equals("")){
                maPhoto = BitmapFactory.decodeFile(bitmapPath, BitmapFactoryOptionsbfo);
                exif = new ExifInterface(bitmapPath);
                deleteImage(bitmapPath);
                bitmapPath = "";

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (maPhoto != null) {
            // Code récupérer permettant de tourner la photo suivant l'orientation de l'appareil
            int orientation = ExifInterface.ORIENTATION_NORMAL;

            if (exif != null)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    maPhoto = rotateBitmap(maPhoto, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    maPhoto = rotateBitmap(maPhoto, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    maPhoto = rotateBitmap(maPhoto, 270);
                    break;
            }

        }

        if (maPhoto != null) {
            result = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.RGB_565);
            Canvas temp = new Canvas(result);
            maPhoto = Bitmap.createScaledBitmap(maPhoto, this.getWidth(), this.getHeight(), true);
            imageWidth = maPhoto.getWidth();
            imageHeight = maPhoto.getHeight();

            // Détecte les visages
            myFace = new FaceDetector.Face[numberOfFace];
            myFaceDetect = new FaceDetector(imageWidth, imageHeight, numberOfFace);
            numberOfFaceDetected = myFaceDetect.findFaces(maPhoto, myFace);
            temp.drawBitmap(maPhoto, 0, 0, null);


            ///Dessine la perruque
            if (numberOfFaceDetected != 0) {
                FaceDetector.Face face = myFace[0];
                PointF myMidPoint = new PointF();
                face.getMidPoint(myMidPoint);
                myEyesDistance = face.eyesDistance();
                perruque = Bitmap.createScaledBitmap(perruque, (int) myEyesDistance * 5, (int) (myEyesDistance * 5), true);
                maillot = Bitmap.createScaledBitmap(maillot, (int) (myEyesDistance * 8), (int) (myEyesDistance * 9.2), true);

                temp.drawBitmap(maillot, (float) (myMidPoint.x - myEyesDistance * 4.1), (float) (myMidPoint.y + myEyesDistance * 1.8), null);
                temp.drawBitmap(perruque, (float) (myMidPoint.x - myEyesDistance * 2.6), (float) (myMidPoint.y - myEyesDistance * 3.4), null);

            }
            canvas.drawBitmap(result, 0, 0, null);
        }
    }


    // Fonction onMeasure permettant à la vue de se mesurer pour obtenir ses dimenssions dans l'activité
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //MUST CALL THIS
        setMeasuredDimension(widthSize, heightSize);
    }

    public void setBitmapUriPath(String bitmapUriPath) {
        this.bitmapUriPath = bitmapUriPath;
        invalidate();
        requestLayout();
    }

    public void setBitmapPath(String bitmapPath) {
        this.bitmapPath = bitmapPath;
        invalidate();
        requestLayout();
    }

    // Supprime la photo prise dans l'appli
    public void deleteImage(String Path) {
        File fdelete = new File(Path);
        fdelete.delete();
    }

    // Getter pour le bitmap contenant l'image modifiée
    public Bitmap getResult() {
        return result;
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


}

