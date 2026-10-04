package trixi.interview.kopidlno.parser;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

public class XMLParserHelpers {
    protected Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        InputSource inputSource = new InputSource(new StringReader(xml));
        return factory.newDocumentBuilder().parse(inputSource);
    }
}
