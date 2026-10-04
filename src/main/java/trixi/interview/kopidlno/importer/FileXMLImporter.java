package trixi.interview.kopidlno.importer;

import lombok.AllArgsConstructor;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@AllArgsConstructor
public class FileXMLImporter implements XMLImporter {
    protected String filePath;

    @Override
    public Document getParsedXML() {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        if (!filePath.endsWith(".xml")) {
            throw new IllegalArgumentException("File path must point to an XML file");
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(filePath);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException("Error parsing XML file: " + e.getMessage(), e);
        }
    }
}
