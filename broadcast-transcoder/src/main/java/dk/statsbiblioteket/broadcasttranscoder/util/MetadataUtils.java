/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.broadcasttranscoder.util;

import dk.statsbiblioteket.broadcasttranscoder.cli.SingleTranscodingContext;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;

import java.util.Date;

public class MetadataUtils {

    private MetadataUtils(){}

    public static long findProgramLengthMillis(TranscodeRequest request) {
        return CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStop())
                            - CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStart());
    }

    public static Date getProgramStart(TranscodeRequest request) {
        return request.getProgramBroadcast().getTimeStart().toGregorianCalendar().getTime();
    }

    public static Date getProgramEnd(TranscodeRequest request) {
        return request.getProgramBroadcast().getTimeStop().toGregorianCalendar().getTime();
    }

    public static long findTotalLengthMillis(TranscodeRequest request) {
         return findProgramLengthMillis(request) - request.getStartOffsetUsed()*1000L + request.getEndOffsetUsed()*1000L;
    }

    public static long getTimeout(TranscodeRequest request, SingleTranscodingContext context) {
        long timeout;
        if (request.getTimeoutMilliseconds() == 0l) {
            long totalLengthMillis = findTotalLengthMillis(request);
            timeout = (long) (totalLengthMillis/context.getTranscodingTimeoutDivisor());
        } else {
            timeout = request.getTimeoutMilliseconds();
        }
        return timeout;
    }
}
