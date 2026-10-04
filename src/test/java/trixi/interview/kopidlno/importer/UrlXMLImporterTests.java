package trixi.interview.kopidlno.importer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlXMLImporterTests {
    private static final String URL = "https://example.com/data.zip";

    @Mock
    private RestTemplate restTemplate;

    @Test
    void getParsedXML_downloadsAndParsesXMLFromZip() throws IOException {
        when(restTemplate.getForObject(URL, byte[].class))
                .thenReturn(zipWithEntry("data.xml", "<root><value>ok</value></root>"));
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
                .thenReturn(zipWithEntry("data.txt", "not XML"));
        UrlXMLImporter importer = new UrlXMLImporter(URL, restTemplate);

        assertThatThrownBy(importer::getParsedXML)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Expected exactly one XML file in ZIP archive, found 0");
    }

    private byte[] zipWithEntry(String name, String content) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(output)) {
            zip.putNextEntry(new ZipEntry(name));
            zip.write(content.getBytes());
            zip.closeEntry();
        }
        return output.toByteArray();
    }
}
