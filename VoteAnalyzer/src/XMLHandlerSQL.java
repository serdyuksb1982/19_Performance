import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XMLHandlerSQL extends Handler {
    private  Voter voter;
    private static final SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");
    private int countVoters = 0;
    private int batchSize;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        int maxBatchSize = 5_000_000;
        if (qName.equals("voter") && batchSize < maxBatchSize){
            Date birthday = null;
            try {
                birthday = birthDayFormat.parse(attributes.getValue("birthDay"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            voter = new Voter(attributes.getValue("name"), birthday);


        }
        else if (qName.equals("visit") && voter != null)
        {

            try {
                DBConnection.preparedStatement.setString(1,  voter.getName());
                DBConnection.preparedStatement.setString(2,  voter.getBirthDayString());
                DBConnection.preparedStatement.addBatch();
                countVoters++;

                if (countVoters > 10000) {
                    DBConnection.preparedStatement.executeBatch();
                    DBConnection.connection.commit();
                    countVoters = 0;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            batchSize++;

        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName.equals("voter"))
        {
            voter = null;
        }
    }

    @Override
    public void printDuplicatedVoters() throws SQLException {

        DBConnection.preparedStatement.executeBatch();
        DBConnection.connection.commit();

        DBConnection.printVoterCounts();
    }

    @Override
    public void printStationWorkTimes() {

    }
}
