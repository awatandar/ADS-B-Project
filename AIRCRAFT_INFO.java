/* this class analyses the decoded data passed to it from the ADSB_DECODER,
    analyses it and pass it to be presented.

    22/02/2017 (abdullah watandar)
    Last Updated: 14/03/2017
*/
package adsb_project;

import java.io.File;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author c09784420
 */
public class AIRCRAFT_INFO {

    private String evenPacket;
    private String oddPacket;
    private int latitude ;
    private int longitude;
    private int altitude;
    private String sign;
    
    private static String charLookUp = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ     _               0123456789      ";
    public AIRCRAFT_INFO(){
  
     //this.evenPacket = evenPacket;
     //this.oddPacket = oddPacket;
              
    }
    
     public String getIcaoAddress(String rawData){

        String icaoString = ""; 
    
        
        return icaoString; 
    }
//--------------------------------------------------------------
     
    public String getPosition( String rawPData, String sign ){
    
        int count = 0;
        String position = "empty";

        return position;             
    }
   //-----------------------------------------------------------------
    public String getDirection(String rawData){
        
        String direction = "";
     
        return direction; 
    }

    public int getAltitude(String binData){
        
        int altitude = 0;
        
        return altitude; 
   
    } 
   
    public String getID(String pID, String pCatagory){
   
        String charMap = charLookUp;// = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ     _               0123456789      ";// the empty spaces are representating nothing
        String PlaneID = pID; // contains id of the plain in binary
        String ecString = pCatagory.substring(5);// get EC out from the first data byte to determine catagory
        int EC_Catagory = Integer.parseInt(ecString, 2); // if catagory = 0, then no catagory info is available.
       
        
        int idLength = PlaneID.length();
        int j1 = 0; // used to mark the start of each 6 bit bunch of binary numbers
        int j2 = 6;// used to mark the end ...
        int[] idCode = new int[8]; // store the decimal code
        String idString = ""; // store the chars looked up
        
        for (int i = 0; i<8; i++ ){ 
            // chop up the String, convert to decimal, to char, put in string and print out:
            String id = PlaneID.substring(j1, j2);
            int idDecCode = Integer.parseInt(id, 2);
            idCode[i] = idDecCode;
            char mychar =charMap.charAt(idDecCode);
            idString = idString+mychar;
            j1 = j2;
            j2 = j2+6;
            System.out.println("\n"+id+"\t"+idDecCode+"\t"+mychar);
            //System.out.print(mychar);
        }
       //System.out.println("\n aircraft ID:\t"+idString+"");
        System.out.print("\n ------------------ aircraft_info class ----------------- :\n\n");
        for (int i=0; i<8; i++){


	//char mychar =s.charAt(i); // get the character at index i

	//System.out.print("  "+idCode[i]);

	}
        // how to map code to chars
	int sl = charLookUp.length();
	int j = 1;
	System.out.println("\naircraft id chars:\t "+PlaneID+"\nlength of chars:\t "+idLength+"\n\nPlane catagory:\t\t "+EC_Catagory+"\n");

	//System.out.print("map numbers to chars:\t ");

	for (int i=0; i<=63; i++){


	//char mychar =s.charAt(i); // get the character at index i

	//System.out.print(""+mychar);

	}

	//System.out.println("");
        
        return idString;
    }
}
