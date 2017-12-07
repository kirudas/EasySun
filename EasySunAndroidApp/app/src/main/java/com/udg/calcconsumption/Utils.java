package com.udg.calcconsumption;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;

/**
 * Created by domy9 on 02/12/2017.
 */

public class Utils {

    public static final String NAME_PREFERENCIES = "UdGHackDayPreferences";
    public static final String PREFS_TRUST_ALL_CERTIFICATE_ACCEPTED = "TrustAllCertificates";
    public static final String PVGIS_FOLDER_NAME = "PVGIS";
    public static final String PVGIS_PATH = "PVGIS_PATH";

    public static String getRutaBasePublica(Context context){
        return context.getExternalFilesDir(null)+ File.separator;
    }

    public static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    /**
     * Retorna cert si tots els permissos estan acceptats. Fals si un d'ells no ho està:
     */
    public static boolean checkPermissionsState(Context context){
        boolean error = false;
        String[] permissions_array = obtenirPermisosAplicacio();
        int i = 0;
        while(!error && i < permissions_array.length){
            int permissionCheck = ContextCompat.checkSelfPermission(context, permissions_array[i]);
            if(permissionCheck == PackageManager.PERMISSION_DENIED){
                error = true;
            }
            else{
                i++;
            }
        }
        return !error;
    }

    public static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    public static boolean comprovar_conexio_internet(Context context) {
        // Afegit per bloquejar que no es pugui utilitzar l'app online amb 2G:
        if (!Utils.isWifiConnection(context) && getNetworkClass(context).equals("2G")){ return false; }
        // Comprovem si hi ha connexió:
        final ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService (Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork =conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) { return true; }
        else { return false; }
    }

    public static boolean isWifiConnection(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) { return true; }
        else{ return false; }
    }

    /**
     *
     * @param context
     * @return
     * <b>"2G"</b> <br> NETWORK_TYPE_GPRS, NETWORK_TYPE_EDGE, NETWORK_TYPE_CDMA, NETWORK_TYPE_1xRTT, NETWORK_TYPE_IDEN <br>
     * <b>"3G"</b> <br> NETWORK_TYPE_UMTS, NETWORK_TYPE_EVDO_0, NETWORK_TYPE_EVDO_A, NETWORK_TYPE_HSDPA, NETWORK_TYPE_HSUPA, NETWORK_TYPE_HSPA, NETWORK_TYPE_EVDO_B, NETWORK_TYPE_EHRPD, NETWORK_TYPE_HSPAP <br>
     * <b>"4G"</b> <br> NETWORK_TYPE_LTE <br>
     * <b>"Unknown"</b> <br> AS DEFAULT
     *
     */
    public static String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "Unknown";
        }
    }

    public static String[] obtenirPermisosAplicacio(){
        String[] permissions_array =
                {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                };
        return permissions_array;
    }

}
