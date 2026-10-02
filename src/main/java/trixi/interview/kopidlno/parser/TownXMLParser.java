package trixi.interview.kopidlno.parser;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import trixi.interview.kopidlno.domain.Town;

import java.util.ArrayList;
import java.util.List;

@Component
public class TownXMLParser implements XMLParser<Town> {
    private static final String TOWN_ELEMENT = "vf:Obec";
    private static final String CODE_ATTRIBUTE = "obi:Kod";
    private static final String NAME_ATTRIBUTE = "obi:Nazev";

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
        Town town = new Town();

        String code = node.getAttributes().getNamedItem(CODE_ATTRIBUTE).getNodeValue();
        town.setCode(Long.parseLong(code));

        town.setName(node.getAttributes().getNamedItem(NAME_ATTRIBUTE).getNodeValue());

        return town;
    }
}
