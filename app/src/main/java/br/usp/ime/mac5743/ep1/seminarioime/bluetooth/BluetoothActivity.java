package br.usp.ime.mac5743.ep1.seminarioime.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

import br.usp.ime.mac5743.ep1.seminarioime.R;

/**
 * Created by aderleifilho on 30/04/17.
 */

public class BluetoothActivity extends AppCompatActivity {

    /*  Um adaptador para conter os elementos da lista de dispositivos descobertos.
      */
    ArrayAdapter<String> pairedAdapter;
    ArrayAdapter<String> nearAdapter;
    ListView lvPaired;
    ListView lvNear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);

        /*  Esse trecho não é essencial, mas dá um melhor visual à lista.
            Adiciona um título à lista de dispositivos pareados utilizando
        o layout text_header.xml.
        */
        lvPaired = (ListView) findViewById(R.id.bluetooth_paired_list);
        lvNear = (ListView) findViewById(R.id.bluetooth_near_list);


        //LayoutInflater inflater = getLayoutInflater();
        //View header = inflater.inflate(R.layout.bluetooth_activity, lv, false);
        //((TextView) header.findViewById(R.id.paired_devices)).setText("\nDispositivos pareados\n");
        //lv.addHeaderView(header, null, false);

        /*  Usa o adaptador Bluetooth para obter uma lista de dispositivos pareados.
         */
        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        /*  Cria um modelo para a lista e o adiciona à tela.
            Se houver dispositivos pareados, adiciona cada um à lista.
         */
        pairedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        nearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lvNear.setAdapter(nearAdapter);
        lvPaired.setAdapter(pairedAdapter);
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }

        btAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        lvNear.setClickable(true);
        lvNear.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String result = (String) lvNear.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                String devAddress = result.substring(result.indexOf("\n") + 1, result.length());
                String devName = btAdapter.getRemoteDevice(devAddress).getName();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("btDevName", devName);
                returnIntent.putExtra("btDevAddress", devAddress);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        lvPaired.setClickable(true);
        lvPaired.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String result = (String) lvPaired.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

                String devAddress = result.substring(result.indexOf("\n") + 1, result.length());
                String devName = btAdapter.getRemoteDevice(devAddress).getName();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("btDevName", devName);
                returnIntent.putExtra("btDevAddress", devAddress);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                nearAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    /*  Executado quando a Activity é finalizada.
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();

        /*  Remove o filtro de descoberta de dispositivos do registro.
         */
        unregisterReceiver(receiver);
    }
}
