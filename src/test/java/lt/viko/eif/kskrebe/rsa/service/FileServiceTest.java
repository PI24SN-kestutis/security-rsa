package lt.viko.eif.kskrebe.rsa.service;

import lt.viko.eif.kskrebe.rsa.model.EncryptedFileData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileServiceTest {

    private final FileService fileService = new FileService();

    @TempDir
    Path tempDir;

    // Pilnas scenarijus: duomenys išsaugomi ir po to korektiškai nuskaitomi.
    @Test
    void shouldSaveAndReadEncryptedData() throws IOException {
        Path file = tempDir.resolve("rsa-data.txt");
        EncryptedFileData originalData = new EncryptedFileData(
                BigInteger.valueOf(3233),
                BigInteger.valueOf(7),
                List.of(BigInteger.valueOf(1087), BigInteger.valueOf(3020), BigInteger.valueOf(1877))
        );

        fileService.saveEncryptedData(file, originalData);
        EncryptedFileData restoredData = fileService.readEncryptedData(file);

        assertEquals(originalData.getN(), restoredData.getN());
        assertEquals(originalData.getE(), restoredData.getE());
        assertIterableEquals(originalData.getCipherValues(), restoredData.getCipherValues());
    }

    // Nuskaitymas turi veikti ir tada, kai faile tarp reikšmių yra papildomų tarpų.
    @Test
    void shouldReadEncryptedDataWithSpacesAroundValues() throws IOException {
        Path file = tempDir.resolve("rsa-spaces.txt");
        Files.writeString(file, "3233 ; 7 ; 1087 ; 3020 ; 1877");

        EncryptedFileData restoredData = fileService.readEncryptedData(file);

        assertEquals(BigInteger.valueOf(3233), restoredData.getN());
        assertEquals(BigInteger.valueOf(7), restoredData.getE());
        assertIterableEquals(
                List.of(BigInteger.valueOf(1087), BigInteger.valueOf(3020), BigInteger.valueOf(1877)),
                restoredData.getCipherValues()
        );
    }

    // Negalima saugoti, jei nenurodytas failo kelias.
    @Test
    void shouldThrowExceptionWhenSavePathIsNull() {
        EncryptedFileData data = new EncryptedFileData(
                BigInteger.valueOf(3233),
                BigInteger.valueOf(7),
                List.of(BigInteger.ONE)
        );

        assertThrows(IllegalArgumentException.class, () -> fileService.saveEncryptedData(null, data));
    }

    // Negalima saugoti null duomenų objekto.
    @Test
    void shouldThrowExceptionWhenDataIsNull() {
        Path file = tempDir.resolve("rsa-null.txt");

        assertThrows(IllegalArgumentException.class, () -> fileService.saveEncryptedData(file, null));
    }

    // Tuščias cipher reikšmių sąrašas neturi būti išsaugomas.
    @Test
    void shouldThrowExceptionWhenCipherValuesAreEmpty() {
        Path file = tempDir.resolve("rsa-empty-cipher.txt");
        EncryptedFileData data = new EncryptedFileData(
                BigInteger.valueOf(3233),
                BigInteger.valueOf(7),
                List.of()
        );

        assertThrows(IllegalArgumentException.class, () -> fileService.saveEncryptedData(file, data));
    }

    // Nuskaitymui taip pat būtina perduoti egzistuojantį kelią.
    @Test
    void shouldThrowExceptionWhenReadPathIsNull() {
        assertThrows(IllegalArgumentException.class, () -> fileService.readEncryptedData(null));
    }

    // Failas be duomenų laikomas netinkamu.
    @Test
    void shouldThrowExceptionWhenFileIsEmpty() throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "   ");

        assertThrows(IllegalArgumentException.class, () -> fileService.readEncryptedData(file));
    }

    // Faile turi būti bent n, e ir viena cipher reikšmė.
    @Test
    void shouldThrowExceptionWhenEncryptedFileFormatIsInvalid() throws IOException {
        Path file = tempDir.resolve("invalid.txt");
        Files.writeString(file, "3233;7");

        assertThrows(IllegalArgumentException.class, () -> fileService.readEncryptedData(file));
    }

    // Tuščias elementas tarp kabliataškių laikomas sugadintu įrašu.
    @Test
    void shouldThrowExceptionWhenCipherValueIsBlank() throws IOException {
        Path file = tempDir.resolve("blank-cipher.txt");
        Files.writeString(file, "3233;7;1087; ;1877");

        assertThrows(IllegalArgumentException.class, () -> fileService.readEncryptedData(file));
    }
}
