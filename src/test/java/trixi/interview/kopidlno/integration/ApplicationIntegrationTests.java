package trixi.interview.kopidlno.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import trixi.interview.kopidlno.domain.Town;
import trixi.interview.kopidlno.domain.TownPart;
import trixi.interview.kopidlno.helpers.ZipWithXmlHelpers;
import trixi.interview.kopidlno.persistent.JPATownPartRepository;
import trixi.interview.kopidlno.persistent.JPATownRepository;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:kopidlno;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.town.data.url=https://example.test/towns.zip"
})
class ApplicationIntegrationTests {
    private static final String DATA_URL = "https://example.test/towns.zip";

    @Autowired
    private JPATownRepository townRepository;
    @Autowired
    private JPATownPartRepository townPartRepository;

    @Test
    void applicationImportsTownDataIntoDatabase() {
        Town town = townRepository.findByCode(123L);
        TownPart townPart = townPartRepository.findByCode(456L);

        assertThat(town).isNotNull();
        assertThat(town.getName()).isEqualTo("Sample town");
        assertThat(town.getId()).isNotNull();
        assertThat(townPart).isNotNull();
        assertThat(townPart.getName()).isEqualTo("Sample town part");
        assertThat(townPart.getId()).isNotNull();
        assertThat(townPart.getTown().getId()).isEqualTo(town.getId());
    }

    @TestConfiguration
    static class TestRestConfiguration extends ZipWithXmlHelpers {
        @Bean
        @Primary
        RestTemplate integrationRestTemplate() throws IOException {
            RestTemplate restTemplate = new RestTemplate();
            MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
            server.expect(requestTo(DATA_URL))
                    .andRespond(withSuccess(
                            new ByteArrayResource(zipWithXml("""
                                    <vf:Data xmlns:vf="urn:test:vf"
                                             xmlns:obi="urn:test:obi"
                                             xmlns:coi="urn:test:coi">
                                      <vf:Obce>
                                        <vf:Obec>
                                          <obi:Kod>123</obi:Kod>
                                          <obi:Nazev>Sample town</obi:Nazev>
                                        </vf:Obec>
                                      </vf:Obce>
                                      <vf:CastiObci>
                                        <vf:CastObce>
                                          <coi:Kod>456</coi:Kod>
                                          <coi:Nazev>Sample town part</coi:Nazev>
                                          <coi:Obec><obi:Kod>123</obi:Kod></coi:Obec>
                                        </vf:CastObce>
                                      </vf:CastiObci>
                                    </vf:Data>
                                    """)), MediaType.APPLICATION_OCTET_STREAM));
            return restTemplate;
        }

        private static byte[] zipWithXml(String content) throws IOException {
            return zipWithXml("towns.xml", content);
        }
    }
}
