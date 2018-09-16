package com.util;

import java.util.Scanner;

public class CollinearPoints {

    public static String FindLinear1(int a[], int b[]){

        //Checking if they are collinear using slopes of AB & BC


        int slope1 = (b[1] - b[0])/(a[1] - a[0]);
        int slope2 = (b[2] - b[1])/(a[2] - a[1]);

        if(slope1 == slope2){
            return "They all lie on the same line, collinear";
        }
        else {
            return "They do not lie on the same line";
        }





    }
    public static String FindLinear2(int a[], int b[]){

        //using the area of a triangle formula

        double area = 0.5*((a[0]*(b[1]-b[2])) + (a[1]*(b[2]-b[0])) + (a[2]*(b[0]-b[1])));

        if(area==0.0){
            return "They all lie on the same line, collinear";
        }
        else{
            return "They do not lie on the same line";
        }


    }



    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int [] xcords = new int[3];
        int [] ycords = new int[3];
        for(int i=0;i<xcords.length;i++){
            System.out.println("Enter point x"+i);
            xcords[i] = scan.nextInt();
        }
        for(int i=0;i<ycords.length;i++){
            System.out.println("Enter point y"+i);
            ycords[i] = scan.nextInt();
        }

        String result =FindLinear1(xcords, ycords);
        String result2 =FindLinear2(xcords, ycords);
        System.out.println(result);
        System.out.println(result2);
    }

}
