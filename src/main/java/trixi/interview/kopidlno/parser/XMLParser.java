package trixi.interview.kopidlno.parser;

import org.w3c.dom.Document;

import java.util.List;

public interface XMLParser<T> {
    List<T> parse(Document xml);
}
