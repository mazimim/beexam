package com.dekikurnia.beesmart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by server02 on 09/05/2017.
 */

public class InputAddress extends Activity {
    private EditText inputAddress;
    private TextInputLayout inputLayoutAddress;
    private Button btnLanjut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input);

        inputLayoutAddress = (TextInputLayout) findViewById(R.id.input_layout_address);
        inputAddress = (EditText) findViewById(R.id.input_address);
        inputAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    inputAddress.setSelection(inputAddress.getText().length());
                }
            }
        });
        btnLanjut = (Button) findViewById(R.id.btn_lanjut);
        inputAddress.addTextChangedListener(new MyTextWatcher(inputAddress));
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        if (!isNetworkAvailable()) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Peringatan")
                    .setMessage("Tidak ada koneksi")
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }

                    })
                    .show();
        }

        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        inputAddress.setText(prefs.getString("autoSave", ""));

        inputAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                prefs.edit().putString("autoSave", s.toString()).commit();
            }
        });

        inputAddress.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            submitForm();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });


    }

    private void submitForm() {
        if (!validateAddress()) {
            return;
        }

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("url", inputAddress.getText().toString());
        startActivity(intent);
        finish();
    }

    private boolean validateAddress() {
        if (inputAddress.getText().toString().trim().isEmpty()) {
            inputLayoutAddress.setError(getString(R.string.err_msg_name));
            requestFocus(inputAddress);
            return false;
        } else {
            inputLayoutAddress.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_address:
                    validateAddress();
                    break;
            }
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                InputAddress.this);
        alertDialogBuilder.setTitle("Keluar");
        alertDialogBuilder
                .setMessage("Yakin keluar dari aplikasi ?")
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        System.exit(0);
                    }
                })
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
