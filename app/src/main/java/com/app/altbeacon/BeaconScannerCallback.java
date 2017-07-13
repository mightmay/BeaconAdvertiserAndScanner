package com.app.altbeacon;



interface BeaconScannerCallback {
    void BeaconDiscovered(AltBeacon beacon);
    void debugData(String data);
}

