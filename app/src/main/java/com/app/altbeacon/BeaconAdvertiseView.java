package com.app.altbeacon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Arrays;

public class BeaconAdvertiseView extends Fragment implements BeaconAdvertiserCallback {

    private final String TAG = "BeaconAdvertiseView";

    private BluetoothManager mBluetoothManager = null;

    private BeaconTransmitter mBeaconTransmitter;


    private boolean weAreStoppingNow = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothManager = (BluetoothManager) getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().BLUETOOTH_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.advertise_tab, container, false);

        final EditText adLengthText = (EditText) v.findViewById(R.id.adlengthbox);
        final EditText adTypeText = (EditText) v.findViewById(R.id.adtypebox);
        final EditText MFGIDText = (EditText) v.findViewById(R.id.mfgidbox);
        final EditText beaconCodeText = (EditText) v.findViewById(R.id.beaconcodebox);
        final EditText beaconID1Text = (EditText) v.findViewById(R.id.beaconid1box);
        final EditText beaconID2Text = (EditText) v.findViewById(R.id.beaconid2box);
        final EditText beaconID3Text = (EditText) v.findViewById(R.id.beaconid3box);
        final EditText ReferenceRSSIText = (EditText) v.findViewById(R.id.referencerssibox);
        final EditText MGFReservedText = (EditText) v.findViewById(R.id.mfgreservedbox);



        final Button toggleButton = (Button) v.findViewById(R.id.advertToggle);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    debugData(" Starting Advertising");

                    StartAdvertiser(adLengthText.getText().toString(),
                            adTypeText.getText().toString(),
                            MFGIDText.getText().toString(),
                            beaconCodeText.getText().toString(),
                            beaconID1Text.getText().toString(),beaconID2Text.getText().toString(),beaconID3Text.getText().toString(),
                            ReferenceRSSIText.getText().toString(),
                            MGFReservedText.getText().toString()
                           );


            }
        });

        return v;
    }

    private void StartAdvertiser(String adLength,String adType,String MFGID,
                                 String beaconCode,String beaconID1, String beaconID2,
                                 String beaconID3, String ReferenceRSSI,String MGFReserved)
    {

        //API is not available before Lollipop
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


           Start(adLength, adType, MFGID,beaconCode, beaconID1,beaconID2,beaconID3, ReferenceRSSI, MGFReserved);


        }else{
            onAdvertisingStarted("Advertisement Not supported by platform version : " + Build.VERSION.SDK_INT);
        }
    }


    @Override
    public void onAdvertisingStarted(String error) {

        if(error != null) {
            Toast.makeText(getActivity().getApplicationContext(), "Can not start advertising : " + error, Toast.LENGTH_LONG).show();
        }
        debugData("onAdvertisingStarted : " + error);
    }

    @Override
    public void onAdvertisingStopped(String error) {
        debugData("onAdvertisingStopped : " + error);
    }

    @Override
    public void debugData(String data) {
        Log.i(TAG,data);
    }



    public void Start(String adLength,String adType,String MFGID,
                         String beaconCode,String beaconID1,String beaconID2,String beaconID3,String ReferenceRSSI,String MGFReserved){

/// L-BEACON Fields
        /// Byte(s)     Name
        /// --------------------------
        /// 0 - 4       preset: 02 01 06 1b ff
        /// 5-6         Manufacturer ID (16-bit unsigned integer, big endian)
        /// 7-8         Beacon code (two 8-bit unsigned integers, but can be considered as one 16-bit unsigned integer in little endian)
        /// 9-24        ID1 (UUID)
        /// 25       ID2 (8-bit unsigned integer, big endian)
        /// 26-28       ID3 (32-bit unsigned integer, big endian)
        /// 29          reference rssi
        /// 30          Reserved for use by the manufacturer to implement special features (optional)
        ///
       // long presetbyte0to2 = 0x020106;
        Beacon beacon = new Beacon.Builder()
                .setManufacturer(Integer.decode(MFGID))

                .setId1(beaconID1)
                .setId2(beaconID2)
                .setId3(beaconID3)
                .setRssi(Integer.decode(ReferenceRSSI))


                .setTxPower(-59)
               // .setDataFields(Arrays.asList(presetbyte0to2))
                .build();
        BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-20,i:21-23,p:24-24");
        BeaconTransmitter beaconTransmitter = new BeaconTransmitter(getActivity().getApplicationContext(), beaconParser);

        if(!beaconTransmitter.isStarted())
        {beaconTransmitter.startAdvertising(beacon);
        }

        for(int i =1;i<=20;i++) {




            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "beacon is " +beaconTransmitter.isStarted());

        }


            beaconTransmitter.stopAdvertising();



    }

}


