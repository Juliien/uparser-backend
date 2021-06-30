package fr.esgi.grp9.uparserbackend.file.domain;

import java.util.List;
import java.util.Optional;

public interface FileService {
    File createFile(File file);
    File findFileById(String id);
    File updateFile(File file);
    List<File> getFiles();
    void deleteFileById(String id);
}
