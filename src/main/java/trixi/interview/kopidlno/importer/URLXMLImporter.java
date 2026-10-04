package trixi.interview.kopidlno.importer;

import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class URLXMLImporter extends FileXMLImporter {
    private final String url;
    private final RestTemplate restTemplate;
    private Path extractionDirectory;

    public URLXMLImporter(String url, RestTemplate restTemplate) {
        super("");
        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Override
    public Document getParsedXML() {
        Path zipFilePath = null;
        try {
            zipFilePath = downloadFromURL();
            filePath = getXMLFileFromZip(zipFilePath).toString();
            return super.getParsedXML();
        } finally {
            try {
                deleteTemporaryFiles(zipFilePath);
            } finally {
                extractionDirectory = null;
                filePath = "";
            }
        }
    }

    private Path downloadFromURL() {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        byte[] zipContent = restTemplate.getForObject(url, byte[].class);
        if (zipContent == null) {
            throw new IllegalStateException("No ZIP content returned from URL: " + url);
        }

        try {
            Path zipFilePath = Files.createTempFile("zipped-xml-", ".zip");
            Files.write(zipFilePath, zipContent);
            return zipFilePath;
        } catch (IOException e) {
            throw new RuntimeException("Error saving downloaded ZIP file", e);
        }
    }

    private Path getXMLFileFromZip(Path zipFilePath) {
        if (zipFilePath == null) {
            throw new IllegalArgumentException("ZIP file path cannot be null");
        }

        try {
            extractionDirectory = Files.createTempDirectory("unzipped-xml-");
            Path normalizedExtractionDirectory = extractionDirectory.toAbsolutePath().normalize();

            try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFilePath))) {
                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    Path outputPath = normalizedExtractionDirectory.resolve(entry.getName()).normalize();
                    if (!outputPath.startsWith(normalizedExtractionDirectory)) {
                        throw new IllegalArgumentException("ZIP entry escapes extraction directory: " + entry.getName());
                    }

                    if (entry.isDirectory()) {
                        Files.createDirectories(outputPath);
                    } else {
                        Files.createDirectories(outputPath.getParent());
                        Files.copy(zipInputStream, outputPath);
                    }
                    zipInputStream.closeEntry();
                }
            }

            List<Path> xmlFiles;
            try (var paths = Files.walk(normalizedExtractionDirectory)) {
                xmlFiles = paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".xml"))
                        .toList();
            }

            if (xmlFiles.size() != 1) {
                throw new IllegalStateException(
                        "Expected exactly one XML file in ZIP archive, found " + xmlFiles.size());
            }
            return xmlFiles.getFirst();
        } catch (IOException e) {
            throw new RuntimeException("Error extracting XML file from ZIP archive", e);
        }
    }

    private void deleteTemporaryFiles(Path zipFilePath) {
        IOException deletionFailure = null;
        if (extractionDirectory != null && Files.exists(extractionDirectory)) {
            try (var paths = Files.walk(extractionDirectory)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        if (deletionFailure == null) {
                            deletionFailure = e;
                        } else {
                            deletionFailure.addSuppressed(e);
                        }
                    }
                }
            } catch (IOException e) {
                if (deletionFailure == null) {
                    deletionFailure = e;
                } else {
                    deletionFailure.addSuppressed(e);
                }
            }
        }

        if (zipFilePath != null) {
            try {
                Files.deleteIfExists(zipFilePath);
            } catch (IOException e) {
                if (deletionFailure == null) {
                    deletionFailure = e;
                } else {
                    deletionFailure.addSuppressed(e);
                }
            }
        }

        if (deletionFailure != null) {
            throw new RuntimeException("Error deleting temporary importer files", deletionFailure);
        }
    }
}
