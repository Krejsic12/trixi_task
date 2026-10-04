package trixi.interview.kopidlno.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class ZipWithXmlHelpers {
    protected static byte[] zipWithXml(String name, String content) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(output)) {
            zip.putNextEntry(new ZipEntry(name));
            zip.write(content.getBytes());
            zip.closeEntry();
        }
        return output.toByteArray();
    }
}
