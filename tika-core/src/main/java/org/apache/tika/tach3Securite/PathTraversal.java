package org.apache.tika.tach3Securite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


// Construit un chemin sans vérifier que celui-ci reste dans repo autorisé
public class PathTraversal {

    public String readFile(String filename) throws IOException {
        Path path = Paths.get("/app/documents", filename); 
        return Files.readString(path);
    }
}
