import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class API_MemoryTest {

    @Test
    public void testClearMemory() {
        // Setup: Write some content to the file
        try {
            FileWriter writer = new FileWriter("history.txt", false);
            writer.write("Some content");
            writer.close();
        } catch (IOException e) {
            fail("Setup failed: Unable to write to history.txt");
        }

        // Act: Call the method to clear memory
        API_Memory.clearMemory();

        // Assert: Check if the file is empty
        try {
            String content = new String(Files.readAllBytes(Paths.get("history.txt")));
            assertEquals("", content, "File should be empty after clearing memory");
        } catch (IOException e) {
            fail("Test failed: Unable to read from history.txt");
        }
    }
}