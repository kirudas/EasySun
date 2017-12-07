/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udg.calcconsumption;

import android.graphics.Path;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
/**
 *
 * @author marcc
 */

public class DadesConsum {

    private ArrayList<Pair<Date, Integer>> consumHorari;
   
    public DadesConsum(){
        consumHorari=new ArrayList<Pair<Date, Integer>>();
    }
    
    public void parserEndesa(String mypath) {
        List<String> consum = new ArrayList<String>();
        try{
            Charset charset = Charset.forName("ISO-8859-1");
            File f = new File(mypath);

            FileInputStream is;
            BufferedReader reader;

            if (f.exists()) {
                is = new FileInputStream(f);
                reader = new BufferedReader(new InputStreamReader(is));
                String line = reader.readLine();
                while(line != null){
                    line = reader.readLine();
                    consum.add(line);
                }
            }

        }
        catch(Exception e){
            System.out.println(e);
        }        
        load(consum);
    }
    
    private void load(List<String> dades){
        int numlinia = 0;
        for (String fila : dades) {
            if (numlinia >= 7) {
                String[] parts = fila.split(";");
                if (parts.length > 2) {
                    
                    String[] dia = parts[0].split("-");
                    int year = Integer.parseInt(dia[0]);
                    int month = Integer.parseInt(dia[1]);
                    int day = Integer.parseInt(dia[2]);
                    int hour = Integer.parseInt(parts[1]);
                    Date data = new Date();
                    int consumdiari = Integer.parseInt(parts[2]);

                    data.setYear(year);
                    data.setMonth(month);
                    data.setDate(day);
                    data.setHours(hour);
                    data.setMinutes(0);
                    data.setSeconds(0);
                    
                    Pair p1 = new Pair(data,consumdiari);                    
                    consumHorari.add(p1);
                    
                } 
            }
            numlinia = numlinia + 1;
        }
    }
    
    public int GetConsumData(Date data){
        int consum = -1;
        for(int i=0; i<consumHorari.size()-1;i++){
            Date d = consumHorari.get(i).first;
            
            if(d.getYear() == data.getYear() && d.getMonth() == data.getMonth() &&  d.getDate() == data.getDate()  &&  d.getHours() == data.getHours() ){
                consum = consumHorari.get(i).second;
                break;
            }
        }
        return consum;
    }
    
    public double GetConsumMesMitja(int any, int mes){
        int consumTotal = 0;
        int nDiesMes = 0;
        for(int i=0; i<consumHorari.size()-1;i++){
            Date d = consumHorari.get(i).first;
            
            if(d.getYear() == any && d.getMonth() == mes){
                consumTotal += consumHorari.get(i).second;
                nDiesMes += 1;
            }
        }
        if(nDiesMes == 0){
            return -1;
        }
        else{
            return consumTotal/nDiesMes;
        }
        
    }
    
    /*public double GetPrecioTarifaDiscriminatoria(double cara, double barata){
        double consumHivern = 0;
        double consumEstiu = 0;
        
        for(int i=0; i<consumHorari.size()-1;i++){
            Date d = consumHorari.get(i).getKey();
            
            if(d.getMonth() >= 9 || d.getMonth() <= 3){
                if(d.getHours() > )
                consumHivern += consumHorari.get(i).getValue();
            }
        }
    }*/
    
    /**
     * @param args the command line arguments
     
    public static void main(String[] args) {
        // TODO code application logic here
        DadesConsum m = new DadesConsum();
                
        Path n = Paths.get("C:/Users/marcc/OneDrive/Escritorio/ConsumAnual.csv");
        System.out.println(n);
        m.parserEndesa(n);
        //m.mostra();
        //2016-09-18;15;1401
        Date d = new Date();
        d.setYear(2016);
        d.setMonth(9);
        d.setDate(18);
        d.setHours(15);
        d.setMinutes(0);
        d.setSeconds(0);
        
        System.out.println(m.GetConsumData(d));
        System.out.println(m.GetConsumMesMitja(2016,9));
    }  */  
}
