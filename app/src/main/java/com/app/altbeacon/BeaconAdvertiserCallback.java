package com.app.altbeacon;


interface BeaconAdvertiserCallback {
    void onAdvertisingStarted(String error);
    void onAdvertisingStopped(String error);
    void debugData(String data);
}
