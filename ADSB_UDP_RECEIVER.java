/*
 /* this class receives the raw data, seperate the fields and them to the ADSB_DECODER class.

 	written: 3/02/2017 (abdullah watandar)
 	Last Updated: 06/04/2017
 */
package adsb_project;

/*
 * @author c09784420
 */
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.File;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ADSB_UDP_RECEIVER {

    public static void main(String[] args) {
       ADSB_UDP_RECEIVER x = new ADSB_UDP_RECEIVER();
       x.start();
       
    }
    public void start(){   
    System.out.println("=================================== ADSB_UDP_RECEIVER  ================================== \n");

        try {
            File myFile = new File("C:\\Users\\abdul\\Desktop\\Upd 27_4_17\\Adsb_Project\\output2t.txt");
            Scanner myScanData = new Scanner(myFile);
            int latitude = 23;
            int longitude = 12;
            int altitude = 240000;
            String[] evenPkt = new String[10];
            String[] oddPkt = new String[10];
            String[] evenICAO = new String[10];
            String[] oddICAO = new String[10];

            // ArrayLists declared
            List<String> evenPkList = new ArrayList<String>();
            List<String> oddPkList = new ArrayList<String>();
            List<String> evenIcaoList = new ArrayList<String>();
            List<String> oddIcaoList = new ArrayList<String>();

            while (myScanData.hasNextLine()) {

                String rawHexData = myScanData.nextLine();
                int rawHexDataLength = rawHexData.length();
                String binaryPacket = new BigInteger("1" + (rawHexData.substring(1, 29)), 16).toString(2).substring(1);

                String validData = checkData(rawHexData); // check Data

                if (binaryPacket.equals(validData)) { // check if result is OK

                    displayPktDetails(rawHexData, binaryPacket);

                    int binaryLength = binaryPacket.length();

                    String DF_Binary = binaryPacket.substring(0, 8);  // seperates the 8 bit DF and CA part of the packet
                    String ICAO_Binary = binaryPacket.substring(8, 32);  // removes the  24 bit ICAO part of the data packet
                    String Data_Binary = binaryPacket.substring(32, 88); //removes the 56 bit Data part of the data packet
                    String PI_Binary = binaryPacket.substring(88);  // removes the Parity/Interrogator ID part of the packet

                    // create an object and pass the data it needs
                    //ADSB_DECODER decoderObject = new ADSB_DECODER(binaryPacket, DF_Binary, ICAO_Binary, Data_Binary, PI_Binary);
                    //*************** only for data part *******************************************************************************
                    String tcBin = binaryPacket.substring(0, 5);

                    int tc = Integer.parseInt(tcBin, 2);
                    int countE = 0;
                    int countO = 0;
                    AIRCRAFT_INFO aircraftInfoOb = new AIRCRAFT_INFO();

                    System.out.println("TC:\t\t" + tcBin + "\ntc length:\t" + tcBin.length()
                            + "\ntc Int:\t\t" + tc
                            + "\n-------------------------\n\n aircraft identification details:\n");

                    if (tc >= 1 && tc <= 4) {
                        //String idbits = allDataBin.substring(5);
                        String planeCatagory = binaryPacket.substring(0, 8);
                        String idData = binaryPacket.substring(8);
                        String planeID = aircraftInfoOb.getID(idData, planeCatagory);
                        System.out.println("aircraft identification details:\t" + planeID);
                    } else if (tc >= 5 && tc <= 8) {
                        System.out.println("surface position.");
                    } else if (tc >= 9 && tc <= 18)// if packet contains airborne position data
                    {

                        //String fBit = binaryPacket.substring(0, 5);
                        int fBit = Integer.parseInt(binaryPacket.substring(53, 54), 2); // the 54th bit: if 0 the packet is even - if 1, the packet is odd

                        if (fBit == 0) {// if it is an even packet

                            evenIcaoList.add(ICAO_Binary);// these lists should replace strings arrays
                            evenPkList.add(binaryPacket);
                            //---------------------------//
                            evenICAO[countE] = ICAO_Binary;
                            evenPkt[countE] = binaryPacket;
                            System.out.println("\neven packet saved:\t" + evenICAO[countE] + "\t" + fBit);

                        } else { // if odd packet

                            oddIcaoList.add(ICAO_Binary);// these lists should replace strings arrays
                            oddPkList.add(binaryPacket);
                            //---------------------------//
                            oddICAO[countO] = ICAO_Binary;
                            oddPkt[countO] = binaryPacket;;
                            System.out.println("\nodd packet saved:\t" + oddICAO[countO] + "\t" + fBit);

                        }

                        //System.out.println("\naircraft position:\t"+decoderObject.getData(positionData) ) ;
                        //count++;  
                    } else if (tc == 19) {
                        System.out.println("airborn velocity.");
                    } else if (tc >= 20 && tc <= 22) {
                        System.out.println("airborn position(w/gnss hight).");
                    } else if (tc >= 23 && tc <= 31) {
                        System.out.println("other uses.");
                    }

                    //String evICAO = even[0].substring(8, 32);
                    //String odICAO = odd[0].substring(8, 32);
                    if (evenICAO[countE].equals(oddICAO[countO])) { // is the ICAO of odd and even packets are the same

                        System.out.println(" \n\n%%---------- Second Packet checked: same icao for even and odd pakcets ------------- %%\n\nICAOs:\n"
                                + evenICAO[0] + "\n" + oddICAO[0]);
                        // decoderObject.getPosition(evenPkt[countE], oddPkt[countO]); //call the getPosition() to decode position
                        System.out.println("\nevenPkt[countE]:\t" + evenPkt[countE] + "\noddPkt[countO]:\t\t" + oddPkt[countO] + "\n\n");
                        calculatePosition(evenPkt[countE], oddPkt[countO]);
                        

                    }

                } else {
                    System.out.println("--> Invalid Packet Discarded.\n");
                }

            }

        } catch (FileNotFoundException fnfE) {
            fnfE.printStackTrace();
        }
    }  //end main

    /**/
    public static String checkData(String pk) {
        /**/
        System.out.println("\n=========================== checkData() =============================================");
        int rawHexDataLength = pk.length(); // this length should be 30 (28 data +2 signs)
        //String trimdHexData = pk.substring(1, 29);
        String delimitExpected = "*;"; // expected first and last sign (delimiters) used for comparison
        String firstSign = pk.substring(0, 1); // first sign extracted
        String SecondSign = pk.substring(rawHexDataLength - 1);// last sign extracted
        String delimitsReceived = firstSign + SecondSign; // first and last sign in the packet, put together
        String validData = "";

        String trimdHexData = pk.substring(1, 29);
        String binaryPacket = new BigInteger("1" + trimdHexData, 16).toString(2).substring(1);

        String dfOnly = binaryPacket.substring(0, 5);// first 5 bits of DF byte
        int dfOnlyD = Integer.parseInt(dfOnly, 2);

        int resultExpct = 17;
        int resultRcvd = dfOnlyD;

        /* check if packet starts with * and ends with ; and check if 
        pakcet length is valid and if the packet is a df17 packet:      
         */
        if (delimitExpected.equals(delimitsReceived) && rawHexDataLength == 30 && resultExpct == resultRcvd) {
            System.out.println("-->  Valid DF17 Packet Received.\n");

            validData = binaryPacket;

        } else {
            validData = "discarded";
        }

        return validData;
    }

    /**/
    public static void displayPktDetails(String rawHexData, String binaryPacket) {
        /**/

        // Dispalys the packets parts (Hex and Binary)
        System.out.println("======================= displayPktDetails()====================================");

        String trimdHexData = rawHexData.substring(1, 29);
        String DF_CA_Hex = trimdHexData.substring(0, 2);  // seperates the 8 bit DF and CA part of the packet
        String ICAO_Hex = trimdHexData.substring(2, 8);  // removes the  24 bit ICAO part of the data packet
        String Data_Hex = trimdHexData.substring(8, 22); //removes the 56 bit Data part of the data packet
        String PI_Hex = trimdHexData.substring(22);  // removes the Parity/Interrogator ID part of the packet
        int rawDataLength = rawHexData.length();  // find out the length of raw data packet
        int trimedDataLength = trimdHexData.length(); // find out the length of trimed data packet
        int binaryLength = binaryPacket.length();

        //display the data, trimed data, seperated fields, lengths and other messages
        System.out.println("\nraw Hex:\t" + rawHexData
                + "\ntrimmed Hex:\t " + trimdHexData
                + "\nbinary packet:\t " + binaryPacket
                + "\nDF and CA Hex:\t " + DF_CA_Hex
                + "\nICA0 Hex:\t " + ICAO_Hex
                + "\nData Hex:\t " + Data_Hex
                + "\nPI Hex:\t\t " + PI_Hex
                + "\n\nraw data length:\t" + rawDataLength
                + "\ntrimed data length:\t" + trimedDataLength
                + "\nbinary data length:\t" + binaryLength
                + "");

        String DF_Binary = binaryPacket.substring(0, 8);      // seperates the 8 bit DF and CA part of the packet
        String ICAO_Binary = binaryPacket.substring(8, 32);   // removes the  24 bit ICAO part of the data packet
        String Data_Binary = binaryPacket.substring(32, 88);  //removes the 56 bit Data part of the data packet
        String PI_Binary = binaryPacket.substring(88);        // removes the Parity/Interrogator ID part of the packet

        //Segments of the Data part:        
        String TC = binaryPacket.substring(32, 37);         // 5 bits ( Type Code )
        String SS = binaryPacket.substring(37, 39);         // 2 bits ( Surveillance Status )
        String NICsb = binaryPacket.substring(39, 40);      // 1 bit ( NIC Supplement-B )
        String Altitude = binaryPacket.substring(40, 52);   // 12 bits (Altitude)
        String T = binaryPacket.substring(52, 53);          // 1 bit (Time)
        String F = binaryPacket.substring(53, 54);          // 1 bit (odd/even frame flag - CPR)
        String Latitude = binaryPacket.substring(54, 71);   // 17 bits (Latitude - CPR format)
        String Longitude = binaryPacket.substring(71, 88);  // 17 bits (Longitude - CPR format) 

        // printing out frame parts (binary):
        System.out.println("\ndf binary:\t\t " + DF_Binary
                + "\nicao binary:\t\t " + ICAO_Binary
                + "\ndata binary:\t\t " + Data_Binary
                + "\nPI binary:\t\t " + PI_Binary
                + "\n============================================================================================\n\n");

        // Data part segments in binary:
        System.out.println("\nSegments of Data part:\n----------------------"
                + "\nTC:\t\t " + TC
                + "\nSS:\t\t " + SS
                + "\nNICsb:\t\t " + NICsb
                + "\nAltitude:\t " + Altitude
                + "\nT:\t\t " + T
                + "\nF:\t\t " + F
                + "\nLatitude:\t " + Latitude
                + "\nLongitude:\t " + Longitude
                + "\n----------------------------------\n\n");
    }

    /**/
    public static List<Integer> getPosition(List<String> evPkL, List<String> odPkL, List<String> evIcaoL, List<String> odIcaoL) {
        /**/

        // obtains the position of the the aircraft and returns it
        System.out.println("======================= getPosition()============================================");

        String binaryPacket = "";
        double position;
        String evenPK = "";
        String oddPK = "";
        String evenIcao = "";
        String oddIcao = "";

        List<Integer> positionList = new ArrayList<Integer>(); // position is an Integer
        //String tcBin = binaryPacket.substring(0, 5);           
        //int tc = Integer.parseInt(binaryPacket.substring(0, 5), 2);

        for (int i = 0; i < 100; i++) {
            for (int j = i + 1; j < 100; j++) {
                if (evIcaoL.get(i).equals(odIcaoL.get(j))) {
                    System.out.println("\ninside for loop 100\n");// for testing purposes

                    position = calculatePosition(evPkL.get(i), odPkL.get(j));
                    //positionList.add(position);
                    System.out.println(position);
                    break;

                } else {

                }
            }
        }
        return positionList;
    }

    /**/
    public static double calculatePosition(String evenPK, String oddPk) {
        /**/

        //extracts actual values need to calculate the position

        System.out.println("\n=============== calculatePosition() output ======================================\n\n");
        ADSB_DECODER decoderObject = new ADSB_DECODER();
        //binaryPacket.substring(40, 52);
        double position;
        String evenData = evenPK.substring(32, 88);
        String oddData = oddPk.substring(32, 88); // not complete
        int evenDataL = evenData.length();
        int oddDataL = oddData.length();

        String eveAlt = evenPK.substring(40, 52); // 0 -7 the altitude bits in the Data part of the packet
        String oddAlt = oddPk.substring(40, 52);
        int altLength = eveAlt.length();

        String eveLat = evenPK.substring(54, 71);
        String oddLat = oddPk.substring(54, 71);
        int latLength = eveLat.length();

        String eveLong = evenPK.substring(71, 88);
        String oddLong = oddPk.substring(71, 88);
        int longLength = eveLong.length();

        //---------------------//
        /*

        //Segments of the Data part:        
        String TC = binaryPacket.substring(32, 37);         // 5 bits ( Type Code )
        String SS = binaryPacket.substring(37, 39);         // 2 bits ( Surveillance Status )
        String NICsb = binaryPacket.substring(39, 40);      // 1 bit ( NIC Supplement-B )
        String Altitude = binaryPacket.substring(40, 52);   // 12 bits (Altitude)
        String T = binaryPacket.substring(52, 53);          // 1 bit (Time)
        String F = binaryPacket.substring(53, 54);          // 1 bit (odd/even frame flag - CPR)
        String Latitude = binaryPacket.substring(54, 71);   // 17 bits (Latitude - CPR format)
        String Longitude = binaryPacket.substring(71, 88);

        //--------------------//
         */
        System.out.println("\neven data length:\t" + evenDataL + "");
        System.out.println("\nodd data length:\t" + oddDataL + "\n");
        System.out.println("\naltitude length:\t" + altLength + "");
        System.out.println("\nlatitude length:\t" + latLength + "");
        System.out.println("\nlongitude length:\t" + longLength + "\n\n");

        position = decoderObject.getAltitude(eveAlt, oddAlt);
        position = decoderObject.getLatitude(eveLat, oddLat);
        position = decoderObject.getLongtitude(eveLong, oddLong);
        decoderObject.passData(); // passes the data to the TCP_SERVER_TESTING class

        return position; // this should be an integer array.
    }

    public static void forPractice(String evenPK, String oddPk) {
        /*
        
        this method is used for practice and has no part in the program.
        it is also used for Notes, calculating, position marking etc.
        
         */

        System.out.println("\n=============== forPractice() ======================================\n\n");
        // all bit counts start from 0 (not from 1).
        // segmentation of the Packet, with each part's length, start postion and end, and the start and end of sub strings:
        String Positions = "   0--df--7  8-----icao------------31   32----------------------Data--------------------------87  88---------PI------- 111";
        String binaryPacket = "10001101  010000000110001000011101   01011000110000111000011001000011010111001100010000010010  011010010010101011010110";
        String StartAndEnd = " 0======8  8=====================32   32====================================================88  88===================112";

        // segmentation of the Data part of the Packet:
        String Posit_Data = "                                         TC    ss  ni    Alt(12)     T    F       Lat-CPR(17)        Long-CPR(17)                             ";
        String PositionsD = "   0--df--7  8-----icao------------31   32-36  --  -   40--------51  -    -    54----Data-----70   71-------------87  88---------PI------- 111";
        String binaryPacketD = "10001101  010000000110001000011101   01011  00  0   110000111000  0    1    10010000110101110   01100010000010010  011010010010101011010110";
        String StartAndEndD = " 0======8  8=====================32   32===  ==  =   ============  =    =    =================   ===============88  88===================112";
        // for the Data Part:                                        0          7   8 --------19  20   21    22            38    39            55
        int position;
        String evenData = evenPK.substring(32, 88);
        String oddData = oddPk.substring(32, 88);
        int evenDataL = evenData.length();
        int oddDataL = oddData.length();

        String eveAlt = evenData.substring(8, 20);
        String oddAlt = oddData.substring(8, 20);
        int altLength = eveAlt.length();

        String eveLat = evenData.substring(22, 39);
        String oddLat = oddData.substring(22, 39);
        int latLength = eveLat.length();

        String eveLong = evenData.substring(39);
        String oddLong = oddData.substring(39);
        int longLength = eveLong.length();
        System.out.println("\neven data length:\t" + evenDataL + "\n\n");
        System.out.println("\nodd data length:\t" + oddDataL + "\n\n");
        System.out.println("\naltitude length:\t" + altLength + "\n");
        System.out.println("\nlatitude length:\t" + latLength + "\n");
        System.out.println("\nlongitude length:\t" + longLength + "\n\n");

    }

}// end class

