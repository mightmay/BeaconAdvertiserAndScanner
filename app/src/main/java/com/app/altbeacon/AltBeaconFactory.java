package com.app.altbeacon;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

// We will need this to scan, since the AltBeacon library does not support ad hoc scan, it only support being trigger by beacon when in range.

public class AltBeaconFactory {

    public static boolean isBLESupported(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public static double calculateDistanceFromRssi(double rawSignalStrengthInDBm, int measuredPower)
    {
        double distance = 0d;
        double near = rawSignalStrengthInDBm / measuredPower;

        if (near < 1.0f)
        {
            distance = Math.pow(near, 10);
        }
        else
        {
            distance = ((0.89976f) * Math.pow(near, 7.7095f) + 0.111f);
        }

        return distance;
    }

    static public boolean scanFilter(byte[] scanRecord){

        Log.d( "AltBeaconFactory","filter : " + BeaconScannerView.editTextFiltervalueString
        + " " +BeaconScannerView.editTextByteNumberString);
       if(scanRecord == null || scanRecord.length  < 20){
            return false;
        }
        if(BeaconScannerView.editTextByteNumberString != null && !(BeaconScannerView.editTextByteNumberString.isEmpty())) {
            int bytetofilter = Integer.parseInt(BeaconScannerView.editTextByteNumberString);
            //String scanRecordinstring = Byte.toString(scanRecord[bytetofilter]);
            int intFilter = Integer.decode(BeaconScannerView.editTextFiltervalueString);
            Log.d( "AltBeaconFactory","filter2 : "
                    + " " +bytetofilter + " " + scanRecord[bytetofilter] +" "+ intFilter);

            if ( ! (intFilter == scanRecord[bytetofilter]) ) {
                return false;
            }
        }
        return true;
    }

    static public String getManufacturer(byte[] scanRecord){
        return getStringPart(3, 3, scanRecord) + getStringPart(2, 2, scanRecord); //little endian
    }

    static public String getBeaconCode(byte[] scanRecord){
        return getStringPart(4, 5, scanRecord);
    }

    /// The expected specification of the data is as follows:
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


    public static AltBeacon getBeaconFromScanrecord(BluetoothDevice device,byte[] scanRecord,final int rssi) {

        String id1 = getStringPart(9, 24, scanRecord);
        int id2 = (((scanRecord[25] & 0xFF) << 8)); // Uint16
        int id3 = ((scanRecord[28] & 0xFF) + ((scanRecord[27] & 0xFF)+  (scanRecord[26] & 0xFF) << 8)); // Uint16

        String allRawData = getStringPart(0,scanRecord.length -1 ,scanRecord);
        long Distance = Math.round(AltBeaconFactory.calculateDistanceFromRssi(rssi, scanRecord[26]));
        return new AltBeacon(allRawData,device,scanRecord[1],getManufacturer(scanRecord),getBeaconCode(scanRecord),id1,id2,id3,scanRecord[29],scanRecord[30],Distance);
    }


    static private String getStringPart(int start, int end, byte[] record){

        end = end + 1;
        if(start < 0 || end < start){
            return null;
        }

        if(record.length < end){
            return null;
        }

        StringBuilder hex = new StringBuilder((end - start) * 2);

        for(int i = start; i < end ; i++){
            hex.append(String.format("%02X", record[i]));
        }

        return hex.toString();
    }
}
