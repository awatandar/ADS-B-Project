/*
this server server will be used to transmit the final data to TCP clients.
sending the final data this way will establish necessary control over who access the data,
and makes the data useful to more clients/interested parties.

updated: 6/4/2017
 */
package adsb_project;

/*
 * @author abdul
 */
import java.io.*;
import java.net.*;
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

public class TCP_SERVER_TESTING {

    // instance variables declared
    private String aircraftID;
    private String icaoAddress;
    private double position;
    private double altitude;
    private double latitude;
    private double longitude;

    private List<Double> myItems;
    private int number;
    private int count;

    public TCP_SERVER_TESTING(List<Double> list) throws Exception {

        /*  // parameter values recieved are assigned to instance variables
        this.altitude = altd;
        this.latitude = latd;
        this.longitude = longtd;
        this.icaoAddress = icao;
         */
        this.myItems = list;

        System.out.println("\n\n=======================TCP_SERVER_TESTING()=======================\n\nItems in the list recieved:\n");

        for (int i = 0; i < 3; i++) {
            System.out.println("Aircraft Details:\n" + myItems.get(i));
        }

        // begining of try-catch block    
        try {

            //create a new serversocket object with port no 6789
            ServerSocket welcomeSocket = new ServerSocket(6789);

            //while loop
            while (true) {
                //creates a new socket object. accepts the connection, and waits for any connection from client
                Socket connectionSocket = welcomeSocket.accept();// Makes a new thread after the connection is accepted

                //displays confirmation of connection
                System.out.println("Connection accepted!");

                // Recovers the fileName from client
                String myFileToBeSent = aircraftID; // this can be an object or a List etc.

                // get the byte array of the file (object most probably)
                File myFile = new File(myFileToBeSent);
                byte[] byteArray = new byte[(int) myFile.length()];
                FileInputStream fInputStream = new FileInputStream(myFile);
                BufferedInputStream buffInputStream = new BufferedInputStream(fInputStream);
                buffInputStream.read(byteArray, 0, byteArray.length);

                //display messages to the users
                System.out.println("I am sending data.");

                //declare new output strea object
                OutputStream outptStream = connectionSocket.getOutputStream();
                outptStream.write(byteArray, 0, byteArray.length);
                outptStream.flush();

                //close the connection and let the user know.
                connectionSocket.close();
                System.out.println("File was successfully sent!");

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
