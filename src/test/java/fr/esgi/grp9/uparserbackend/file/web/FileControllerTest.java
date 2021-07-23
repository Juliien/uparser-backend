package fr.esgi.grp9.uparserbackend.file.web;

import fr.esgi.grp9.uparserbackend.file.domain.File;
import fr.esgi.grp9.uparserbackend.file.service.FileService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class FileControllerTest {

    @InjectMocks
    FileController fileController;
    @Mock
    FileService fileService;

    private final File file = File.builder()
            .fileName("nameTest")
            .fileContent("a/path")
            .userId("anId")
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
}
