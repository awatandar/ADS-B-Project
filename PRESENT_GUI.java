/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adsb_project;

/**
 *
 * @author watandar
 */
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

public class PRESENT_GUI extends JFrame implements ActionListener {
    
        JPanel panel;
        
        private double alt;
        private  double lat;
        private double longt;
        private String airID;

    public PRESENT_GUI(double alt, double lat, double lngt, String id) {
        
        
        super("Add component on JFrame at runtime");
        this.alt = alt;
        this.lat = lat;
        this.longt = lngt;
        this.airID = id;
        
        setLayout(new BorderLayout());
        this.panel = new JPanel();
        this.panel.setLayout(new FlowLayout());
        add(panel, BorderLayout.CENTER);
        JButton button = new JButton("CLICK HERE");
        add(button, BorderLayout.SOUTH);
        button.addActionListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent evt) {
        /*
        this.panel.add(new JButton("Button"));
        this.panel.revalidate();
        validate();
        */
                JFrame myFrame = new JFrame("Test GUI");
        myFrame.setVisible(true);
        myFrame.setBounds(300, 300, 500, 300);
        JLabel myText = new JLabel("Altitude:\t   "+alt+
                "        \nLatitude:\t     "+lat+
                "         \nLongitude:\t   "+longt,
                SwingConstants.CENTER);
      
        myFrame.getContentPane().add(myText, BorderLayout.CENTER);
    }
/*
    public static void main(String[] args) {
        PRESENT_GUI acojfar = new PRESENT_GUI();
    }

*/
    
}
