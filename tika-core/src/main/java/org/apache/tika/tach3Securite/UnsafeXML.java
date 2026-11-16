package org.apache.tika.tach3Securite;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class UnsafeXML {

    public Document readXML(InputStream input) throws Exception {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        return factory.newDocumentBuilder().parse(input);
    }
}