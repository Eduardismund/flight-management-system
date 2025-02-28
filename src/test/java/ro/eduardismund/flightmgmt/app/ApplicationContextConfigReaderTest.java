package ro.eduardismund.flightmgmt.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ApplicationContextConfigReaderTest {

    @Test
    public void getReaderWithPath(@TempDir Path tempDir) throws IOException {
        final var filePath = tempDir.resolve(getClass().getSimpleName() + ".properties");
        final String content = """
                key1=value1
                key2=value2""";

        Files.writeString(filePath, content);
        final var reader = new ApplicationContext().getReader(filePath.toString());
        final var bufferedReader = new BufferedReader(reader);
        assertEquals(content, bufferedReader.lines().collect(Collectors.joining("\n")));
    }

    @Test
    void getReader() throws IOException {
        final var subject = spy(ApplicationContext.class);
        final var reader = mock(Reader.class);
        doReturn(reader).when(subject).getReader(ApplicationContext.CONFIG_FILE);

        assertSame(reader, subject.getReader());
    }
}
