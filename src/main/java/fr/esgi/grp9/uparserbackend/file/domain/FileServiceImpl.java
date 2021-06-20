package fr.esgi.grp9.uparserbackend.file.domain;

import fr.esgi.grp9.uparserbackend.common.exception.NotFoundWithIdException;
<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
=======
>>>>>>> 47d0e7b66c572dfa83d8e65f1e152d5a4a33e80f
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;

<<<<<<< HEAD
    @Autowired
=======
>>>>>>> 47d0e7b66c572dfa83d8e65f1e152d5a4a33e80f
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
<<<<<<< HEAD
        return fileRepository.findById(id).orElseThrow(() -> new NotFoundWithIdException("File", id));
=======
        return fileRepository.findFileById(id).orElseThrow(() -> new NotFoundWithIdException("File", id));
>>>>>>> 47d0e7b66c572dfa83d8e65f1e152d5a4a33e80f
    }

    @Override
    public List<File> getFiles() {
        return fileRepository.findAll();
    }
<<<<<<< HEAD

    @Override
    public void deleteFileById(String id) {
        fileRepository.deleteById(id);
    }
=======
>>>>>>> 47d0e7b66c572dfa83d8e65f1e152d5a4a33e80f
}
