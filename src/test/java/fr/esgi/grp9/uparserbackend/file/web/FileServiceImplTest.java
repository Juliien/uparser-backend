package fr.esgi.grp9.uparserbackend.file.web;

import fr.esgi.grp9.uparserbackend.file.domain.File;
import fr.esgi.grp9.uparserbackend.file.domain.FileRepository;
import fr.esgi.grp9.uparserbackend.file.service.FileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceImplTest {

    @InjectMocks
    private FileService fileServiceImpl;
    @Mock
    private FileRepository fileRepository;

    private File file = File.builder()
            .fileName("nameTest")
            .fileContent("a/path")
            .userId("anId")
            .createDate(LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40))
            .build();

    @Test
    public void should_create_file_nominal() {
        fileServiceImpl.createFile(file);
        verify(fileRepository).save(file);
    }

    @Test
    public void should_get_files_nominal() {
        fileServiceImpl.getFiles();
        verify(fileRepository).findAll();
    }

//    @Test(expected = NotFoundWithIdException.class)
//    public void should_throw_NotFoundWithIdException_when_find_nonexistent_file() {
//        String id = "impossible";
//        fileServiceImpl.findFileById(id);
//        verify(fileRepository).findById(id);
//    }
}
