package src.utils;

import org.joda.time.DateTime;
import org.joda.time.Months;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class kpi {

    public static LinkedList<Double> list =  new LinkedList<Double>();
    //Se alamacena solo los dos meses anteriores
    private static DateTime lastUpDate = new DateTime(0);
    //Lleva el recuento del mes actual
    public static Integer NumeroTotal = 0;
    public static Integer NumeroPositivos = 0;


    public static void addOne(boolean pos){
        //Comprobamos diferencia de fechas
        DateTime now=new DateTime();
        Months diference=Months.monthsBetween(lastUpDate,now);

        if(diference.getMonths()!=0){
            rotate();
        }
        NumeroTotal++;
        if(pos)NumeroPositivos++;
        lastUpDate= now;
    }

    public static void Initialize(){
        list.add(0.0);
        list.add(0.0);
        lastUpDate= DateTime.now();
        NumeroPositivos=0;
        NumeroTotal=0;
    }

    private static void rotate(){
        Double porcentaje=((double) NumeroPositivos)/NumeroTotal;
        Integer value = getValue(porcentaje);
        //write value on file
        if(!list.isEmpty()) {
            list.removeLast();
        }
        list.addFirst(porcentaje);
        NumeroTotal=0;
        NumeroPositivos=0;
    }

    private static Integer getValue(double porcentaje){
        Integer res = null;

        if(list.isEmpty()) {
            // Si está vacio, ponemos como base 0
            res = 0;
            writeFile("0");
        } else {
            if(list.getLast() > porcentaje || list.getFirst() > porcentaje){
                res= -1;
                writeFile("-");
            } else if(list.getLast()<porcentaje||list.getFirst()<porcentaje){
                res=1;
                writeFile("+");
            } else {
                res= 0;
                writeFile("0");
            }
        }
        
        return res;
    }
    private static void writeFile(String value){
        try{
            String text;
            DateTime now=DateTime.now();
            text=String.valueOf(now.getYear());
            text=text.concat(",");
            text=text.concat(String.valueOf(now.getMonthOfYear()));
            text=text.concat(",");
            text=text.concat(value);
            text=text.concat("\n");
            File file = new File("kpi.txt");

            FileWriter writer= new FileWriter(file,true);
            writer.write(text);
            writer.flush();
            writer.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
//========================================================================
//========================================================================
//========================================================================
//========================================================================

    public static void  setList(LinkedList<Double>newList){
        list=newList;
    }
    public static LinkedList getList(){
        return list;
    }

    public static void forceRotate(){
        rotate();
    }
    public static Integer getValue(){
        Integer res = null;
        Double porcentaje=((double) NumeroPositivos)/NumeroTotal;

        if(list.getLast()>porcentaje||list.getFirst()>porcentaje){
            res= -1;
            writeFile("-");

        }else if(list.getLast()<porcentaje||list.getFirst()<porcentaje){
            res=1;
            writeFile("+");

        }else {
            res= 0;
            writeFile("0");

        }
        return res;
    }

}
