package com.udg.calcconsumption;

import android.graphics.Color;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Grafic extends AppCompatActivity {

    LineChart mChart;
    DadesConsum dc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafic);

        mChart = (LineChart) findViewById(R.id.mChart);
        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        loadData(getIntent().getExtras());
    }

    private void loadData(Bundle extras) {

        copyAssetFileToDevice();

        double pInst = extras.getDouble(Constants.POTENCIA_INSTALACIO);
        double incl = extras.getDouble(Constants.INCLINACIO);

        // add data
        setData(pInst, incl);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
    }

    private void copyAssetFileToDevice() {
            InputStream inputStream = null;
            OutputStream outStream = null;
            try{
                inputStream = this.getAssets().open("ConsumAnual.csv");
                outStream = new FileOutputStream(Utils.getRutaBasePublica(this)+"ConsumAnual.csv");
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0){
                    outStream.write(buffer,0,length);
                }
                outStream.flush();
                outStream.close();
                inputStream.close();
            }
            catch (IOException e){
                throw new Error("Problema al copiar la base de dades del respositori!");
            }
    }

    private ArrayList<String> setXAxisValues(){
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("GEN");
        xVals.add("FEB");
        xVals.add("MAR");
        xVals.add("ABR");
        xVals.add("MAI");
        xVals.add("JUN");
        xVals.add("JUL");
        xVals.add("AGO");
        xVals.add("SEP");
        xVals.add("OCT");
        xVals.add("NOV");
        xVals.add("DES");
        xVals.add("");

        return xVals;
    }

    private ArrayList<Entry> setYAxisValues1(){
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry(18000, 0));
        yVals.add(new Entry(13600, 1));
        yVals.add(new Entry(13000, 2));
        yVals.add(new Entry(14700, 3));
        yVals.add(new Entry(16000, 4));
        yVals.add(new Entry(16800, 5));
        yVals.add(new Entry(17500, 6));
        yVals.add(new Entry(18600, 7));
        yVals.add(new Entry(16000, 8));
        yVals.add(new Entry(16000, 9));
        yVals.add(new Entry(19000, 10));
        yVals.add(new Entry(19000, 11));

        return yVals;
    }

    private ArrayList<Entry> setYAxisValues2(double pInst, double incl){
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry((float) (8000 * pInst), 0));
        yVals.add(new Entry((float) (10800 * pInst), 1));
        yVals.add(new Entry((float) (14000 * pInst), 2));
        yVals.add(new Entry((float) (15000 * pInst), 3));
        yVals.add(new Entry((float) (17000 * pInst), 4));
        yVals.add(new Entry((float) (18500 * pInst), 5));
        yVals.add(new Entry((float) (18300 * pInst), 6));
        yVals.add(new Entry((float) (17300 * pInst), 7));
        yVals.add(new Entry((float) (14400 * pInst), 8));
        yVals.add(new Entry((float) (11500 * pInst), 9));
        yVals.add(new Entry((float) (8500 * pInst), 10));
        yVals.add(new Entry((float) (7300 * pInst), 11));

        return yVals;
    }

    private void setData(double pInst, double incl) {
        ArrayList<String> xVals = setXAxisValues();

        ArrayList<Entry> yVals1 = setYAxisValues1();

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals1, "Consum diari");
        set1.setFillAlpha(110);

        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        ArrayList<Entry> yVals2 = setYAxisValues2(pInst, incl);

        LineDataSet set2;

        // create a dataset and give it a type
        set2 = new LineDataSet(yVals2, "Generació diària");
        set2.setFillAlpha(110);

        set2.setColor(Color.RED);
        set2.setCircleColor(Color.RED);
        set2.setLineWidth(1f);
        set2.setDrawCircleHole(false);
        set2.setValueTextSize(9f);
        set2.setDrawFilled(true);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);

    }


}
