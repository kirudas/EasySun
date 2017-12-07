package com.udg.calcconsumption;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class PvgisParser {

    JSONObject monthlyAverage;
    String message;

    HashMap<String, Double> dades;
    String [] nomMes = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public PvgisParser(String pathFile) throws PvgisParserException {

        File file = new File(pathFile);

        if (!file.exists())
            throw new PvgisParserException("El fitxer no exiteix");

        try {

            InputStream is = new FileInputStream(file);

            JSONObject fileObject = new JSONObject(Utils.getStringFromInputStream(is));
            message = (String) fileObject.get("message");
            monthlyAverage = fileObject.getJSONObject("data").getJSONObject("output").getJSONObject("monthlyAverage");

            if (!message.equals("Ok"))
                throw new PvgisParserException("No hem pogut recuperar les dades");

            setupData();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new PvgisParserException("El fitxer no exiteix");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new PvgisParserException("JSON mal format");
        }


    }

    private void setupData() {

        for (String month : nomMes) {

            try {
                double aux = (double) (monthlyAverage.getJSONObject(month)).get("Ed");
                dades.put(month, aux);
                Log.e("JSON_PARSER", month + ": "+ aux);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public HashMap<String, Double> getDades() {
        return this.dades;
    }

}
