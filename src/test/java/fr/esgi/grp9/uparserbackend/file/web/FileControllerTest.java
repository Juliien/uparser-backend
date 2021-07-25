package fr.esgi.grp9.uparserbackend.file.web;

import fr.esgi.grp9.uparserbackend.file.domain.File;
import fr.esgi.grp9.uparserbackend.file.service.FileService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;
=======
>>>>>>> f99e331661596ef02185434ce6bf67f6739a8e78

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

<<<<<<< HEAD
@SpringBootTest
=======
>>>>>>> f99e331661596ef02185434ce6bf67f6739a8e78
@RunWith(MockitoJUnitRunner.class)
public class FileControllerTest {

    @InjectMocks
    FileController fileController;
    @Mock
    FileService fileService;

    private final File file = File.builder()
            .fileName("nameTest")
            .fileContent("a/path")
<<<<<<< HEAD
            .userId("anId")
            .createDate(LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40))
=======
            .createDate(new Date())
>>>>>>> f99e331661596ef02185434ce6bf67f6739a8e78
            .build();

    @Test
    public void should_get_list_of_files() {
        fileController.getFiles();
        verify(fileService).getFiles();
    }

    @Test(expected = ResponseStatusException.class)
    public void should_throw_404_when_find_by_id_new_file() {
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
