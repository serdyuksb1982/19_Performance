import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class XMLHandler extends Handler {
    private Voter voter;
    private long memoryFirst;
    private final Map< Voter, Integer > voterCounts;
    public XMLHandler() {
        voterCounts = new HashMap<Voter, Integer> ();
        voteStationWorkTimes = new HashMap<> ();
        visitDateFormat = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss" );
        birthDayFormat = new SimpleDateFormat ( "yyyy.MM.dd" );
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        {

            try {
                if (qName.equals ( "voter" ) && voter == null) {
                    Date birthDay = birthDayFormat.parse ( attributes.getValue ( "birthDay" ) );
                    voter = new Voter ( attributes.getValue ( "name" ), birthDay );
                    memoryFirst = Math.max ( memoryFirst, (Runtime.getRuntime ().totalMemory () - Runtime.getRuntime ().freeMemory ()) );
                } else if (qName.equals ( "visit" ) && voter != null) {
                    int count = voterCounts.getOrDefault ( voter, 0 );
                    voterCounts.put ( voter, count + 1 );
                    Integer station = Integer.parseInt ( attributes.getValue ( "station" ) );
                    Date time = visitDateFormat.parse ( attributes.getValue ( "time" ) );
                    WorkTime workTime = voteStationWorkTimes.get ( station );
                    if (workTime == null) {
                        workTime = new WorkTime ();
                        voteStationWorkTimes.put ( station, workTime );
                    }
                    workTime.addVisitTime ( time.getTime () );
                    memoryFirst = Math.max ( memoryFirst, (Runtime.getRuntime ().totalMemory () - Runtime.getRuntime ().freeMemory ()) );
                }
            } catch (ParseException e) {
                e.printStackTrace ();
            }
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName.equals ( "voter" )) {
            voter = null;
            memoryFirst = Math.max(memoryFirst, (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        }
    }


    @Override
    public void printDuplicatedVoters() {
        System.out.println(XMLHandler.class.getName () + " - " + memoryFirst + ".");
        System.out.println(XMLHandler.class.getName () + " - " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        for (Voter voter : voterCounts.keySet ()) {
            int count = voterCounts.get ( voter );
            if(count > 1) {
                System.out.println (voter.toString () + " - " + count);
            }
        }

    }

    @Override
    public void printStationWorkTimes() {
        System.out.println("Voting station work times: ");
        System.out.println(XMLHandler.class.getName () + " - " + memoryFirst + ".");
        System.out.println(XMLHandler.class.getName () + " - " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())  + ".");
        for(Integer votingStation : voteStationWorkTimes.keySet())
        {
            WorkTime workTime = voteStationWorkTimes.get(votingStation);
            System.out.println("\t" + votingStation + " - " + workTime);
        }


    }

}
