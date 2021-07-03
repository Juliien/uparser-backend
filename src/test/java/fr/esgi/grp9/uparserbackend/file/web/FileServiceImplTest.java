package fr.esgi.grp9.uparserbackend.file.web;

import fr.esgi.grp9.uparserbackend.exception.common.NotFoundWithIdException;
import fr.esgi.grp9.uparserbackend.file.domain.File;
import fr.esgi.grp9.uparserbackend.file.domain.FileRepository;
import fr.esgi.grp9.uparserbackend.file.domain.FileServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceImplTest {

    @InjectMocks
    private FileServiceImpl fileServiceImpl;
    @Mock
    private FileRepository fileRepository;

    private File file = File.builder()
            .fileName("nameTest")
            .filePath("a/path")
            .creationDate(LocalDate.now())
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

    @Test(expected = NotFoundWithIdException.class)
    public void should_throw_NotFoundWithIdException_when_find_nonexistent_file() {
        String id = "impossible";
        fileServiceImpl.findFileById(id);
        verify(fileRepository).findById(id);
    }

    @Test(expected = NotFoundWithIdException.class)
    public void should_throw_NotFoundWithIdException_when_update_nonexistent_file() {
        this.file.setId("impossible");
        this.file.setFileName("nameTestUpdated");
        File updatedFile = fileServiceImpl.updateFile(this.file);
        verify(fileRepository).save(file);
        Assert.assertEquals(this.file, updatedFile);
    }

    @Test(expected = NotFoundWithIdException.class)
    public void should_throw_NotFoundWithIdException_when_delete_nonexistent_file() {
        this.file.setId("impossible");
        this.file.setFileName("nameTestUpdated");
        File updatedFile = fileServiceImpl.updateFile(this.file);
        verify(fileRepository).save(file);
        Assert.assertEquals(this.file, updatedFile);
    }
}
