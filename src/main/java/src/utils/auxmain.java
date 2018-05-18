package src.utils;
import src.utils.kpi;

import java.util.LinkedList;

public class auxmain {

    public static void main(String[] args) throws InterruptedException {
    kpi.Initialize();
    System.out.println(kpi.getList());
    kpi.addOne(true);
    System.out.println(kpi.NumeroPositivos);
    System.out.println(kpi.NumeroTotal);

    kpi.addOne(false);
    System.out.println(kpi.NumeroPositivos);
    System.out.println(kpi.NumeroTotal);

    LinkedList<Double> testList = new LinkedList<Double>();
    testList.add(0.5);
    testList.add(0.5);
    kpi.NumeroPositivos=0;
    kpi.NumeroTotal=10;
    kpi.setList(testList);
    System.out.println("=============================Los dos anteriores son mayores=============================");
    System.out.println(kpi.getList());
    System.out.println(kpi.getValue());


    testList.set(0,0.5);
    testList.set(1,0.0);
    kpi.NumeroPositivos=2;
    kpi.NumeroTotal=10;
    kpi.setList(testList);
    System.out.println("=============================Solo el index 0 es mayor=============================");
    System.out.println(kpi.getList());
    System.out.println(kpi.getValue());




    testList.set(0,0.0);
    testList.set(1,0.5);
    kpi.NumeroPositivos=2;
    kpi.NumeroTotal=10;
    kpi.setList(testList);
    System.out.println("=============================Solo el index 1 es mayor=============================");
    System.out.println(kpi.getList());
    System.out.println(kpi.getValue());




    testList.set(0,0.0);
    testList.set(1,0.0);
    kpi.NumeroPositivos=2;
    kpi.NumeroTotal=10;
    kpi.setList(testList);
    System.out.println("=============================los dos index son menores=============================");
    System.out.println(kpi.getList());
    System.out.println(kpi.getValue());



    testList.set(0,0.0);
    testList.set(1,1.0);
    kpi.NumeroPositivos=10;
    kpi.NumeroTotal=10;
    kpi.setList(testList);
    System.out.println("=============================Solo el index 0 es menor y el 1 es igual=============================");
    System.out.println(kpi.getList());
    System.out.println(kpi.getValue());


    testList.set(0,1.0);
    testList.set(1,0.0);
    kpi.NumeroPositivos=10;
    kpi.NumeroTotal=10;
    kpi.setList(testList);
    System.out.println("=============================Solo el index 1 es menor y el 0 es igual=============================");
    System.out.println(kpi.getList());
    System.out.println(kpi.getValue());


    testList.set(0,1.0);
    testList.set(1,1.0);
    kpi.NumeroPositivos=10;
    kpi.NumeroTotal=10;
    kpi.setList(testList);
    System.out.println("=============================Todos son iguales=============================");
    System.out.println(kpi.getList());
    System.out.println(kpi.getValue());


    }

}
