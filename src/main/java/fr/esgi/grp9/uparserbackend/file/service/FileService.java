package fr.esgi.grp9.uparserbackend.file.domain;

import fr.esgi.grp9.uparserbackend.exception.common.NotFoundWithIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public File findFileById(String id) {
        return fileRepository.findById(id).orElseThrow(() -> new NotFoundWithIdException("File", id));
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
