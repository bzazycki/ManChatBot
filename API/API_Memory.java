package API;

import java.io.FileWriter;
import java.io.IOException;

public class API_Memory {

    public static void clearMemory() {
        try {
            FileWriter writer = new FileWriter("history.txt", false);
            writer.write("");
            writer.close();
            System.out.println("Memory cleared successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while clearing memory.");
            e.printStackTrace();
        }
    }
}
