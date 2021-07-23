package fr.esgi.grp9.uparserbackend.file.service;

import fr.esgi.grp9.uparserbackend.file.domain.File;
import fr.esgi.grp9.uparserbackend.file.domain.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FileService implements IFileService {
    private final FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public File createFile(File file) {
        try {
            return fileRepository.save(
                    File.builder()
                            .userId(file.getUserId())
                            .fileName(file.getFileName())
                            .fileContent(file.getFileContent())
                            .fileExtension(file.getFileExtension())
                            .createDate(new Date())
                            .build()
            );
        } catch (Exception valueInstantiationException){
            valueInstantiationException.getMessage();
        }
        return file;
    }

    @Override
    public Optional<File> findFileById(String id) {
        return fileRepository.findById(id);
    }

    @Override
    public List<File> getFiles() {
        return fileRepository.findAll();
    }

    @Override
    public List<File> getFilesByUserId(String userId) {
        return this.fileRepository.findAllByUserId(userId);
    }

    @Override
    public void deleteFileById(String id) {
        fileRepository.deleteById(id);
    }
}
