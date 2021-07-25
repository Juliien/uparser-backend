package fr.esgi.grp9.uparserbackend.file.service;

import fr.esgi.grp9.uparserbackend.file.domain.File;

import java.util.List;
import java.util.Optional;

public interface IFileService {
    File createFile(File file);
    Optional<File> findFileById(String id);
    List<File> getFiles();
    List<File> getFilesByUserId(String userId);
    void deleteFileById(String id);
}
