package com.udg.calcconsumption;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by domy9 on 02/12/2017.
 */

public class DownloadPvGis extends AsyncTask<Void, Void, Boolean> {

    Activity activity;
    ProgressDialog dialog;
    SharedPreferences preferencies;
    Bundle bundle;
    boolean errorSSL= false;
    String pathPvGisFile = "";

    public DownloadPvGis(Activity activity, Bundle bundle) {
        this.activity = activity;
        this.bundle = bundle;

        preferencies = activity.getSharedPreferences(Utils.NAME_PREFERENCIES, Context.MODE_PRIVATE);

    }

    @Override
    protected void onPreExecute() {

        dialog = new ProgressDialog(activity);
        dialog.setTitle(activity.getResources().getString(R.string.downLang_data));
        dialog.setMessage(activity.getResources().getString(R.string.downLang_siusplau_acabi));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        if (!Utils.comprovar_conexio_internet(activity)){
            this.cancel(true);
        }

        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        File langDirectory = comprovarDirectoris();
        if (langDirectory == null){ this.cancel(true); }

        return downloadFile(langDirectory, bundle);

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (dialog != null)
            if (dialog.isShowing())
                dialog.dismiss();

        if (aBoolean) {
            //Mostrar descarregat correctament
            SharedPreferences.Editor editor = preferencies.edit();
            editor.putString(Utils.PVGIS_PATH, pathPvGisFile);
            editor.commit();

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setTitle(activity.getResources().getString(R.string.descarregaFinalitzada));
            builder.setMessage(activity.getResources().getString(R.string.descarregaFinalitzadaCorrectament));
            builder.setPositiveButton(activity.getResources().getString(R.string.accept_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        PvgisParser pParser = new PvgisParser(pathPvGisFile);
                        pParser.getDades();
                    } catch (PvgisParserException e) {
                        e.printStackTrace();
                    }

                }
            });
            AlertDialog dialog = builder.create();
            if (dialog != null)
                dialog.show();

        }
        else {
            //Mostrar hi ha un error

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setTitle(activity.getResources().getString(R.string.problemaAlDescarregar));
            builder.setMessage(activity.getResources().getString(R.string.algunProblemaAlDescarregarElFitxer));
            builder.setPositiveButton(activity.getResources().getString(R.string.accept_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog dialog = builder.create();
            if (dialog != null)
                dialog.show();
        }

    }

    @Override
    protected void onCancelled(Boolean aBoolean) {

        if(errorSSL){

            try{

                TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                // TODO Auto-generated method stub
                                return null;
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType)
                                    throws CertificateException {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType)
                                    throws CertificateException {
                                // TODO Auto-generated method stub

                            }
                        }
                };

                // Install the all-trusting trust manager
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                // Create all-trusting host name verifier
                HostnameVerifier allHostsValid = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        // TODO Auto-generated method stub
                        return true;
                    }
                };

                // Install the all-trusting host verifier
                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            }
            catch (NoSuchAlgorithmException n){ n.printStackTrace(); }
            catch (KeyManagementException e) { e.printStackTrace(); }

            SharedPreferences preferencies = activity.getSharedPreferences(Utils.NAME_PREFERENCIES, Context.MODE_PRIVATE);
            SharedPreferences.Editor prefsEdit = preferencies.edit();
            prefsEdit.putBoolean(Utils.PREFS_TRUST_ALL_CERTIFICATE_ACCEPTED, true);
            prefsEdit.commit();

        }


        super.onCancelled(aBoolean);
    }


    private File comprovarDirectoris(){

        File langDirectory = new File(Utils.getRutaBasePublica(activity) + Utils.PVGIS_FOLDER_NAME);

        if (langDirectory == null || !langDirectory.exists() || !langDirectory.isDirectory())
            if (langDirectory != null)
                langDirectory.mkdirs();

        return langDirectory;

    }

    private boolean downloadFile(File f, Bundle bundle){
        if (Utils.comprovar_conexio_internet(activity)) {
            boolean acabat_be = false;
            try {
                copy(getInputStream(bundle), new File(f, "pvgis.json"));
                acabat_be = true;
            } catch (Exception e) {
                acabat_be = false;
            }
            return acabat_be;
        }
        else{
            return false;
        }
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            IOUtils.copy(in, out);
            in.close();
            out.close();

            pathPvGisFile = file.getAbsolutePath();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private InputStream getInputStream(Bundle bundle) throws IOException
    {
        InputStream inputStream = null;

        try {

            String urlStr = "https://pvgisjson.com/api/v1/pv?";

            ArrayList<NameValuePair> parametresPost = new ArrayList<NameValuePair>();
            String query = "";

            parametresPost.add(new BasicNameValuePair("lat", String.valueOf(bundle.getDouble(Constants.LATITUDE))));
            parametresPost.add(new BasicNameValuePair("lng", String.valueOf(bundle.getDouble(Constants.LONGITUDE))));
            parametresPost.add(new BasicNameValuePair("pvtech", bundle.getString(Constants.PVTECH)));
            parametresPost.add(new BasicNameValuePair("peakpower", String.valueOf(bundle.getDouble(Constants.PEAKPOWER))));
            parametresPost.add(new BasicNameValuePair("losses", String.valueOf(bundle.getDouble(Constants.LOSSES))));
            parametresPost.add(new BasicNameValuePair("mounting", bundle.getString(Constants.MOUNTING)));
            parametresPost.add(new BasicNameValuePair("slope", String.valueOf(bundle.getDouble(Constants.SLOPE))));
            parametresPost.add(new BasicNameValuePair("azimuth", String.valueOf(bundle.getDouble(Constants.AZIMUTH))));

            query = Utils.getQuery(parametresPost);

            urlStr += query;

            Log.e("TEST", urlStr);

            URL Https_url = new URL(urlStr);
            HttpsURLConnection con = (HttpsURLConnection) Https_url.openConnection();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
                con.setRequestProperty("Connection", "close");
            }
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            int status = con.getResponseCode();

            if (status >= 400 ) {
                inputStream = con.getErrorStream();
            }
            else{
                inputStream = con.getInputStream();
            }

            if (inputStream != null) {
                String result = getStringFromInputStream(inputStream);
                Log.e("TEST", result);
//                JSONObject jobject = new JSONObject(result);
//                String minimum_version = jobject.getString("minimum_version");
//
//                if (minimum_version.equals(currentVersion)){ doUpdate = false; }
//                else{
//                    if (Integer.parseInt(minimum_version) > Integer.parseInt(currentVersion)){
//                        doUpdate = true;
//                        versio = new Versio();
//                        versio.setNumero(Integer.parseInt(jobject.getString("Versio")));
//                        versio.setCodi(jobject.getString("Codi"));
//                        versio.setData(jobject.getString("Data"));
//                        versio.setDescripcio(jobject.getString("Descripcio"));
//                        versio.setDataAlta(Utils.getSqliteFormatDate());
//                        versio.setDataModificacio(Utils.getSqliteFormatDate());
//								/* Forced is going to be removed as soon as possible. It will be replaced by minimum_version. */
////								versio.forced = Boolean.parseBoolean(jobject.getString("force_update"));
//                        versio.minimumversion = Integer.parseInt(minimum_version);
//                    }
//                    else{
//                        doUpdate = false;
//                        versionsIguals= true;
//                    }
//                }
            }

        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return inputStream;


//        try {
//
//            String urlStr = "https://pvgisjson.com/api/v1/pv?";
//
//            ArrayList<NameValuePair> parametresPost = new ArrayList<NameValuePair>();
//            String query = "";
//
//            parametresPost.add(new BasicNameValuePair("lat", String.valueOf(bundle.getDouble(Constants.LATITUDE))));
//            parametresPost.add(new BasicNameValuePair("lng", String.valueOf(bundle.getDouble(Constants.LONGITUDE))));
//            parametresPost.add(new BasicNameValuePair("pvtech", bundle.getString(Constants.PVTECH)));
//            parametresPost.add(new BasicNameValuePair("peakpower", String.valueOf(bundle.getDouble(Constants.PEAKPOWER))));
//            parametresPost.add(new BasicNameValuePair("losses", String.valueOf(bundle.getDouble(Constants.LOSSES))));
//            parametresPost.add(new BasicNameValuePair("mounting", bundle.getString(Constants.MOUNTING)));
//            parametresPost.add(new BasicNameValuePair("slope", String.valueOf(bundle.getDouble(Constants.SLOPE))));
//            parametresPost.add(new BasicNameValuePair("azimuth", String.valueOf(bundle.getDouble(Constants.AZIMUTH))));
//
//            query = Utils.getQuery(parametresPost);
//
//            urlStr += query;
//
//            HttpsURLConnection https = (HttpsURLConnection) (new URL(urlStr)).openConnection();
//
//            https.setReadTimeout(12500);
//            https.setConnectTimeout(12500);
//            https.setRequestMethod("GET");
//            https.setDoInput(true);
//            https.setDoOutput(true);
//
//            https.setFixedLengthStreamingMode(query.length());
//
//            // Afegim els paràmetres per POST:
//            OutputStream os = https.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//            writer.write(query);
//            writer.flush();
//            writer.close();
//            os.close();
//            https.connect();
//            return https.getInputStream();
//        }
//        catch(SSLHandshakeException e) {
//            this.cancel(true);
//            errorSSL = true;
//            return null;
//        }
//        catch (Exception e) {
//            return null;
//        }
    }

    private static String getStringFromInputStream(InputStream is) {

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

}
