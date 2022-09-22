package com.plugin.ibeacon;

import android.util.Log;
import android.content.Intent;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.BeaconParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.apache.cordova.PluginResult;

/**
 * This class echoes a string called from JavaScript.
 */
public class IBeacon extends CordovaPlugin {

  private String TAG = "Beacon";
  private BeaconManager beaconManager = null;
  public static final Region wildcardRegion = new Region("wildcardRegion", null, null, null);

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("coolMethod")) {
      String message = args.getString(0);
      this.coolMethod(message, callbackContext);
      return true;
    }
    if (action.equals("startListening")) {
      this.startListening(callbackContext);
      return true;
    }
    if (action.equals("stopListening")) {
      this.stopListening(callbackContext);
      return true;
    }
    return false;
  }

  private void coolMethod(String message, CallbackContext callbackContext) {
    if (message != null && message.length() > 0) {
      callbackContext.success(message);
    } else {
      callbackContext.error("Expected one non-empty string argument.");
    }
  }

  private void startListening(CallbackContext callbackContext) {
    if (null != beaconManager) {
      return;
    }
    beaconManager = BeaconManager.getInstanceForApplication(cordova.getContext());
    beaconManager.getBeaconParsers().clear();
    beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

    RangeNotifier rangeNotifier = new RangeNotifier() {
      @Override
      public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        List<Map<String, Object>> results = new ArrayList<>();
        beacons.forEach(beacon -> {
          Map<String, Object> tempMap = new HashMap<>();
          tempMap.put("UUID", beacon.getId1().toUuid().toString());
          tempMap.put("Mac", beacon.getBluetoothAddress());
          tempMap.put("Name", beacon.getBluetoothName());
          tempMap.put("Major", beacon.getId2().toString());
          tempMap.put("Minor", beacon.getId3().toString());
          tempMap.put("RSSI", beacon.getRssi());
          tempMap.put("Distance", beacon.getDistance());
          results.add(tempMap);
        });
        JSONArray jsonArray = new JSONArray(results);
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonArray.toString());
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
      }
    };
    beaconManager.addRangeNotifier(rangeNotifier);
    beaconManager.startRangingBeacons(wildcardRegion);
  }

  private void stopListening(CallbackContext callbackContext) {
    if (null == beaconManager) {
      callbackContext.success("stoped");
      return;
    }
    beaconManager.stopRangingBeacons(wildcardRegion);
    beaconManager.removeAllRangeNotifiers();
    callbackContext.success("stoped");
  }
}
