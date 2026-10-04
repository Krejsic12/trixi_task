package trixi.interview.kopidlno.importer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import trixi.interview.kopidlno.helpers.ZipWithXmlHelpers;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlXMLImporterTests extends ZipWithXmlHelpers {
    private static final String URL = "https://example.com/data.zip";

    @Mock
    private RestTemplate restTemplate;

    @Test
    void getParsedXML_downloadsAndParsesXMLFromZip() throws IOException {
        when(restTemplate.getForObject(URL, byte[].class))
                .thenReturn(zipWithXml("data.xml", "<root><value>ok</value></root>"));
        UrlXMLImporter importer = new UrlXMLImporter(URL, restTemplate);

        Document document = importer.getParsedXML();

        assertThat(document.getDocumentElement().getTagName()).isEqualTo("root");
        assertThat(document.getElementsByTagName("value").item(0).getTextContent()).isEqualTo("ok");
        verify(restTemplate).getForObject(URL, byte[].class);
    }

    @Test
    void getParsedXML_rejectsEmptyURL() {
        UrlXMLImporter importer = new UrlXMLImporter("", restTemplate);

        assertThatThrownBy(importer::getParsedXML)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("URL cannot be null or empty");
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getParsedXML_rejectsNullDownloadedContent() {
        when(restTemplate.getForObject(URL, byte[].class)).thenReturn(null);
        UrlXMLImporter importer = new UrlXMLImporter(URL, restTemplate);

        assertThatThrownBy(importer::getParsedXML)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No ZIP content returned from URL: " + URL);
    }

    @Test
    void getParsedXML_rejectsZipWithoutXMLFile() throws IOException {
        when(restTemplate.getForObject(URL, byte[].class))
                .thenReturn(zipWithXml("data.txt", "not XML"));
        UrlXMLImporter importer = new UrlXMLImporter(URL, restTemplate);

        assertThatThrownBy(importer::getParsedXML)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Expected exactly one XML file in ZIP archive, found 0");
    }
}
