package dk.statsbiblioteket.broadcasttranscoder.util;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

/**
 * Created with IntelliJ IDEA.
 * User: csr
 * Date: 9/24/12
 * Time: 1:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class JaxbWrapper<T> {
    private JAXBContext jaxbContext = null;

       private Schema schema;

       /**
        * @param schemaUrl
        * @param clazz
        * @throws JAXBException
        * @throws SAXException
        */
       public JaxbWrapper(URL schemaUrl, Class<?>... clazz) throws JAXBException, SAXException {
           SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
           this.schema = schemaFactory.newSchema(schemaUrl);
           this.jaxbContext = JAXBContext.newInstance(clazz);
       }

       /**
        * @param schemaFile
        * @param clazz
        * @throws JAXBException
        * @throws SAXException
        */
       public JaxbWrapper(File schemaFile, Class<?>... clazz) throws JAXBException, SAXException {
           SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
           this.schema = schemaFactory.newSchema(schemaFile);
           this.jaxbContext = JAXBContext.newInstance(clazz);
       }

       /**
        * Converts the given object to XML
        *
        * @param t
        * @return
        * @throws JAXBException
        *
        * @author Vineet Manohar
        */
       public String objectToXml(T t) throws JAXBException {
           StringWriter writer = new StringWriter();
           objectToXml(t, writer);
           return writer.toString();
       }

       /**
        * Converts the given object to XML
        *
        * @param t
        *            the object to convert to xml
        * @param writer
        *            the output will be written to this writer
        *
        * @throws JAXBException
        *
        * @author Vineet Manohar
        */
       public void objectToXml(T t, Writer writer) throws JAXBException {
           createMarshaller().marshal(t, writer);
       }

       /**
        * Converts the given object to XML
        *
        * @param t
        *            the object to convert to xml
        * @param outputStream
        *            the output will be written to this output stream
        *
        * @throws JAXBException
        *
        * @author Vineet Manohar
        */
       public void objectToXml(T t, OutputStream outputStream) throws JAXBException {
           createMarshaller().marshal(t, outputStream);
       }

       /**
        * Converts the given object to XML
        *
        * @param t
        *            the object to convert to xml
        * @param file
        *            the output will be written to this file
        *
        * @throws JAXBException
        *
        * @author Vineet Manohar
        */
       public void objectToXml(T t, File file) throws JAXBException {
           createMarshaller().marshal(t, file);
       }

       /**
        * validates the object against the schema, throws an Exception if
        * validation fails
        *
        * @param t
        *            the object to validate
        * @throws JAXBException
        *             when schema validation fails
        *
        * @author Vineet Manohar
        */
       public void validate(T t) throws JAXBException {
           createMarshaller().marshal(t, new StringWriter());
       }

       /**
        * converts xml form to a java object
        *
        * @param is
        *            InputStream which points to a valid XML content
        * @return the Java object representing the xml
        * @throws JAXBException
        *             if jaxb unmarshalling fails. Common reason is schema
        *             incompatibility
        *
        * @author Vineet Manohar
        */
       @SuppressWarnings("unchecked")
       public T xmlToObject(InputStream is) throws JAXBException {
           return (T) createUnmarshaller().unmarshal(is);
       }

       /**
        * converts xml form to a java object
        *
        * @param xml
        *            a valid XML string
        * @return the Java object representing the xml
        * @throws JAXBException
        *             if jaxb unmarshalling fails. Common reason is schema
        *             incompatibility
        *
        * @author Vineet Manohar
        */
       @SuppressWarnings("unchecked")
       public T xmlToObject(String xml) throws JAXBException {
           StringReader stringReader = new StringReader(xml);
           return (T) createUnmarshaller().unmarshal(stringReader);
       }

       /**
        * Creates a marshaller. Clients generally don't need to call this method,
        * hence the method is protected. If you want use features not already
        * exposed, you can subclass and call this method.
        *
        * @return
        * @throws JAXBException
        * @throws PropertyException
        *
        * @author Vineet Manohar
        */
       protected Marshaller createMarshaller() throws JAXBException, PropertyException {
           Marshaller marshaller = jaxbContext.createMarshaller();
           marshaller.setSchema(schema);
           marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
           return marshaller;
       }

       /**
        * Creates a marshaller. Clients generally don't need to call this method,
        * hence the method is protected. If you want use features not already
        * exposed, you can subclass and call this method.
        *
        * @return
        * @throws JAXBException
        *
        * @author Vineet Manohar
        */
       protected Unmarshaller createUnmarshaller() throws JAXBException {
           Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
           unmarshaller.setSchema(schema);
           return unmarshaller;
       }

}
