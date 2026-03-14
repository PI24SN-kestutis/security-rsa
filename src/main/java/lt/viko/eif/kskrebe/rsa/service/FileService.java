package lt.viko.eif.kskrebe.rsa.service;

import lt.viko.eif.kskrebe.rsa.model.EncryptedFileData;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasė, atsakinga už RSA duomenų saugojimą ir nuskaitymą iš failų.
 *
 * <p>Naudojamas vienos eilutės formatas:</p>
 * <pre>
 * n;e;cipher1;cipher2;cipher3;...
 * </pre>
 *
 * <p>Pirmas skaičius yra RSA modulis {@code n},
 * antras – viešasis eksponentas {@code e},
 * o visi likę – užšifruoto teksto reikšmės.</p>
 */
public class FileService {

    /**
     * Išsaugo užšifruotus duomenis tekstiniame faile viena eilute.
     *
     * <p>Formato pavyzdys:</p>
     * <pre>
     * 3233;7;1087;3020;1877;1087;2412
     * </pre>
     *
     * @param filePath failo kelias
     * @param data užšifruotų duomenų objektas
     * @throws IOException jei nepavyksta įrašyti failo
     * @throws IllegalArgumentException jei failo kelias arba duomenys yra null
     */
    public void saveEncryptedData(Path filePath, EncryptedFileData data) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path must not be null.");
        }

        if (data == null) {
            throw new IllegalArgumentException("Encrypted data must not be null.");
        }

        if (data.getCipherValues() == null || data.getCipherValues().isEmpty()) {
            throw new IllegalArgumentException("Cipher values must not be null or empty.");
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

    /**
     * Nuskaito užšifruotus duomenis iš failo.
     *
     * <p>Tikimasi vienos eilutės formato:</p>
     * <pre>
     * n;e;cipher1;cipher2;cipher3;...
     * </pre>
     *
     * @param filePath failo kelias
     * @return nuskaitytų užšifruotų duomenų objektas
     * @throws IOException jei nepavyksta perskaityti failo
     * @throws IllegalArgumentException jei failas tuščias arba formatas neteisingas
     */
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

        BigInteger n = new BigInteger(parts[0].trim());
        BigInteger e = new BigInteger(parts[1].trim());

        List<BigInteger> cipherValues = new ArrayList<>();

        for (int i = 2; i < parts.length; i++) {
            String part = parts[i].trim();

            if (part.isEmpty()) {
                throw new IllegalArgumentException("Cipher value is empty.");
            }

            cipherValues.add(new BigInteger(part));
        }

        return new EncryptedFileData(n, e, cipherValues);
    }
}