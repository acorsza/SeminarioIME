package br.usp.ime.mac5743.ep1.seminarioime.activity;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.zxing.Result;

import br.usp.ime.mac5743.ep1.seminarioime.R;
import br.usp.ime.mac5743.ep1.seminarioime.api.RestAPIUtil;
import br.usp.ime.mac5743.ep1.seminarioime.util.Preferences;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ReadQRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private boolean qrCodeLigado = false;
    private boolean ligarQRCode = false;
    public static final String SEMINAR_ID = "seminarId";

    private SharedPreferences sharedPref;
    private String seminarId;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Bundle b = getIntent().getExtras();
        this.seminarId = b.getString("seminarId");

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        mScannerView.setKeepScreenOn( true );
        mScannerView.setAutoFocus( true );
        setContentView(R.layout.activity_read_qrcode );                // Set the scanner view as the content view

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if ( toolbar!= null ) {
            setSupportActionBar(toolbar);
            if ( getSupportActionBar()!= null ) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            executePermissionGranted();

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if ( ligarQRCode && !qrCodeLigado ) {
            startQRCode();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if ( qrCodeLigado ) {
            stopQRCode();
        }
    }

    private synchronized void startQRCode() {
        if ( !qrCodeLigado ) {
            qrCodeLigado = true;
            mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
            mScannerView.startCamera();          // Start camera on resume
        }
    }

    private synchronized void stopQRCode() {
        if (qrCodeLigado) {
            mScannerView.stopCamera();           // Stop camera on pause
            qrCodeLigado = false;
        }
    }

    private void turnOnQRCode() {
        ligarQRCode = true;
        startQRCode();
    }

    private void saveResult( String seminarId ) {
        Intent intent = new Intent(this, ConfirmQRCodeActivity.class);

        boolean ok = false;
        if ( this.seminarId.equals( seminarId ) ) {
            ok = RestAPIUtil.confirmAttendance(sharedPref.getString(Preferences.NUSP.name(), null), seminarId);
            if ( !ok ) {
                String errorMessage = getString(R.string.error_qrcode_message );
                intent.putExtra( ConfirmQRCodeActivity.MESSAGE_FIELD, errorMessage );
            }
        } else {
            String errorMessage = getString(R.string.incorrect_qrcode_message );
            intent.putExtra( ConfirmQRCodeActivity.MESSAGE_FIELD, errorMessage );
        }
        intent.putExtra( ConfirmQRCodeActivity.OK_FIELD, ok );
        intent.putExtra( ConfirmQRCodeActivity.SEMINAR_ID, seminarId );

        startActivity( intent );
    }

    @Override
    public void handleResult(Result rawResult) {
        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
        saveResult( rawResult.getText() );
    }

    private void executePermissionGranted() {
        this.setContentView( mScannerView );
        turnOnQRCode();
    }

    private void executePermissionDenied() {
        Intent intent = new Intent(this, ConfirmQRCodeActivity.class);
        String permissionDeniedMessage = getString(R.string.qrcode_permission_denied );

        intent.putExtra( ConfirmQRCodeActivity.OK_FIELD,Boolean.FALSE );
        intent.putExtra( ConfirmQRCodeActivity.MESSAGE_FIELD, permissionDeniedMessage );
        intent.putExtra( ConfirmQRCodeActivity.SEMINAR_ID, (String) null );
        startActivity( intent );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    executePermissionGranted();

                } else {
                    executePermissionDenied();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
