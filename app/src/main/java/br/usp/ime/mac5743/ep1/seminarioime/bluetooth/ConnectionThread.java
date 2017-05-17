package br.usp.ime.mac5743.ep1.seminarioime.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

import br.usp.ime.mac5743.ep1.seminarioime.activity.SeminarDetailsActivity;

public class ConnectionThread extends Thread{

    private BluetoothSocket btSocket = null;
    private BluetoothServerSocket btServerSocket = null;
    private InputStream input = null;
    private OutputStream output = null;
    private String btDevAddress = null;
    private final String myUUID = "00001101-0000-1000-8000-00805F9B34FB";
    boolean server;
    boolean running = false;
    private Handler handler;
    public static final int START_ACTION = 1;
    public static final int FINISH_ACTION = 2;
    public static final String ACTION_FIELD = "action";

    public static final String INIT_CONNECTION = "---S";

    public ConnectionThread() {
        this.server = true;
    }

    public ConnectionThread(String btDevAddress) {
        this.server = false;
        this.btDevAddress = btDevAddress;
    }

    public void setHandler( Handler handler ) {
        this.handler = handler;
    }

    public void run() {

        this.running = true;
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(this.server) {

            try {

                btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord("Super Bluetooth", UUID.fromString(myUUID));
                btSocket = btServerSocket.accept();

                if(btSocket != null) {

                    btServerSocket.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }

        } else {

            try {


                BluetoothDevice btDevice = btAdapter.getRemoteDevice(btDevAddress);
                btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(myUUID));

                btAdapter.cancelDiscovery();


                if (btSocket != null)
                    btSocket.connect();

            } catch (IOException e) {
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }

        }

        if(btSocket != null) {

            toMainActivity("---S".getBytes());

            try {

                input = btSocket.getInputStream();
                output = btSocket.getOutputStream();

                byte[] buffer = new byte[1024];
                int bytes;

                while(running) {

                    bytes = input.read(buffer);
                    toMainActivity(Arrays.copyOfRange(buffer, 0, bytes));

                }

            } catch (Exception e) {
                e.printStackTrace();
                toMainActivity("---N".getBytes());
            }
        }
        if ( running ) {
            cancel();
        }
        finished();

    }

    private void toMainActivity(byte[] data) {

        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt( ACTION_FIELD, START_ACTION );
        bundle.putByteArray("data", data);
        message.setData(bundle);
        handler.sendMessage(message);
    }


    public void write(byte[] data) {

        if(output != null) {
            try {
                output.write(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            toMainActivity("---N".getBytes());
        }
    }

    private void finished() {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt( ACTION_FIELD, FINISH_ACTION );
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public void cancel() {

        try {

            running = false;

            if(btServerSocket != null) {
                btServerSocket.close();
            }
            if(btSocket != null) {
                btSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
