package dk.statsbiblioteket.broadcasttranscoder.util.persistence;

import dk.statsbiblioteket.broadcasttranscoder.cli.Context;
import dk.statsbiblioteket.broadcasttranscoder.domscontent.ProgramBroadcast;
import dk.statsbiblioteket.broadcasttranscoder.processors.TranscodeRequest;
import dk.statsbiblioteket.broadcasttranscoder.util.CalendarUtils;
import dk.statsbiblioteket.broadcasttranscoder.util.MetadataUtils;
import junit.framework.TestCase;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 12/17/12
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class BroadcastTranscodingRecordDAOTest extends TestCase {

    Context context = new Context();
    TranscodeRequest request = new TranscodeRequest();


    public void setUp() throws Exception {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        context.setHibernateConfigFile(new File(Thread.currentThread().getContextClassLoader().getResource("hibernate-derby.xml").toURI()));
        context.setProgrampid("uuid:test1");
        request.setTvmeter(false);
        request.setEndOffsetUsed(0);
        request.setStartOffsetUsed(10);
        request.setTitle("test transcoding");
        ProgramBroadcast programBroadcast = new ProgramBroadcast();
        programBroadcast.setChannelId("dr1");
        XMLGregorianCalendar startCal = CalendarUtils.getCalendar();
        XMLGregorianCalendar endCal = CalendarUtils.getCalendar();


        programBroadcast.setTimeStart(startCal);
        programBroadcast.setTimeStop(endCal);
        startCal.setHour(13);
        endCal.setHour(14);
        request.setProgramBroadcast(programBroadcast);

    }

    public void testSetTimestamp() throws Exception {
        HibernateUtil util = HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath());
        context.setTimestampPersister(new BroadcastTranscodingRecordDAO(util));
        TimestampPersister persister = context.getTimestampPersister();
        long timestamp = new Date().getTime();

        Long temp = persister.getTimestamp(context.getProgrampid());
        assertNull("Database is not empty",temp);

        persister.setTimestamp(context.getProgrampid(),timestamp);

        Long retrieved = persister.getTimestamp(context.getProgrampid());
        assertTrue("timestamps not equal after save",timestamp ==retrieved );

    }

    public void testSomthing(){


        HibernateUtil util = HibernateUtil.getInstance(context.getHibernateConfigFile().getAbsolutePath());
        BroadcastTranscodingRecordDAO dao = new BroadcastTranscodingRecordDAO(util);
        Long temp = dao.getTimestamp(context.getProgrampid());
        assertNull("Database is not empty",temp);



        dao.setTimestamp(context.getProgrampid(),new Date().getTime());
        BroadcastTranscodingRecord record = dao.read(context.getProgrampid());



        record.setTvmeter(request.isTvmeter());
        record.setBroadtcastStartTime(MetadataUtils.getProgramStart(request));
        record.setBroadcastEndTime(MetadataUtils.getProgramEnd(request));
        record.setChannel(request.getProgramBroadcast().getChannelId());
        record.setEndOffset(request.getEndOffsetUsed());
        record.setStartOffset(request.getStartOffsetUsed());
        record.setTitle(request.getTitle());
        dao.update(record);

        BroadcastTranscodingRecord record2 = dao.read(context.getProgrampid());

        System.out.println(record);
        System.out.println(record2);
        assertEquals("object changed by persistence",record,record2);


    }
}
