package lt.viko.eif.kskrebe.rsa.service;

import lt.viko.eif.kskrebe.rsa.model.EncryptedFileData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceTest {

    private final FileService fileService = new FileService();

    @TempDir //JUnit sukuria laikiną katalogą testui.
    Path tempDir;


}