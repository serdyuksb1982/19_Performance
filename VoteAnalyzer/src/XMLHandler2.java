import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class XMLHandler2 extends Handler {
    int limit = 5_000_000;
    int number = 0;
    private Voter voter;
    private long memoryNext;
    private final List< String > voterCounts;
    public XMLHandler2() {
        voterCounts = new ArrayList ();
        voteStationWorkTimes = new HashMap<> ();
        visitDateFormat = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss" );
        birthDayFormat = new SimpleDateFormat ( "yyyy.MM.dd" );
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {

            if (qName.equals("voter") && number < limit){

                Date birthday = birthDayFormat.parse(attributes.getValue("birthDay"));
                voter = new Voter(attributes.getValue("name"), birthday);
                memoryNext = Math.max(memoryNext, (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
            }
            else if (qName.equals("visit") && voter != null)
            {
                voterCounts.add(voter.toString());
                Integer station = Integer.parseInt ( attributes.getValue ( "station" ) );
                Date time = visitDateFormat.parse ( attributes.getValue ( "time" ) );
                WorkTime workTime = voteStationWorkTimes.get ( station );
                if (workTime == null) {
                    workTime = new WorkTime ();
                    voteStationWorkTimes.put ( station, workTime );
                }
                workTime.addVisitTime ( time.getTime () );
                memoryNext = Math.max(memoryNext, (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        number++;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if((qName.equals ( "voter" ))) {
            voter = null;
        }
    }

    @Override
    public void printDuplicatedVoters() {
        System.out.println ( XMLHandler2.class.getName () + " - " + memoryNext + "." );
        System.out.println ( XMLHandler2.class.getName () + " - " + (Runtime.getRuntime ().totalMemory () - Runtime.getRuntime ().freeMemory () ));

        int counts = 0;

        Collections.sort ( voterCounts );
        String votersName = voterCounts.get ( 0 );
        for (String v : voterCounts) {
            if (v.equals ( votersName )) {
                counts++;
            } else {
                if (counts > 1) System.out.println ( votersName + " - " + counts );
                counts = 1;
                votersName = v;
            }
        }
        memoryNext = Math.max ( memoryNext, (Runtime.getRuntime ().totalMemory ()
                - Runtime.getRuntime ().freeMemory ()) );

    }

    @Override
    public void printStationWorkTimes() {
        System.out.println("Voting station work times: ");
        System.out.println ( XMLHandler2.class.getName () + " - " + memoryNext + "." );
        System.out.println ( XMLHandler2.class.getName () + " - " + (Runtime.getRuntime ().totalMemory () - Runtime.getRuntime ().freeMemory () ));
        for(Integer votingStation : voteStationWorkTimes.keySet())
        {
            WorkTime workTime = voteStationWorkTimes.get(votingStation);
            System.out.println("\t" + votingStation + " - " + workTime);
        }
    }

}
