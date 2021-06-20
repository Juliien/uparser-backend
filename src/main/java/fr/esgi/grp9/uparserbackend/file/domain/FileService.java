package fr.esgi.grp9.uparserbackend.file.domain;

import java.util.List;
import java.util.Optional;

public interface FileService {
    File createFile(File file);
    File findFileById(String id);
    List<File> getFiles();
<<<<<<< HEAD
    void deleteFileById(String id);
=======
>>>>>>> 47d0e7b66c572dfa83d8e65f1e152d5a4a33e80f
}
