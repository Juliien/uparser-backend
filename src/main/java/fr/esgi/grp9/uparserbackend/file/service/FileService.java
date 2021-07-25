package fr.esgi.grp9.uparserbackend.file.service;

import fr.esgi.grp9.uparserbackend.file.domain.File;
import fr.esgi.grp9.uparserbackend.file.domain.FileRepository;
import fr.esgi.grp9.uparserbackend.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FileService implements IFileService {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    @Autowired
    public FileService(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public File createFile(File file) {
        file.setCreateDate(LocalDateTime.now());
        return fileRepository.save(file);
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
    public Optional<List<File>> getFilesByUserId(String userId) {
        Optional<List<File>> _files = Optional.empty();
        if (this.userRepository.findById(userId).isEmpty()){
            return _files;
        }
        return this.fileRepository.findAllByUserId(userId);
    }

    @Override
    public void deleteFileById(String id) {
        fileRepository.deleteById(id);
    }
}
