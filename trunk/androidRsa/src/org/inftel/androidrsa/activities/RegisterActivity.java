
package org.inftel.androidrsa.activities;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.steganography.MobiProgressBar;
import org.inftel.androidrsa.utils.AndroidRsaConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class RegisterActivity extends Activity {

    private static final String TAG = "RegisterActivity";
    private static final int ACTIVITY_SELECT_IMAGE = 100;

    private String[] mFileList;
    private File mChosenFile;
    private String mChosenFileString;
    private Bitmap mChosenImage;
    private String mChosenImagePath;
    private MobiProgressBar progressBar;
    private final Handler handler = new Handler();
    private Context context;

    private static final int DIALOG_LOAD_FILE = 1000;
    private static final int DIALOG_RUN_ONCE = 1001;
    private static final int DIALOG_NOT_CHOSEN = 1002;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);

        // Get a reference to the Shared Preferences and a Shared Preference
        // Editor.
        SharedPreferences prefs = getSharedPreferences(AndroidRsaConstants.SHARED_PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        Editor prefsEditor = prefs.edit();

        // Save that we've been run once.
        if (prefs.getBoolean(AndroidRsaConstants.SP_KEY_RUN_ONCE, false)) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        } else {
            prefsEditor.putBoolean(AndroidRsaConstants.SP_KEY_RUN_ONCE, true);
            showDialog(DIALOG_RUN_ONCE);
        }
    }

    public void onClickPickImage(View view) throws IOException {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
    }

    // Find *.crt in the sd
    private void loadFileList() {
        File mPath = AndroidRsaConstants.EXTERNAL_SD_PATH;
        // try {
        // mPath.mkdirs();
        // } catch (SecurityException e) {
        // Log.e(TAG, "unable to write on the sd card " + e.toString());
        // }
        if (mPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    return filename.contains(AndroidRsaConstants.FTYPE);
                }
            };
            mFileList = mPath.list(filter);
        }
        else {
            mFileList = new String[0];
        }
    }

    // Dialogs
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new Builder(this);

        switch (id) {
            case DIALOG_LOAD_FILE:
                if (mFileList == null) {
                    builder.setMessage(R.string.not_found)
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                } else {
                    builder.setTitle(R.string.choose_file);
                    builder.setSingleChoiceItems(mFileList, -1,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mChosenFileString = mFileList[which];
                                }
                            }).setPositiveButton(getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mChosenFile = new File(AndroidRsaConstants.EXTERNAL_SD_PATH
                                            + File.separator + mChosenFileString);

                                    dialog.dismiss();
                                }
                            });
                }
                break;
            case DIALOG_RUN_ONCE:
                builder.setMessage(R.string.first_time_configuration)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                break;

            case DIALOG_NOT_CHOSEN:
                builder.setMessage(R.string.not_chosen)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                break;
        }
        dialog = builder.show();
        return dialog;
    }

    public void onClickButtonPickCertificate(View view) throws IOException {
        loadFileList();
        showDialog(DIALOG_LOAD_FILE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case ACTIVITY_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {
                            MediaStore.Images.Media.DATA
                    };

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null,
                            null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mChosenImagePath = cursor.getString(columnIndex);
                    cursor.close();

                    mChosenImage = BitmapFactory.decodeFile(mChosenImagePath);

                    ImageView img = (ImageView) findViewById(R.id.image);
                    img.setImageBitmap(mChosenImage);

                }
        }
    }

    public void onClickButtonDone(View view) throws IOException {

        if (mChosenFile != null && mChosenImage != null) {

            // saving the key's path and cert's path

            // progressBar = new MobiProgressBar(RegisterActivity.this);
            // progressBar.setMax(100);
            // progressBar.setMessage("Enconding");
            // progressBar.show();
            // Thread tt = new Thread(new Runnable() {
            // public void run() {
            // encode(converToString(mChosenFile), mChosenImage,
            // mChosenImagePath);
            // handler.post(mShowAlert);
            //
            // }
            // });
            // tt.start();
            //
            // Intent i = new Intent(this, LoginActivity.class);
            // startActivity(i);

            Intent i = new Intent(getApplicationContext(), EncodeActivity.class);
            i.putExtra(AndroidRsaConstants.FILE_PATH, AndroidRsaConstants.EXTERNAL_SD_PATH
                    + File.separator + mChosenFileString);
            i.putExtra(AndroidRsaConstants.IMAGE_PATH, mChosenImagePath);
            startActivity(i);

        } else {
            showDialog(DIALOG_NOT_CHOSEN);
        }

    }

    // private String converToString(File file) {
    // FileInputStream fis;
    // StringBuilder total = new StringBuilder();
    // try {
    // fis = new FileInputStream(file);
    // BufferedReader r = new BufferedReader(new InputStreamReader(fis));
    // String line;
    // while ((line = r.readLine()) != null) {
    // total.append(line);
    // }
    //
    // } catch (FileNotFoundException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // return total.toString();
    // }
    //
    // final Runnable mShowAlert = new Runnable() {
    // public void run() {
    // progressBar.dismiss();
    // AlertDialog.Builder builder = new AlertDialog.Builder(
    // context);
    // builder.setMessage("Saved")
    // .setCancelable(false).setPositiveButton(
    // context.getText(R.string.ok),
    // new DialogInterface.OnClickListener() {
    // public void onClick(
    // DialogInterface dialog,
    // int id) {
    // RegisterActivity.this.finish();
    // }
    // });
    //
    // AlertDialog alert = builder.create();
    // alert.show();
    // }
    // };
    //
    // private Uri encode(String s, Bitmap sourceBitmap, String
    // absoluteFilePathSource) {
    //
    // Uri result = null;
    //
    // int width = sourceBitmap.getWidth();
    // int height = sourceBitmap.getHeight();
    //
    // int[] oneD = new int[width * height];
    // sourceBitmap.getPixels(oneD, 0, width, 0, 0, width, height);
    // int density = sourceBitmap.getDensity();
    // sourceBitmap.recycle();
    // byte[] byteImage = LSB2bit.encodeMessage(oneD, width, height, s,
    // new ProgressHandler() {
    // private int mysize;
    // private int actualSize;
    //
    // public void increment(final int inc) {
    // actualSize += inc;
    // if (actualSize % mysize == 0)
    // handler.post(mIncrementProgress);
    // }
    //
    // public void setTotal(final int tot) {
    // mysize = tot / 50;
    // handler.post(mInitializeProgress);
    // }
    //
    // public void finished() {
    //
    // }
    // });
    // oneD = null;
    // sourceBitmap = null;
    // int[] oneDMod = LSB2bit.byteArrayToIntArray(byteImage);
    // byteImage = null;
    // Log.v("Encode", "" + oneDMod[0]);
    // Log.v("Encode Alpha", "" + (oneDMod[0] >> 24 & 0xFF));
    // Log.v("Encode Red", "" + (oneDMod[0] >> 16 & 0xFF));
    // Log.v("Encode Green", "" + (oneDMod[0] >> 8 & 0xFF));
    // Log.v("Encode Blue", "" + (oneDMod[0] & 0xFF));
    //
    // System.gc();
    // Log.v("Free memory", Runtime.getRuntime().freeMemory() + "");
    // Log.v("Image mesure", (width * height * 32 / 8) + "");
    //
    // Bitmap destBitmap = Bitmap.createBitmap(width, height,
    // Config.ARGB_8888);
    //
    // destBitmap.setDensity(density);
    // int partialProgr = height * width / 50;
    // int masterIndex = 0;
    // for (int j = 0; j < height; j++)
    // for (int i = 0; i < width; i++) {
    // // The unique way to write correctly the sourceBitmap, android
    // // bug!!!
    // destBitmap.setPixel(i, j, Color.argb(0xFF,
    // oneDMod[masterIndex] >> 16 & 0xFF,
    // oneDMod[masterIndex] >> 8 & 0xFF,
    // oneDMod[masterIndex++] & 0xFF));
    // if (masterIndex % partialProgr == 0)
    // handler.post(mIncrementProgress);
    // }
    // handler.post(mSetInderminate);
    // Log.v("Encode", "" + destBitmap.getPixel(0, 0));
    // Log.v("Encode Alpha", "" + (destBitmap.getPixel(0, 0) >> 24 & 0xFF));
    // Log.v("Encode Red", "" + (destBitmap.getPixel(0, 0) >> 16 & 0xFF));
    // Log.v("Encode Green", "" + (destBitmap.getPixel(0, 0) >> 8 & 0xFF));
    // Log.v("Encode Blue", "" + (destBitmap.getPixel(0, 0) & 0xFF));
    //
    // String sdcardState = android.os.Environment.getExternalStorageState();
    // String destPath = null;
    // int indexSepar = absoluteFilePathSource.lastIndexOf(File.separator);
    // int indexPoint = absoluteFilePathSource.lastIndexOf(".");
    // if (indexPoint <= 1)
    // indexPoint = absoluteFilePathSource.length();
    // String fileNameDest = absoluteFilePathSource.substring(indexSepar + 1,
    // indexPoint);
    // fileNameDest += AndroidRsaConstants.ENCODED_IMAGE_NAME;
    // if (sdcardState.contentEquals(android.os.Environment.MEDIA_MOUNTED))
    // destPath = android.os.Environment.getExternalStorageDirectory()
    // + File.separator + fileNameDest + ".png";
    //
    // OutputStream fout = null;
    // try {
    //
    // Log.v("Path", destPath);
    // fout = new FileOutputStream(destPath);
    // destBitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
    // // Media.insertImage(getContentResolver(),destPath, fileNameDest,
    // // "MobiStego Encoded");
    // result = Uri.parse("file://" + destPath);
    // sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
    // fout.flush();
    // fout.close();
    //
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // destBitmap.recycle();
    // return result;
    // }
    //
    // final Runnable mIncrementProgress = new Runnable() {
    // public void run() {
    // progressBar.incrementProgressBy(1);
    // }
    // };
    //
    // final Runnable mInitializeProgress = new Runnable() {
    // public void run() {
    // progressBar.setMax(100);
    // }
    // };
    //
    // final Runnable mSetInderminate = new Runnable() {
    // public void run() {
    // progressBar.setMessage("saving");
    // progressBar.setIndeterminate(true);
    // }
    // };
}
