/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.broadcasttranscoder.util;

import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;

public class MetadataUtils {

    private MetadataUtils(){}

    public static long findProgramLengthMillis(TranscodeRequest request) {
        return CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStop())
                            - CalendarUtils.getTimestamp(request.getProgramBroadcast().getTimeStart());
    }


}
