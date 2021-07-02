package fr.esgi.grp9.uparserbackend.file.web;

import fr.esgi.grp9.uparserbackend.exception.common.NotFoundWithIdException;
import fr.esgi.grp9.uparserbackend.file.domain.File;
import fr.esgi.grp9.uparserbackend.file.domain.FileServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FileControllerTest {

    @InjectMocks
    FileController fileController;
    @Mock
    FileServiceImpl fileService;

    @Test
    public void should_get_list_of_files() {
        fileController.getFiles();
        verify(fileService).getFiles();
    }

    @Test
    public void should_create_new_file() {
        final File file = File.builder()
                .fileName("nameTest")
                .filePath("a/path")
                .creationDate(LocalDate.now())
                .build();
        fileController.createFile(file);
        verify(fileService).createFile(file);
    }
}
