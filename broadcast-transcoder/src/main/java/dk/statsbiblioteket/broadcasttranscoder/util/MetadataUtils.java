/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.broadcasttranscoder.util;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
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


    public static long getTimeout(TranscodeRequest request, Context context) {
        long timeout;
        if (request.getTimeoutMilliseconds() == 0l) {
            long programLength = findProgramLengthMillis(request);
            timeout = programLength/context.getTranscodingTimeoutDivisor();
        } else {
            timeout = request.getTimeoutMilliseconds();
        }
        return timeout;
    }
}
