package trixi.interview.kopidlno.parser;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import trixi.interview.kopidlno.domain.Town;

import java.util.ArrayList;
import java.util.List;

@Component
public class TownXMLParser extends XMLParser<Town> {
    private static final String TOWN_ELEMENT = "vf:Obec";
    private static final String CODE_ELEMENT = "obi:Kod";
    private static final String NAME_ELEMENT = "obi:Nazev";

    @Override
    public List<Town> parse(Document xml) {
        List<Town> towns = new ArrayList<>();

        NodeList nodes = xml.getElementsByTagName(TOWN_ELEMENT);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            towns.add(parseOne(node));
        }

        return towns;
    }

    private Town parseOne(Node node) {
        Element element = (Element) node;
        Town town = new Town();

        String code = getValue(element, CODE_ELEMENT);
        town.setCode(Long.parseLong(code));

        town.setName(getValue(element, NAME_ELEMENT));

        return town;
    }
}
