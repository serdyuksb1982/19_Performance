import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;


public abstract class  Handler extends DefaultHandler
{
    protected SimpleDateFormat birthDayFormat;
    protected SimpleDateFormat visitDateFormat;

    protected HashMap<Integer, WorkTime> voteStationWorkTimes;

    public Handler() {};

    @Override
    public void startDocument() throws SAXException {

    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

    }

    @Override
    public abstract void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException;

    @Override
    public abstract void endElement(String uri, String localName, String qName) throws SAXException;

    public abstract void printDuplicatedVoters() throws SQLException;

    public abstract void printStationWorkTimes();
}
