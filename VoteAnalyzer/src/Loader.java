import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;


public class Loader
{
    private static final SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");
    private static final SimpleDateFormat visitDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    private static final HashMap<Integer, WorkTime> voteStationWorkTimes = new HashMap<>();
    private static final HashMap<Voter, Integer> voterCounts = new HashMap<>();
    private static final String fileName = "C:/Users/Uzer/OneDrive/Рабочий стол/VoteAnalyzer/VoteAnalyzer/";

    public static void main(String[] args) throws Exception
    {
        String string = "res/data-18M.xml";
        System.out.println ("Start time of work: " + LocalTime.now ().toString () );
        long startUp = System.currentTimeMillis();
        parseXMLHandler(fileName.concat ( string ));

        long startEnd = System.currentTimeMillis();
        System.out.println("Parsing duration: " + getTime( startUp, startEnd) + ".");
        DBConnection.printVoterCounts ();


    }

    private static void parseXMLHandler(String fileName) throws ParserConfigurationException, SAXException, IOException, SQLException {
                DBConnection.connection = DBConnection.getConnection();
        DBConnection.connection.setAutoCommit ( false );

        DBConnection.preparedStatement = DBConnection.getConnection().prepareStatement("INSERT INTO voter_count(name, birthDate) " + "VALUES (?, ?)");

        SAXParserFactory factoryToMySQL = SAXParserFactory.newInstance();
        SAXParser parserToMySQL = factoryToMySQL.newSAXParser();

        parserToMySQL.parse(new File(fileName), new XMLHandlerSQL());

    }

    public static void printWithDoc(String name) throws Exception {
        parseFile(name);

        System.out.println("Voting station work times: ");
        for(Integer votingStation : voteStationWorkTimes.keySet())
        {
            WorkTime workTime = voteStationWorkTimes.get(votingStation);
            System.out.println("\t" + votingStation + " - " + workTime);
        }

        System.out.println("Duplicated voters: ");
        for(Voter voter : voterCounts.keySet())
        {
            Integer count = voterCounts.get(voter);
            if(count > 1) {
                System.out.println("\t" + voter + " - " + count);
            }
        }
    }

    private static void parseFile(String fileName) throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(fileName));

        findEqualVoters(doc);
        //fixWorkTimes(doc);
    }

    private static void findEqualVoters(Document doc) throws Exception
    {
        NodeList voters = doc.getElementsByTagName("voter");
        int votersCount = voters.getLength();
        for(int i = 0; i < votersCount; i++)
        {
            Node node = voters.item(i);
            NamedNodeMap attributes = node.getAttributes();

            String name = attributes.getNamedItem("name").getNodeValue();

            String birthDay = attributes.getNamedItem("birthDay").getNodeValue();

            DBConnection.countVoter(name, birthDay);

        }

        DBConnection.executeMultiInsert();
    }

    private static void fixWorkTimes(Document doc) throws Exception
    {
        NodeList visits = doc.getElementsByTagName("visit");
        int visitCount = visits.getLength();
        for(int i = 0; i < visitCount; i++)
        {
            Node node = visits.item(i);
            NamedNodeMap attributes = node.getAttributes();

            Integer station = Integer.parseInt(attributes.getNamedItem("station").getNodeValue());
            Date time = visitDateFormat.parse(attributes.getNamedItem("time").getNodeValue());
            WorkTime workTime = voteStationWorkTimes.get(station);
            if(workTime == null)
            {
                workTime = new WorkTime();
                voteStationWorkTimes.put(station, workTime);
            }
            workTime.addVisitTime(time.getTime());
        }
    }

    public static double getTime(long start, long end){
        return (double) (end - start) / 1000 / 60;
    }
}