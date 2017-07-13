package com.app.altbeacon;

import android.bluetooth.BluetoothManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;


public class BeaconScannerView  extends Fragment implements BeaconScannerCallback {

    private final String TAG = "BeaconScannerView";

    private ArrayList<AltBeacon> listItems = null;
    private MainListAdapter adapter = null;
    private ListView listView = null;

    private BluetoothManager mBluetoothManager = null;
    private BeaconScanner mBeaconScanner = null;
    public static String editTextByteNumberString =null;
    public static String editTextFiltervalueString =null;
    Long start_time;
    Long end_time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothManager = (BluetoothManager) getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().BLUETOOTH_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.scanner_tab, container, false);

        final EditText editTextByteNumber = (EditText) v.findViewById(R.id.editTextByteNumber);
        final EditText editTextFiltervalue = (EditText) v.findViewById(R.id.editTextFilterValue);

        final Button toggleButton = (Button) v.findViewById(R.id.scannerToggle);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBeaconScanner == null) {
                    debugData("Starting Scanner");
                    toggleButton.setText("Scanning");
                    editTextByteNumberString = editTextByteNumber.getText().toString();
                    editTextFiltervalueString = editTextFiltervalue.getText().toString();
                    StartScanner();
                    start_time = System.currentTimeMillis();
                } else {
                    debugData("Stopping Scanner");
                    StopScanner();
                    toggleButton.setText("SCAN");
                }
            }
        });

        listView = (ListView) v.findViewById(R.id.list);
        listItems = new ArrayList<AltBeacon>();
        adapter = new MainListAdapter(getActivity(),listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item value
                AltBeacon itemValue = (AltBeacon) listView.getItemAtPosition(position);

                String showText =  "LUNERA BEACON FORMAT"
                        + System.getProperty("line.separator")+"Raw Data: "+itemValue.getmAllRawData()
                        + System.getProperty("line.separator")+"ID (Byte 1-15)  :" +itemValue.getId1()
                        +  System.getProperty("line.separator") +"ID (Byte 17) : " + itemValue.getId2()
                        + System.getProperty("line.separator")+ "ID (Byte 18-20) : " + itemValue.getId3();
                Toast.makeText(getActivity().getApplicationContext(),showText, Toast.LENGTH_LONG).show();
            }

        });

        return v;
    }

    private void StartScanner() {
        StopScanner();
        BeaconScanner tmpBeaconScanner = new BeaconScanner(getActivity().getApplicationContext(), this, this.mBluetoothManager);
        tmpBeaconScanner.Start();
        mBeaconScanner = tmpBeaconScanner;
    }

    private void StopScanner() {
        BeaconScanner tmpScanner = mBeaconScanner;
        mBeaconScanner = null;
        if(tmpScanner != null){
            tmpScanner.Stop();
        }

        listItems.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void BeaconDiscovered(AltBeacon beacon) {

        if(beacon == null && beacon.getDevice() != null){
            return;
        }


        /*
        if(!beacon.getBeaconCode().equalsIgnoreCase(MainActivity.BEACON_CODE)){

            return;
        }


        debugData("Beacon Discovered : " + beacon.getDevice().getAddress());

        debugData("getBeaconCode:" + beacon.getBeaconCode());
           */
        end_time = System.currentTimeMillis();
        for(int i = 0; i < listItems.size();i++){
            if(listItems.get(i).getId1().equalsIgnoreCase(beacon.getId1())){
                listItems.get(i).setBeepcount(listItems.get(i).beepcount + 1);
                 float elapsed = (end_time - start_time) / 1000;

                    listItems.get(i).setFrequency(listItems.get(i).beepcount / elapsed);
               /* debugData("freq:" + listItems.get(i).frequency + (listItems.get(i).beepcount/elapsed));
                debugData(String.valueOf(elapsed));
                debugData("count:" + listItems.get(i).beepcount);

                listItems.remove(i);
                listItems.add(i, beacon);*/
                    adapter.notifyDataSetChanged();





                return;
            }
        }

        listItems.add(beacon);
        adapter.notifyDataSetChanged();
        debugData("listItems count:" + listItems.size());
    }

    @Override
    public void debugData(String data) {
        Log.i(TAG, data);
    }
}
