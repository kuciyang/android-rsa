
package org.inftel.androidrsa;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class Register extends Activity {

    private static final String TAG = "TAG_REGISTER";
    private String idioma;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);

        // Spinner spinner = (Spinner) findViewById(R.id.texto_idiom_register);
        // ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        // this, R.array.idioms_array, android.R.layout.simple_spinner_item);
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // spinner.setAdapter(adapter);
        //
        // spinner.setOnItemSelectedListener(new
        // AdapterView.OnItemSelectedListener() {
        // public void onItemSelected(AdapterView<?> parent,
        // android.view.View v, int position, long id) {
        // idioma = parent.getItemAtPosition(position).toString();
        // }
        //
        // public void onNothingSelected(AdapterView<?> parent) {
        // }
        // });

    }

    public void onClickButtonRegister(View view) throws IOException {

    }
}
