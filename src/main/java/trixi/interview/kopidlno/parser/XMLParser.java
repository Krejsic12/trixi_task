package trixi.interview.kopidlno.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;

public abstract class XMLParser<T> {
    abstract List<T> parse(Document xml);

    protected String getValue(Element element, String fieldName) {
        NodeList children = element.getElementsByTagName(fieldName);
        if (children.getLength() > 0) {
            return children.item(0).getTextContent();
        }

        throw new IllegalArgumentException("Missing " + fieldName + " in " + element.getTagName());
    }
}
