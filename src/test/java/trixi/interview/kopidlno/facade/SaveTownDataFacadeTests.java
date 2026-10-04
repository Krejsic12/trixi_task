package trixi.interview.kopidlno.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import trixi.interview.kopidlno.domain.Town;
import trixi.interview.kopidlno.domain.TownPart;
import trixi.interview.kopidlno.helpers.ZipWithXmlHelpers;
import trixi.interview.kopidlno.parser.TownPartXMLParser;
import trixi.interview.kopidlno.parser.TownXMLParser;
import trixi.interview.kopidlno.persistent.JPATownPartRepository;
import trixi.interview.kopidlno.persistent.JPATownRepository;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveTownDataFacadeTests extends ZipWithXmlHelpers {
    private static final String URL = "https://example.com/town-data.zip";

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private JPATownRepository townRepo;
    @Mock
    private JPATownPartRepository townPartRepo;
    @Mock
    private TownXMLParser townParser;
    @Mock
    private TownPartXMLParser townPartParser;

    @Test
    void saveTownData_existingTownInDatabase() throws IOException {
        Town existingTown = new Town(10L, 123L, "Old name", List.of(new TownPart()));
        Town parsedTown = new Town(null, 123L, "New name", null);
        TownPart existingTownPart = new TownPart(20L, 456L, "Old part name", existingTown);
        TownPart parsedTownPart = new TownPart(null, 456L, "New part name", existingTown);
        when(restTemplate.getForObject(URL, byte[].class))
                .thenReturn(zipWithXml("<root/>"));
        when(townParser.parse(org.mockito.ArgumentMatchers.any())).thenReturn(List.of(parsedTown));
        when(townPartParser.parse(org.mockito.ArgumentMatchers.any())).thenReturn(List.of(parsedTownPart));
        when(townRepo.findByCode(123L)).thenReturn(existingTown);
        when(townPartRepo.findByCode(456L)).thenReturn(existingTownPart);

        SaveTownDataFacade facade = new SaveTownDataFacade(
                restTemplate, townRepo, townPartRepo, townParser, townPartParser);
        ReflectionTestUtils.setField(facade, "townDataUrl", URL);

        facade.saveTownData();

        assertThat(parsedTown.getId()).isEqualTo(10L);
        assertThat(parsedTown.getTownParts()).isSameAs(existingTown.getTownParts());
        assertThat(parsedTownPart.getId()).isEqualTo(20L);
        assertThat(parsedTownPart.getTown()).isSameAs(existingTownPart.getTown());
        verify(townRepo).save(parsedTown);
        verify(townPartRepo).save(parsedTownPart);
    }

    @Test
    void saveTownData_townNotInDatabase() throws IOException {
        Town parsedTown = new Town(null, 123L, "New town", null);
        TownPart parsedTownPart = new TownPart(null, 456L, "New town part", parsedTown);
        when(restTemplate.getForObject(URL, byte[].class))
                .thenReturn(zipWithXml("<root/>"));
        when(townParser.parse(org.mockito.ArgumentMatchers.any())).thenReturn(List.of(parsedTown));
        when(townPartParser.parse(org.mockito.ArgumentMatchers.any())).thenReturn(List.of(parsedTownPart));
        when(townRepo.findByCode(123L)).thenReturn(null);
        when(townPartRepo.findByCode(456L)).thenReturn(null);

        SaveTownDataFacade facade = new SaveTownDataFacade(
                restTemplate, townRepo, townPartRepo, townParser, townPartParser);
        ReflectionTestUtils.setField(facade, "townDataUrl", URL);

        facade.saveTownData();

        assertThat(parsedTown.getId()).isNull();
        assertThat(parsedTownPart.getId()).isNull();
        verify(townRepo).save(parsedTown);
        verify(townPartRepo).save(parsedTownPart);
    }

    private byte[] zipWithXml(String content) throws IOException {
        return zipWithXml("towns.xml", content);
    }
}
