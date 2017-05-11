package br.usp.ime.mac5743.ep1.seminarioime.activity;



import br.usp.ime.mac5743.ep1.seminarioime.R;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Arrays;

import br.usp.ime.mac5743.ep1.seminarioime.util.Preferences;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ReadQRCodeActivity extends Activity implements ZXingScannerView.ResultHandler {
    public static final String SEMINAR_ID = "seminarId";

    private ZXingScannerView mScannerView;
    private static final String TAG = "ReadQRCodeActivity";
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private boolean qrCodeLigado = false;
    private static final boolean reading = true;

    private SharedPreferences sharedPref;


    private String nusp;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        nusp = sharedPref.getString(Preferences.NUSP.name(), null);


        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        mScannerView.setKeepScreenOn( true );
        mScannerView.setAutoFocus( true );
        setContentView(R.layout.activity_read_qrcode );                // Set the scanner view as the content view
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
    public void onResume() {
        super.onResume();
        if ( qrCodeLigado ) {
            turnOnQRCode();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if ( qrCodeLigado ) {
            mScannerView.stopCamera();           // Stop camera on pause
        }
    }

    private void startQRCode() {
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }
    public void turnOnQRCode() {
        qrCodeLigado = true;
        startQRCode();
    }

    public void saveResult( String seminarId ) {
        Intent intent = new Intent(this, ConfirmQRCodeActivity.class);

        // TODO: chamar o serviço
        String apiMessage = null;
        intent.putExtra( ConfirmQRCodeActivity.OK_FIELD,apiMessage == null );
        intent.putExtra( ConfirmQRCodeActivity.MESSAGE_FIELD,apiMessage);
        intent.putExtra( ConfirmQRCodeActivity.SEMINAR_ID, seminarId );
        startActivity( intent );
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
        saveResult( rawResult.getText() );
    }

    private void executePermissionGranted() {
        this.setContentView( mScannerView );
        turnOnQRCode();
    }

    public void executePermissionDenied() {
        Intent intent = new Intent(this, ConfirmQRCodeActivity.class);
        String permissionDeniedMessage = getString(R.string.qrcode_permission_denied );

        // TODO: chamar o serviço
        String apiMessage = null;
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

                    Log.d( "Testes", "Concedeu!!!!"  );
                    executePermissionGranted();

                } else {

                    Log.d( "Testes", "Concedeu nada!!!!"  );
                    executePermissionDenied();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
