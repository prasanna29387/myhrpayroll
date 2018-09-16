package com.util;

import java.util.HashMap;
import java.util.Scanner;

public class RomanNumeral {

    public static String romanform(int a){


        HashMap<Integer, String> roman = new HashMap<Integer, String>();

        int[] arabic = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};


        roman.put(1,"I");
        roman.put(4,"IV");
        roman.put(5,"V");
        roman.put(9,"IX");
        roman.put(10,"X");
        roman.put(40,"XL");
        roman.put(50,"L");
        roman.put(90,"XC");
        roman.put(100,"C");
        roman.put(400,"CD");
        roman.put(500,"D");
        roman.put(900,"CM");
        roman.put(1000,"M");






        String answer = "";
        for(int i: arabic){

            while (a >= i){
                answer += roman.get(i);
                a -= i;
            }

        }

        return answer;



    }

    public static void main(String[] args) {
        // TODO code application logic here
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();

        String result = romanform(n);
        System.out.println(result);







    }
}
