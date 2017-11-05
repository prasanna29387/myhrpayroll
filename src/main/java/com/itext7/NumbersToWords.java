package com.itext7;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

public class NumbersToWords {

    private static final String[] tensNames = {
            "",
            " ten",
            " twenty",
            " thirty",
            " fourty",
            " fifty",
            " sixty",
            " seventy",
            " eighty",
            " ninety"
    };

    private static final String[] numNames = {
            "",
            " one",
            " two",
            " three",
            " four",
            " five",
            " six",
            " seven",
            " eight",
            " nine",
            " ten",
            " eleven",
            " twelve",
            " thirteen",
            " fourteen",
            " fifteen",
            " sixteen",
            " seventeen",
            " eighteen",
            " nineteen"
    };


    private static String convertLessThanOneThousand(int number) {
        String soFar;

        if (number % 100 < 20) {
            soFar = numNames[number % 100];
            number /= 100;
        } else {
            soFar = numNames[number % 10];
            number /= 10;

            soFar = tensNames[number % 10] + soFar;
            number /= 10;
        }
        if (number == 0) return soFar;
        String prefix = StringUtils.isNotEmpty(soFar) ? " hundred and " : " hundred ";
        return numNames[number] + prefix + soFar;
    }


    public static String convert(long number) {
        // 0 to 999 999 999 999
        if (number == 0) {
            return "zero";
        }

        String snumber = Long.toString(number);

        // pad with "0"
        String mask = "000000000000";
        DecimalFormat df = new DecimalFormat(mask);
        snumber = df.format(number);

//        // XXXnnnnnnnnn
//        int billions = Integer.parseInt(snumber.substring(0, 3));
//        // nnnXXXnnnnnn
//        int millions = Integer.parseInt(snumber.substring(3, 6));
        // nnnnnXXnnnnn
        int tenLakhs = Integer.parseInt(snumber.substring(5, 7));
        // nnnnnnnXXnnn
        int tenThousands = Integer.parseInt(snumber.substring(7, 9));
        // nnnnnnnnnXXX
        int thousands = Integer.parseInt(snumber.substring(9, 12));


        String tradTenLaks;
        switch (tenLakhs) {
            case 0:
                tradTenLaks = "";
                break;
            case 1:
                tradTenLaks = "one lakh ";
                break;
            default:
                tradTenLaks = convertLessThanOneThousand(tenThousands)
                        + " lakh ";
        }
        String result = tradTenLaks;


        String tradTenThousands;
        switch (tenThousands) {
            case 0:
                tradTenThousands = "";
                break;
            case 1:
                tradTenThousands = "one thousand ";
                break;
            default:
                tradTenThousands = convertLessThanOneThousand(tenThousands)
                        + " thousand ";
        }
        result = result + tradTenThousands;

        String tradThousand;
        tradThousand = convertLessThanOneThousand(thousands);
        result = result + tradThousand;

        // remove extra spaces!
        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }

    public static void main(String[] args) {
        System.out.println("*** " + NumbersToWords.convert(0));
        System.out.println("*** " + NumbersToWords.convert(7551));
        System.out.println("*** " + NumbersToWords.convert(75551));
        System.out.println("*** " + NumbersToWords.convert(99999));
        System.out.println("*** " + NumbersToWords.convert(200));
        System.out.println("*** " + NumbersToWords.convert(100000));
        System.out.println("*** " + NumbersToWords.convert(1110000));

    }
}

