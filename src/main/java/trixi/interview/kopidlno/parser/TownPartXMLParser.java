package trixi.interview.kopidlno.parser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import trixi.interview.kopidlno.domain.Town;
import trixi.interview.kopidlno.domain.TownPart;
import trixi.interview.kopidlno.persistent.JPATownRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TownPartXMLParser implements XMLParser<TownPart> {
    private static final String TOWN_PART_ELEMENT = "vf:CastObce";
    private static final String CODE_ATTRIBUTE = "obi:Kod";
    private static final String NAME_ATTRIBUTE = "obi:Nazev";
    private static final String RELATED_TOWN_ATTRIBUTE = "coi:Obec";

    private Map<Long, Town> townsByCode = new HashMap<>();

    private final JPATownRepository townRepository;

    @Override
    public List<TownPart> parse(Document xml) {
        List<TownPart> townParts = new ArrayList<>();

        NodeList nodes = xml.getElementsByTagName(TOWN_PART_ELEMENT);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            townParts.add(parseOne(node));
        }

        townsByCode = new HashMap<>();
        return townParts;
    }

    private TownPart parseOne(Node node) {
        TownPart townPart = new TownPart();

        String code = node.getAttributes().getNamedItem(CODE_ATTRIBUTE).getNodeValue();
        townPart.setCode(Long.parseLong(code));

        townPart.setName(node.getAttributes().getNamedItem(NAME_ATTRIBUTE).getNodeValue());

        Node relatedTownNode = node.getAttributes().getNamedItem(RELATED_TOWN_ATTRIBUTE);
        Long relatedTownCode = Long.parseLong(
                relatedTownNode.getAttributes().getNamedItem(CODE_ATTRIBUTE).getNodeValue());
        townPart.setTown(getRelatedTown(relatedTownCode));

        return townPart;
    }

    private Town getRelatedTown(Long relatedTownCode) {
        Town relatedTown;
        if (townsByCode.containsKey(relatedTownCode)) {
            relatedTown = townsByCode.get(relatedTownCode);
        } else {
            relatedTown = townRepository.findByCode(relatedTownCode);

            if (relatedTown == null) {
                throw new IllegalStateException("Related town with code " + relatedTownCode + " not found in the database.");
            }

            townsByCode.put(relatedTownCode, relatedTown);
        }

        return relatedTown;
    }
}
