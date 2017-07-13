package com.app.altbeacon;

import android.bluetooth.BluetoothDevice;

public class AltBeacon {
    private final BluetoothDevice mDevice;
    private final int mType;
    private final String mManufacturer;
    private final String mBeaconCode;
    private final String mId1;
    private final int mId2;
    private final int mId3;
    private final int mRefRSSI;
    private final int mManufacturerReserved;
    private final long mDistance;
    private final String mAllRawData;
    public int beepcount;
    public float frequency;

    public AltBeacon(String allRawData , BluetoothDevice device, int type, String manufacturer, String beaconCode, String id1, int id2, int id3, int refRSSI, int reserved, long distance) {
        this.frequency=0;
        this.beepcount=0;
        this.mAllRawData = allRawData;
        this.mDevice = device;
        this.mType = type;
        this.mManufacturer = manufacturer;
        this.mBeaconCode = beaconCode;
        this.mId1 = id1;
        this.mId2 = id2;
        this.mId3 = id3;
        this.mRefRSSI = refRSSI;
        this.mManufacturerReserved = reserved;
        this.mDistance = distance;
    }


    public String getmAllRawData(){return mAllRawData;}
    public BluetoothDevice getDevice(){return mDevice;}
    public int getType(){return mType;}
    public String getManufacturer(){return mManufacturer;}
    public String getBeaconCode(){return mBeaconCode;}
    public String getId1(){return mId1;}
    public int getId2(){return mId2;}
    public int getId3(){return mId3;}
    public int getRefRSSI(){return mRefRSSI;}
    public int getManufacturerReserved(){return mManufacturerReserved;}
    public long getDistance(){return mDistance;}
    public void setBeepcount(int bc){this.beepcount= bc;}
    public void setFrequency(float freq){this.frequency= freq;}


}
