package fr.esgi.grp9.uparserbackend.file.web;

import fr.esgi.grp9.uparserbackend.file.domain.File;
import fr.esgi.grp9.uparserbackend.file.domain.FileServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Date;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FileControllerTest {

    @InjectMocks
    FileController fileController;
    @Mock
    FileServiceImpl fileService;

    private final File file = File.builder()
            .fileName("nameTest")
            .fileContent("a/path")
            .createDate(new Date())
            .build();

    @Test
    public void should_get_list_of_files() {
        fileController.getFiles();
        verify(fileService).getFiles();
    }

    @Test
    public void should_find_by_id_new_file() {
        String id = "anId";
        fileController.getFileById(id);
        verify(fileService).findFileById(id);
    }

    @Test
    public void should_create_new_file() {
        ResponseEntity<File> responseEntity = fileController.createFile(this.file);
        verify(fileService).createFile(this.file);
        Assert.assertEquals(new ResponseEntity<>(this.file, HttpStatus.CREATED), responseEntity);
    }
}
