package ludo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * @description Klasse um die Beschreinung in der Simulation anzuzeigen
 */
public class CreateIndex {
    String destination = "target/classes/ludo/";
    File html = new File("src/main/resources/index.html");
    File png = new File("src/main/resources/icon.png");

    /**
     * @description Kopiert die Dateien der Simulationsbeschreibung von resources in den Output
     */
    public void copy_files() {
        List<File> files = new ArrayList<>();
        files.add(html);
        files.add(png);

        try {
            for(File file : files) {
                Files.copy(file.toPath(),
                        (new File(destination + file.getName())).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch(Exception e) {
            System.err.println("Failed to copy files");
            e.printStackTrace();
        }
    }

}
