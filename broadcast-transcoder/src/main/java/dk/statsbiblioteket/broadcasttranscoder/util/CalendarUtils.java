package dk.statsbiblioteket.broadcasttranscoder.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/26/12
 * Time: 1:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class CalendarUtils {

    public static long getTimestamp(XMLGregorianCalendar cal) {
        return cal.toGregorianCalendar().getTimeInMillis();
    }

    public static XMLGregorianCalendar getCalendar() throws DatatypeConfigurationException {
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Copenhagen"), Locale.ROOT);
        XMLGregorianCalendar xmlcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        return xmlcal;
    }
}
