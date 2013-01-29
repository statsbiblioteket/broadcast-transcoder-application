package dk.statsbiblioteket.broadcasttranscoder.mock;

import dk.statsbiblioteket.doms.central.*;

import javax.jws.WebParam;
import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 11/19/12
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class DomsMockApi implements CentralWebservice{

    private Map<String,Map<String,String>> objects = new HashMap<String, Map<String, String>>();

    @Override
    public String newObject(@WebParam(name = "pid", targetNamespace = "") String s, @WebParam(name = "oldID", targetNamespace = "") List<String> strings, @WebParam(name = "comment", targetNamespace = "") String s1) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        String pid = "uuid:" + UUID.randomUUID().toString();
        if (objects.containsKey(pid)){
            return pid;
        }
        objects.put(pid,new HashMap<String, String>());
        return pid;
    }

    @Override
    public ObjectProfile getObjectProfile(@WebParam(name = "pid", targetNamespace = "") String s) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setObjectLabel(@WebParam(name = "pid", targetNamespace = "") String s, @WebParam(name = "name", targetNamespace = "") String s1, @WebParam(name = "comment", targetNamespace = "") String s2) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteObject(@WebParam(name = "pids", targetNamespace = "") List<String> strings, @WebParam(name = "comment", targetNamespace = "") String s) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void markPublishedObject(@WebParam(name = "pids", targetNamespace = "") List<String> strings, @WebParam(name = "comment", targetNamespace = "") String s) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void markInProgressObject(@WebParam(name = "pids", targetNamespace = "") List<String> strings, @WebParam(name = "comment", targetNamespace = "") String s) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void modifyDatastream(@WebParam(name = "pid", targetNamespace = "") String s, @WebParam(name = "datastream", targetNamespace = "") String s1, @WebParam(name = "contents", targetNamespace = "") String s2, @WebParam(name = "comment", targetNamespace = "") String s3) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        Map<String, String> object = objects.get(s);
        if (object == null){
            throw new InvalidResourceException("dfsd","sdfds");
        }
        object.put(s1,s2);
    }

    @Override
    public String getDatastreamContents(@WebParam(name = "pid", targetNamespace = "") String s, @WebParam(name = "datastream", targetNamespace = "") String s1) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        Map<String, String> object = objects.get(s);
        if (object == null){
            throw new InvalidResourceException("dfsd","sdfds");
        }
        String contents = object.get(s1);
        if (contents == null){
            throw new InvalidResourceException("sdfd","sdfsd");
        }
        return contents;
    }

    @Override
    public void addFileFromPermanentURL(@WebParam(name = "pid", targetNamespace = "") String s, @WebParam(name = "filename", targetNamespace = "") String s1, @WebParam(name = "md5sum", targetNamespace = "") String s2, @WebParam(name = "permanentURL", targetNamespace = "") String s3, @WebParam(name = "formatURI", targetNamespace = "") String s4, @WebParam(name = "comment", targetNamespace = "") String s5) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getFileObjectWithURL(@WebParam(name = "URL", targetNamespace = "") String s) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addRelation(@WebParam(name = "pid", targetNamespace = "") String s, @WebParam(name = "relation", targetNamespace = "") Relation relation, @WebParam(name = "comment", targetNamespace = "") String s1) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Relation> getRelations(@WebParam(name = "pid", targetNamespace = "") String s) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Relation> getNamedRelations(@WebParam(name = "pid", targetNamespace = "") String s, @WebParam(name = "predicate", targetNamespace = "") String s1) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Relation> getInverseRelations(@WebParam(name = "pid", targetNamespace = "") String s) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteRelation(@WebParam(name = "pid", targetNamespace = "") String s, @WebParam(name = "relation", targetNamespace = "") Relation relation, @WebParam(name = "comment", targetNamespace = "") String s1) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ViewBundle getViewBundle(@WebParam(name = "pid", targetNamespace = "") String s, @WebParam(name = "ViewAngle", targetNamespace = "") String s1) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<RecordDescription> getIDsModified(@WebParam(name = "since", targetNamespace = "") long l, @WebParam(name = "collectionPid", targetNamespace = "") String s, @WebParam(name = "viewAngle", targetNamespace = "") String s1, @WebParam(name = "state", targetNamespace = "") String s2, @WebParam(name = "offset", targetNamespace = "") Integer integer, @WebParam(name = "limit", targetNamespace = "") Integer integer1) throws InvalidCredentialsException, MethodFailedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getLatestModified(@WebParam(name = "collectionPid", targetNamespace = "") String s, @WebParam(name = "viewAngle", targetNamespace = "") String s1, @WebParam(name = "state", targetNamespace = "") String s2) throws InvalidCredentialsException, MethodFailedException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> findObjectFromDCIdentifier(@WebParam(name = "string", targetNamespace = "") String s) throws InvalidCredentialsException, MethodFailedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<SearchResult> findObjects(@WebParam(name = "query", targetNamespace = "") String s, @WebParam(name = "offset", targetNamespace = "") int i, @WebParam(name = "pageSize", targetNamespace = "") int i1) throws InvalidCredentialsException, MethodFailedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void lockForWriting() throws InvalidCredentialsException, MethodFailedException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unlockForWriting() throws InvalidCredentialsException, MethodFailedException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public User createTempAdminUser(@WebParam(name = "username", targetNamespace = "") String s, @WebParam(name = "roles", targetNamespace = "") List<String> strings) throws InvalidCredentialsException, MethodFailedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getObjectsInCollection(@WebParam(name = "collectionPid", targetNamespace = "") String s, @WebParam(name = "contentModelPid", targetNamespace = "") String s1) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void createObject(String pid) {
        if (objects.containsKey(pid)){
            return;
        }
        objects.put(pid,new HashMap<String, String>());
    }
}
