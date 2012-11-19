package dk.statsbiblioteket.broadcasttranscoder.util;

import javax.xml.datatype.XMLGregorianCalendar;

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
}
