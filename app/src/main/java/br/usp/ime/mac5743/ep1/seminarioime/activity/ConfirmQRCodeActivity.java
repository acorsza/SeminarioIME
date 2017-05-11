package br.usp.ime.mac5743.ep1.seminarioime.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import br.usp.ime.mac5743.ep1.seminarioime.R;

public class ConfirmQRCodeActivity extends AppCompatActivity {

    public static final String OK_FIELD = "ok";
    public static final String MESSAGE_FIELD = "message";
    public static final String SEMINAR_ID = "seminarId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_qrcode);
        Bundle b = getIntent().getExtras();

        String ok = b.getString( OK_FIELD );
        String msg = b.getString( MESSAGE_FIELD );
        String seminarId = b.getString( SEMINAR_ID );

        TextView tv = (TextView) findViewById(R.id.textQrCodeConfirmed );

        if ( b.getBoolean( OK_FIELD ) ) {
            String successMessage = getString(R.string.ok_qrcode_message );
            tv.setText( String.format( successMessage, seminarId ) );
        } else {
            tv.setText( msg );
        }
    }
}
