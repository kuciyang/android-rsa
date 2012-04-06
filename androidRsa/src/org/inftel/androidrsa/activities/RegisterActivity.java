
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
    private static final int DIALOG_KEY_NOT_FOUND = 1003;

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
            case DIALOG_KEY_NOT_FOUND:
                builder.setMessage(R.string.key_not_found)
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
        // TODO comprobar tamaño de la foto
        // TODO comprobar que el certificado esta firmado x nuestra CA
        String mKeyPath = AndroidRsaConstants.EXTERNAL_SD_PATH
                + File.separator + AndroidRsaConstants.KEY_NAME + mChosenFileString;
        File mKey = new File(mKeyPath);
        if (mKey.exists()) {
            if (mChosenFile != null && mChosenImage != null) {
                String mChosenFilePath = AndroidRsaConstants.EXTERNAL_SD_PATH
                        + File.separator + mChosenFileString;

                // Saving the key's path and cert's path in preferences
                SharedPreferences prefs = getSharedPreferences(
                        AndroidRsaConstants.SHARED_PREFERENCE_FILE,
                        Context.MODE_PRIVATE);
                Editor prefsEditor = prefs.edit();
                prefsEditor.putString(AndroidRsaConstants.CERT_PATH, mChosenFilePath);
                prefsEditor.putString(AndroidRsaConstants.KEY_PATH, mKeyPath);

                // Applying steganography
                Intent i = new Intent(getApplicationContext(), EncodeActivity.class);
                i.putExtra(AndroidRsaConstants.FILE_PATH, mChosenFilePath);
                i.putExtra(AndroidRsaConstants.IMAGE_PATH, mChosenImagePath);
                startActivity(i);

            } else {
                showDialog(DIALOG_NOT_CHOSEN);
            }
        } else {
            showDialog(DIALOG_KEY_NOT_FOUND);
        }

    }
}
