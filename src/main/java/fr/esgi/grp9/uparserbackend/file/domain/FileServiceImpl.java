package fr.esgi.grp9.uparserbackend.file.domain;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import fr.esgi.grp9.uparserbackend.exception.common.NotFoundWithIdException;
import fr.esgi.grp9.uparserbackend.exception.file.FileNameEmptyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;

    @Autowired
    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public File createFile(File file) {
        try {
            return fileRepository.save(
                    File.builder()
                            .fileName(file.getFileName())
                            .filePath(file.getFilePath())
                            .creationDate(LocalDateTime.now())
                            .build()
            );
        } catch (Exception valueInstantiationException){
            valueInstantiationException.getMessage();
        }
        return file;
    }

    @Override
    public File findFileById(String id) {
        return fileRepository.findById(id).orElseThrow(() -> new NotFoundWithIdException("File", id));
    }

    @Override
    public File updateFile(File file) {
        File old_file = fileRepository.findById(file.getId()).orElseThrow(() -> new NotFoundWithIdException("File", file.getId()));
        file.setCreationDate(old_file.getCreationDate());
        return fileRepository.save(file);
    }

    @Override
    public List<File> getFiles() {
        return fileRepository.findAll();
    }

    @Override
    public void deleteFileById(String id) {
        fileRepository.deleteById(id);
    }
}
