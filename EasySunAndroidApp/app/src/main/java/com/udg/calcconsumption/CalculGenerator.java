/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udg.calcconsumption;
import java.lang.Math;
import static java.lang.Math.pow;
import java.util.ArrayList;

/**
 *
 * @author marcc
 */
public class CalculGenerator {
    private Double[] generacioMensual;
    private double potenciaInstalada;
    private double inclinacio;
    private double orientacioRespecteSud;
    private double superficie;
    private double latitud;
    private double phi;
    
    public void CalculGenerator(Double[] dades,double _potenciaInstalada,double _inclinacio, double _orientacioRespecteSud, double _superficie, double _latitud){
        generacioMensual=dades;
        potenciaInstalada = _potenciaInstalada;
        inclinacio = _inclinacio;
        orientacioRespecteSud = _orientacioRespecteSud;
        superficie = _superficie;
        latitud = _latitud;
        phi = GetPhi();
    }
    
    public Double[] GetGeneracioMensual(){
        Double[] newGeneracioMensual = new Double[12];
        for(int i = 0; i < 12 ; i++){
            newGeneracioMensual[i] = generacioMensual[i]*potenciaInstalada*phi*1000*superficie;
        }
        return newGeneracioMensual;
    }
    
    private double GetPhi(){
        return 1-(1.2*pow(10,-4)*pow((inclinacio-(3.7+0.89*latitud)),2)+3.5*pow(10,-5)*pow(orientacioRespecteSud,2));
    }
}
