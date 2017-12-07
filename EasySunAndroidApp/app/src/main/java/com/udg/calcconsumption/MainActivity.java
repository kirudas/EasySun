package com.udg.calcconsumption;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSIONS=255;

    Button btnDescarrega2;
    EditText etPotInst, etInclinacio, etAzimuth;

    public static Double[] politecnica = new Double[] {2.07, 3.02, 4.42, 5.16, 6.20, 7.05, 6.96, 6.07, 4.76, 3.44, 2.26, 1.81};

    //////////////////////////////////
    //	activity methods            //
    //////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDescarrega2 = (Button) findViewById(R.id.btnDownload2);
        etPotInst = (EditText) findViewById(R.id.etPotInst);
        etInclinacio = (EditText) findViewById(R.id.etInclinacio);
        etAzimuth = (EditText) findViewById(R.id.etAzimuth);

        etPotInst.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    Double.parseDouble(String.valueOf(charSequence));
                }catch (Exception ex) {
                    etPotInst.setError("No és un valor numéric");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etInclinacio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    Double.parseDouble(String.valueOf(charSequence));
                }catch (Exception ex) {
                    etInclinacio.setError("No és un valor numéric");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etAzimuth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    Double.parseDouble(String.valueOf(charSequence));
                }catch (Exception ex) {
                    etAzimuth.setError("No és un valor numéric");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnDescarrega2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etPotInst.getText() != null && etInclinacio.getText() != null && etAzimuth.getText() != null) {

                    try {
                        double potInst = Double.parseDouble(String.valueOf(etPotInst.getText()));
                        double inclinacio = Double.parseDouble(String.valueOf(etInclinacio.getText()));
                        double azimuth = Double.parseDouble(String.valueOf(etAzimuth.getText()));

                        Bundle bundle = new Bundle();
                        bundle.putDouble(Constants.POTENCIA_INSTALACIO, potInst);
                        bundle.putDouble(Constants.INCLINACIO, inclinacio);
                        bundle.putDouble(Constants.AZIMUTH, azimuth);

                        Intent intent = new Intent(MainActivity.this, Grafic.class);
                        intent.putExtras(bundle);

                        startActivity(intent);

                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }
        });

        checkPermissions();

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.sortint));
        builder.setMessage(getResources().getString(R.string.segurSortir));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.sortir), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancela), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        if (dialog != null)
            dialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length == 0){ return; }
        boolean trobat=false;
        int i=0;
        // Comprovem permisos acceptats:
        switch (requestCode) {
            case PERMISSIONS:
                while(!trobat&&i<permissions.length){
                    if(grantResults[i]!= PackageManager.PERMISSION_GRANTED){
                        trobat=true;
                    }
                    else i++;
                }
                break;
        }

        // Comprovem el resultat:
        if(trobat){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.ControlDePermisos));
            builder.setMessage(getString(R.string.ParaUsarEscioSonNecesariosTodosLosPermisos));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.accept_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }

    //////////////////////////////////
    //	this methods                //
    //////////////////////////////////

    private boolean checkPermissions(){
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!Utils.checkPermissionsState(this)){
                ActivityCompat.requestPermissions(this, Utils.obtenirPermisosAplicacio(), PERMISSIONS);
                return true;
            }
        }
        return false;
    }

}
