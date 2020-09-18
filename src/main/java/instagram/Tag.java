package instagram;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tag {
    public static String getRandomTag() throws NoSuchAlgorithmException {
        List<String> tags = new ArrayList<>();
        Path path = Paths.get("./src/main/resources/tags");
        try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)){
            String currentLine;
            while((currentLine = reader.readLine()) != null){//while there is content on the current line
                tags.add(currentLine);
            }
        }catch(IOException ex){
            ex.printStackTrace(); //handle an exception here
        }
        return "- ".concat(tags.get(new Random().nextInt(tags.size())));
    }
}