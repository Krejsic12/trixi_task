package trixi.interview.kopidlno.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import trixi.interview.kopidlno.domain.Town;
import trixi.interview.kopidlno.domain.TownPart;
import trixi.interview.kopidlno.importer.UrlXMLImporter;
import trixi.interview.kopidlno.importer.XMLImporter;
import trixi.interview.kopidlno.parser.TownPartXMLParser;
import trixi.interview.kopidlno.parser.TownXMLParser;
import trixi.interview.kopidlno.persistent.JPATownPartRepository;
import trixi.interview.kopidlno.persistent.JPATownRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SaveTownDataFacade {
    @Value("${app.town.data.url}")
    private String townDataUrl;

    private final RestTemplate restTemplate;

    private final JPATownRepository townRepo;
    private final JPATownPartRepository townPartRepo;

    private final TownXMLParser townParser;
    private final TownPartXMLParser townPartParser;

    public void saveTownData() {
        XMLImporter importer = new UrlXMLImporter(townDataUrl, restTemplate);
        Document xmlDocument = importer.getParsedXML();

        List<Town> towns = townParser.parse(xmlDocument);
        towns.forEach(town -> {
            Town existingTown = townRepo.findByCode(town.getCode());
            if (existingTown != null) {
                town.setId(existingTown.getId());
                town.setTownParts(existingTown.getTownParts());
            }
            townRepo.save(town);
        });

        List<TownPart> townParts = townPartParser.parse(xmlDocument);
        townParts.forEach(townPart -> {
            TownPart existingTownPart = townPartRepo.findByCode(townPart.getCode());
            if (existingTownPart != null) {
                townPart.setId(existingTownPart.getId());
            }
            townPartRepo.save(townPart);
        });
    }
}
