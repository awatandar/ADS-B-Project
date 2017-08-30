/* this class decodes the raw data passed to it by ADSB_UDP_RECEIVER class
    and processes it.

    Last Updated: 6/04/2017
    abdullah watandar
 */
package adsb_project;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Scanner;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.io.UnsupportedEncodingException;
import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.util.List;
import java.lang.Math.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 *
 * @author c09784420
 */
public class ADSB_DECODER {

    private String rawPacket;
    private String binaryPacket;
    private String DF;
    private String ICAO;
    private String data;
    private String crc;
    private double latE;
    private double latO;
    private double latitude;
    private double longitude;
    private double altitude;
    private static double NZ = 15; // this value of NZ (number of geographic latitude zones) is for Mode-S CPR encoding (dealth with in here)
    private double NL_lat; // represents the Number of Longitude Zones (function) using the latitude angle (lat)
    private double tE;
    private double tO;
    private static double Pi = Math.PI;
    private static String charLookUp = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ     _               0123456789      ";

    public ADSB_DECODER() {
        /*
     this.binaryPacket = binPacket;
     this.DF = df;
     this.ICAO = icao;
     this.data = data;
     this.PI = pi;
         */

    }

//+++++++++++++
    public String getICAO() {  // process the 24 bit ICAO part of packets

        String icao = charLookUp;
        String ICAOBin = ICAO;
        int icaoLength = ICAOBin.length();

        int j1 = 0; // used to mark the start of each 6 bit bunch of binary numbers
        int j2 = 6;// used to mark the end ...

        int[] icaoCode = new int[4]; // store the decimal code
        String icaoString = ""; // store the chars looked up

        System.out.println("\n ---------- getICAO() -----------------\n");

        for (int i = 0; i < 4; i++) {
            // chop up the String, convert to decimal, to char, put in string and print out:
            String icaoParts = ICAOBin.substring(j1, j2);
            int icaoDecimal = Integer.parseInt(icaoParts, 2);
            icaoCode[i] = icaoDecimal;
            char caoChar = icao.charAt(icaoDecimal);
            icaoString = icaoString + caoChar;
            j1 = j2;
            j2 = j2 + 6;
            System.out.println("\n" + icaoParts + "\t" + icaoDecimal + "\t" + caoChar);
            //System.out.print(mychar);
        }

        System.out.println("\nFull Aircraft ICAO Address:\t" + icaoString + "\n\n");

        return icaoString;

    }

//+++++++++++++
    public double getAltitude(String eveAlt, String oddAlt) {  // process the 56 bit DATA part of packets
        System.out.println("\n=============== getAltitude()========================================================\n\n");

        String evenQbit = eveAlt.substring(7, 8);   // take the Q bit out
        String oddQbit = oddAlt.substring(7, 8);    // Q bit for odd frame

        String eveAlt1 = eveAlt.substring(0, 7);    // first part of the altitude (before the Qbit)
        String eveAlt2 = eveAlt.substring(8);       // second part of the altitude (after the Qbit)

        String oddAlt1 = eveAlt.substring(0, 7);    // first part for odd alt
        String oddAlt2 = eveAlt.substring(8);       // second part for odd alt

        String evenAltCombined = eveAlt1 + eveAlt2;     // combine the two parts of even alt
        String oddAltCombined = oddAlt1 + oddAlt2;      // combine the two parts of odd alt

        double evenAltCombinedInt = Integer.parseInt(evenAltCombined, 2);  //convert the conbined alt to integer
        double oddAltCombinedInt = Integer.parseInt(oddAltCombined, 2);    // convert the combined odd alt

        double evenAltitude = Integer.parseInt(eveAlt, 2);
        double oddAltitude = Integer.parseInt(oddAlt, 2);

        double alttude = evenAltCombinedInt;// for testing
//---------------------------------------------------------------------------------------------------------------------

        if (evenQbit.equals("1")) { // if Q bit is 1, then: alt*25

            evenAltitude = (evenAltCombinedInt * 25) - 1000;
            oddAltitude = (oddAltCombinedInt * 25) - 1000;

        } else { // if Q bit is 0 then: alt*100

            evenAltitude = (evenAltCombinedInt * 100) - 1000;
            oddAltitude = (oddAltCombinedInt * 100) - 1000;
        }
//---------------------------------------------------------------------------------------------------------------------

        //display Altitude details //
        System.out.print("\neven alt:\t" + eveAlt + "\nodd alt:\t" + oddAlt + "\nQ bit even:\t" + evenQbit + "\nQ bit odd:\t" + oddQbit + "\n");
        System.out.println("\nevenAltCombined:\t" + evenAltCombined
                + "\nevenAltCombined int:\t"
                + evenAltCombinedInt
                + "\n\noddAltCombined:\t\t"
                + oddAltCombined
                + "\nnoddAltCombined int:\t"
                + oddAltCombinedInt + "\n");

//---------------------------------------------------------------------------------------------------------------------

        /* displays details */
        System.out.println("\neveAlt:\t\t" + eveAlt + "\nevntAlt int\t" + evenAltitude
                + "\n\noddAlt:\t\t" + oddAlt + "\noddtAlt int:\t" + oddAltitude + "\n\n");
        /*end displays details*/

        altitude = evenAltitude;
        
        System.out.println("(((((((((((((((((((((("+altitude);

        return alttude; // this should be an integer array.
    }

//---------------------------------------------------------------------------------------------------------------------
    public double getLatitude(String eveLat, String oddLat) { // pads data if leading zeros missing
        System.out.println("\n==============getLatitude()==================== \n");

//---------------------------------------------------------------------------------------------------------------------
        //convert the latitude values:
        double evenLatt = Integer.parseInt(eveLat, 2);
        double oddLatt = Integer.parseInt(oddLat, 2);
        double evLatitude = (double) Math.round(evenLatt / 131072 * 10000d) / 10000;//round the decimal places of evenLatt/131072 to 4 digits.
        double odLatitude = (double) Math.round(oddLatt / 131072 * 10000d) / 10000;//round the decimal places of oddLatt/131072 to 4 digits;

//---------------------------------------------------------------------------------------------------------------------
        //simplifying and implementing the NL(lat) formula:
        double lat = 0; // the value of this variable should be determine. I dont know how.

        double a = Pi / (2 * NZ);
        double b = (Pi / 180) * lat;
        double c = (1 - Math.cos(a)) / Math.sqrt(Math.cos(b));
        double d = 1 - (c);
        //double d = 1-(1 - Math.cos(a)/Math.sqrt(Math.cos(b)));
        //double f = 1 - (c);
        //NL_lat = Math.floor(2 * Pi / Math.acos(c));
        //NL_lat = Math.floor(2 * Pi / Math.acos(f));

        NL_lat = Math.floor(2 * Pi / Math.acos(1 - (1 - Math.cos(Pi / (2 * NZ)) / Math.sqrt(Math.cos((Pi / 180) * lat)))));
        // double f = 1 - (1 - Math.cos(Pi / (2 * NZ))/Math.sqrt(Math.cos(Pi / (180 * lat))));
        // double c = 1 - Math.cos(Pi / (2 * NZ))/Math.sqrt(Math.cos(Pi / (180 * lat)));

        System.out.println("\nNL(lat):\t" + NL_lat + "\n");

//---------------------------------------------------------------------------------------------------------------------
        //calculating the latitude index:       
        double latIndex = Math.ceil((59 * evLatitude) - 60 * odLatitude + (1 / 2));
        System.out.println("lat index:\t\t" + latIndex + "\n");

//---------------------------------------------------------------------------------------------------------------------      
        // calculating latitudes - step 1:
        double evenLat1 = 360 / (4 * NZ);//DLatE  - it is a constant
        double oddLat1 = 360 / (4 * NZ - 1);//DLatO - a constant
        //System.out.println("\nDlatE:\t" + evenLat1 + "\nDlatO:\t" + oddLat1 + "\n\n");

//---------------------------------------------------------------------------------------------------------------------       
        // calculating the relative latitudes - Step 2:
        double evLat2 = evenLat1 * ((latIndex % 60) + evLatitude); // LatE
        double odLat2 = oddLat1 * ((latIndex % 59) + odLatitude);// latO

        evLat2 = (double) Math.round(evLat2 * 10000d) / 10000d;
        odLat2 = (double) Math.round(odLat2 * 10000d) / 10000d;
        latE = evLat2;
        latO = odLat2;

//---------------------------------------------------------------------------------------------------------------------        
        // make sure thate the latitude falls within range of -90 and +90
        if (evLat2 >= 270) {

            evLat2 = evLat2 - 360;

        } else if (odLat2 >= 270) {

            odLat2 = odLat2 - 360;
        }
//---------------------------------------------------------------------------------------------------------------------

        // which latitude to choose? the newest one, with latest time stamp, is chosen:
        if (tE > tO) { // the times stamp needs to be extracted
            latitude = latE;
        } else { // if odd fram time is greater than the even frame time

            latitude = latO;

        }

//---------------------------------------------------------------------------------------------------------------------
        // display the final latitude values
        System.out.println("even latitude final :\t"
                + evLat2 + "\nodd latitude final:\t"
                + odLat2 + "\n\nfinal latitude:\t\t"
                + latitude);

//---------------------------------------------------------------------------------------------------------------------        
        double lattude = evenLatt;// for testing

        return lattude;

    }

    public double getLongtitude(String eveLong, String oddLong) { // pads data if leading zeros missing

        System.out.println("\n==============getLongtitude()==================== \n");

        double evenLongtitude = Integer.parseInt(eveLong, 2);
        double oddLongtitude = Integer.parseInt(oddLong, 2);

        evenLongtitude = evenLongtitude / 131072;
        oddLongtitude = oddLongtitude / 131072;

        // if Te > To:
        if (tE > tO) { // no Time stamp has been extracted so far.
            double niIndex = Math.max(NL_lat * (latE), 1);
            double dLong = 360 / niIndex;
            double mIndex = Math.floor(evenLongtitude * (NL_lat * (latE) - 1) - oddLongtitude * (NL_lat * (latE) + 1 / 2));
            double longE = dLong * (mIndex % niIndex) + evenLongtitude;
            longitude = longE;
            
            
        } else{
                //if To >Te:
                double niIndexO = Math.max(NL_lat * (latO) - 1, 1);
                double dLongO = 360 / niIndexO;
                double mIndexO = Math.floor(evenLongtitude * ((NL_lat * (latO) - 1)) - oddLongtitude * (NL_lat * (latO) + 1 / 2));
                double longO = dLongO * (mIndexO % niIndexO) + oddLongtitude;
                //longO = longO - 360;
                longitude = longO;
                
            }
        
        System.out.println("longO:\t" + longitude + "\n");
        
        System.out.println("\n\n\neven Long:\t" + eveLong + "\neven Long int:\t" + evenLongtitude
                + "\n\nodd Long:\t" + oddLong + "\nodd Long int:\t" + oddLongtitude + "\n\n");

        return longitude;

    }

    public void getCRC() {    // process the 24 bit PI (Error Checking - Parity ) part of packets
        System.out.println("\n\n=============== getPI() output ============================================================\n\n");

        char sign = '4';
        String rawCRC = crc;
        String testing = "1100101";

        //String piBinary1 =  Integer.toBinaryString(Integer.parseInt(rawPI, 16));
        String piBinary = crc;//Integer.toBinaryString(Integer.parseInt("1" + rawPI, 16)).substring(1);

        int piBinLength = piBinary.length();
        String piBinary2 = paddData(piBinary, sign);
        int piBinLength2 = piBinary2.length();
//---------------------------------------------------------------------------------------------------------------------

        System.out.println("\nPI raw:\t\t\t" + rawCRC + "\nPI in Binary:\t\t" + piBinary
                + "\nPI length:\t\t" + piBinLength + "\n\nPI Binary padded:\t" + piBinary2
                + "\nPI Bin. padded length:\t" + piBinLength2 + "\n\n\n++++++++++++++++++++++++++ End "
                + "++++++++++++++++++++++++++++++++++++++++++++\n"
                + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n\n");

    }

    public void passData() { // pads data if leading zeros missing

        List<Double> itemList = Arrays.asList(altitude, latitude, longitude);
/*
        try {

            TCP_SERVER_TESTING tcpServer = new TCP_SERVER_TESTING(itemList);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
  */      
        PRESENT_GUI acojfar = new PRESENT_GUI(altitude, latitude, longitude, ICAO );

    }

    public String paddData(String shortString, char sign) { // pads data if leading zeros missing

        String paddedData = "";

        return paddedData;

    }

}// end class
