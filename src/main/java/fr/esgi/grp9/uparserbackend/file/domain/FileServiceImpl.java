package fr.esgi.grp9.uparserbackend.file.domain;

import fr.esgi.grp9.uparserbackend.common.exception.NotFoundWithIdException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public File createFile(File file) {
        return fileRepository.save(
                File.builder()
                        .fileName(file.getFileName())
                        .filePath(file.getFilePath())
                        .creationDate(LocalDate.now())
                        .runDate(null)
                        .build()
        );
    }

    @Override
    public File findFileById(String id) {
        return fileRepository.findFileById(id).orElseThrow(() -> new NotFoundWithIdException("File", id));
    }

    @Override
    public List<File> getFiles() {
        return fileRepository.findAll();
    }
}