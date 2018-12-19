/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imported;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

/**
 *
 * @author Saud
 */
public abstract class SampleDataGen {
    
    private static Random rand = new Random();
    
    public abstract String run(int tupleNumber, int totalTuples);
    
    public static void setSeed(long seed) {
        rand = new Random(seed);
    }
    
    public static String randString(int length, String charRange) {
        charRange = formatCharRange(charRange);
        String out = "";
        for(int i=0; i < length; i++) {
            out += charRange.charAt(randInt(0, charRange.length()-1));
        }
        return out;
    }
    
    public static String randHex(int length) {
        return randString(length, "0-9A-F");
    }
    
    public static String randWord() {
        return randString(randInt(5,10), "a-z");
    }
    
    public static String randWords(int wordCount) {
        return randWords(wordCount, " ");
    }
    
    public static String randWords(int wordCount, String separator) {
        String out = randWord();
        for(int i=0; i < wordCount-1; i++) {
            out += separator + randWord();
        }
        return out;
    }
    
    public static String randEmail() {
        return randWord()+"@"+randWord()+".com";
    }
    
    public static String randPostCode() {
        return randString(randInt(1,2),"A-Z") + randString(randInt(1,2),"1-9")
                    + " " + randString(randInt(1,2),"A-Z") + randString(randInt(1,2),"1-0");
    }
    public static String randAddressLn1() {
        return randInt(1, 99) + " " + randWords(randInt(1,2));
    }
    public static String randAddressLn2() {
        return randWords(randInt(1,2));
    }
    public static String randAddressLn3() {
        return randWords(1);
    }
    public static String randCountryCode() {
        return randString(randInt(2,5),"A-Z");
    }
    
    /**
     * 
     * @param startDate "1999-12-31"
     * @param endDate "1999-12-31"
     * @return "1999-12-31"
     */
    public static String randDate(String startDate, String endDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            long start = df.parse(startDate).getTime()/1000;
            long end = df.parse(endDate).getTime()/1000;
            long sum = start + SampleDataGen.randInt(0, (int) (end-start));
            return df.format(sum*1000);
        } catch(Exception e) {}
        return null;
    }
    
    /**
     * get a random date between...
     * @param startDateTime eg "1999-12-31 23:59:59"
     * @param endDateTime eg "1999-12-31 23:59:59"
     * @return a random DATETIME eg "19991231 12:59:59 pm"
     */
    public static String randDateTime(String startDateTime, String endDateTime) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long start = df.parse(startDateTime).getTime()/1000;
            long end = df.parse(endDateTime).getTime()/1000;
            long sum = start + SampleDataGen.randInt(0, (int) (end-start));
            return df.format(sum*1000);
        } catch(Exception e) {}
        return null;
    }
    
    /**
     * returns a random number
     * @param min the minimum value (inclusive)
     * @param max the maximum value (inclusive)
     * @return a random number
     */
    public static int randInt(int min, int max) {
        return rand.nextInt(max-min+1)+min;
    }
    
    public static double randDouble(double min, double max) {
        return Math.random() * (max-min) + min;
    }
    
    public static double randLongitude() {
        return randDouble(-180, 180);
    }
    
    public static double randLatitude() {
        return randDouble(-90, 90);
    }
    
    public static int randBool(double chanceOfOne) {
        if(Math.random() <= chanceOfOne) return 1;
        return 0;
    }
    
    private static String formatCharRange(String charRange) {
        String out = "";
        for(int i=0; i < charRange.length(); i++) {
            if(charRange.charAt(i) == '-') {
                
                int charsToAdd = charRange.charAt(i+1) - charRange.charAt(i-1);
                for(int j=1; j < charsToAdd; j++) {
                    out += (char) ((int) charRange.charAt(i-1) + j);
                }
                
            } else {
                out += charRange.charAt(i);
            }
        }
        return out;
    }
    
}
