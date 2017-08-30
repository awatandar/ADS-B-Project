/* this class analyses the decoded data passed to it from the ADSB_DECODER,
    analyses it and pass it to be presented.

    13/03/2017 (abdullah watandar)
    Last Updated: 14/03/2017
*/
package adsb_project;

/**
 *
 * @author c09784420
 */
public class ADSB_ANALYSIS {
    
    private String Position;
    private String Direction;
    private String ICAO;
    private String Other;
    private int Number;
    private int count;
    
    
    public ADSB_ANALYSIS(String position,String direction,String icao){
  
     this.Position = position;
     this.Direction = direction;
     this.ICAO = icao;
    
              
    }
    
    public int countPlanes(){
        int i = count;
        return i;
    }
    
}
