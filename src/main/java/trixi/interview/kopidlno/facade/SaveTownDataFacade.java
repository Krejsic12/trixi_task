package trixi.interview.kopidlno.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import trixi.interview.kopidlno.importer.UrlXMLImporter;
import trixi.interview.kopidlno.importer.XMLImporter;
import trixi.interview.kopidlno.parser.TownPartXMLParser;
import trixi.interview.kopidlno.parser.TownXMLParser;
import trixi.interview.kopidlno.persistent.JPATownPartRepository;
import trixi.interview.kopidlno.persistent.JPATownRepository;

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

        townRepo.saveAll(townParser.parse(xmlDocument));
        townPartRepo.saveAll(townPartParser.parse(xmlDocument));
    }
}
