package com.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TwodArrayConversion {

    public static void main(String[] args) {
        TwodArrayConversion twodArrayConversion = new TwodArrayConversion();
        twodArrayConversion.convertListOfListTo2DArray();

    }

    private void convertListOfListTo2DArray()
    {
        //This is just to create a list of list as an input.
        List<List<String>> outerList = new ArrayList<>();

        List<String> innerList ;
        for(int i =0;i<10 ;i++)
        {
            innerList = new ArrayList<>();
            innerList.add("Akku"+i);
            outerList.add(innerList);

        }
        System.out.println("Print input" + outerList.toString());


        //Using Java8 Lambda streams to convert it to 2D array

        String[][] array = outerList.stream().map(iL -> iL.stream().
                toArray(String[]::new)).toArray(String[][]::new);


        System.out.println("Print 2D array" + Arrays.deepToString(array));
    }
}
