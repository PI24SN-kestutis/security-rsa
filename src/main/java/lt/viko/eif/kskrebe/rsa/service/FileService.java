package lt.viko.eif.kskrebe.rsa.service;

import lt.viko.eif.kskrebe.rsa.model.EncryptedFileData;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasė, atsakinga už RSA duomenų saugojimą ir nuskaitymą iš failų.
 */
public class FileService {

    /**
     * Išsaugo užšifruotus duomenis tekstiniame faile.
     *
     * <p>Faile saugoma:</p>
     * <pre>
     * n=...
     * e=...
     * cipher=...
     * </pre>
     *
     * @param filePath failo kelias
     * @param data užšifruotų duomenų objektas
     * @throws IOException jei nepavyksta įrašyti failo
     */
    public void saveEncryptedData(Path filePath, EncryptedFileData data) throws IOException {

        if (filePath == null) {
            throw new IllegalArgumentException("File path must not be null.");
        }

        if (data == null) {
            throw new IllegalArgumentException("Encrypted data must not be null.");
        }

        StringBuilder builder = new StringBuilder();

        builder.append(data.getN());
        builder.append(";");
        builder.append(data.getE());

        for (BigInteger value : data.getCipherValues()) {
            builder.append(";");
            builder.append(value);
        }

        Files.writeString(filePath, builder.toString());
    }

    public EncryptedFileData readEncryptedData(Path filePath) throws IOException {

        if (filePath == null) {
            throw new IllegalArgumentException("File path must not be null.");
        }

        String content = Files.readString(filePath).trim();

        if (content.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        String[] parts = content.split(";");

        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid encrypted file format.");
        }

        BigInteger n = new BigInteger(parts[0]);
        BigInteger e = new BigInteger(parts[1]);

        List<BigInteger> cipherValues = new ArrayList<>();

        for (int i = 2; i < parts.length; i++) {
            cipherValues.add(new BigInteger(parts[i]));
        }

        return new EncryptedFileData(n, e, cipherValues);
    }
}