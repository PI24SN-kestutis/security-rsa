package lt.viko.eif.kskrebe.rsa.service;

import lt.viko.eif.kskrebe.rsa.model.RsaKeyPair;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RsaServiceTest {

    private final RsaService rsaService = new RsaService();

    // Patikrina, ar iš dviejų pirminių skaičių sugeneruojami korektiški RSA parametrai.
    @Test
    void shouldGenerateValidRsaKeys() {
        BigInteger p = BigInteger.valueOf(61);
        BigInteger q = BigInteger.valueOf(53);

        RsaKeyPair keys = rsaService.generateKeys(p, q);

        assertEquals(BigInteger.valueOf(3233), keys.getN());
        assertEquals(BigInteger.ONE, keys.getE().multiply(keys.getD()).mod(keys.getPhi()));
    }

    // Nepirminiai p arba q neturi būti priimami raktų generavimui.
    @Test
    void shouldThrowExceptionWhenGeneratingKeysWithNonPrimeNumbers() {
        assertThrows(IllegalArgumentException.class,
                () -> rsaService.generateKeys(BigInteger.valueOf(12), BigInteger.valueOf(53)));
    }

    // Vienas simbolis turi būti užšifruojamas į vieną skaitinę reikšmę.
    @Test
    void shouldEncryptText() {
        RsaKeyPair keys = rsaService.generateKeys(BigInteger.valueOf(61), BigInteger.valueOf(53));

        List<BigInteger> encrypted = rsaService.encrypt("A", keys.getE(), keys.getN());

        assertNotNull(encrypted);
        assertEquals(1, encrypted.size());
        assertNotEquals(BigInteger.valueOf('A'), encrypted.get(0));
    }

    // Tuščias tekstas negali būti šifruojamas.
    @Test
    void shouldThrowExceptionWhenPlainTextIsEmpty() {
        RsaKeyPair keys = rsaService.generateKeys(BigInteger.valueOf(61), BigInteger.valueOf(53));

        assertThrows(IllegalArgumentException.class,
                () -> rsaService.encrypt("", keys.getE(), keys.getN()));
    }

    // Jei simbolio kodas yra didesnis arba lygus n, RSA šifravimas negalimas.
    @Test
    void shouldThrowExceptionWhenSymbolCodeIsNotSmallerThanModulus() {
        assertThrows(IllegalArgumentException.class,
                () -> rsaService.encrypt("A", BigInteger.valueOf(3), BigInteger.valueOf(60)));
    }

    // Užšifruotas tekstas turi būti atkuriamas į pradinę reikšmę.
    @Test
    void shouldDecryptEncryptedTextBackToOriginal() {
        RsaKeyPair keys = rsaService.generateKeys(BigInteger.valueOf(61), BigInteger.valueOf(53));
        String originalText = "LABAS";

        List<BigInteger> encrypted = rsaService.encrypt(originalText, keys.getE(), keys.getN());
        String decrypted = rsaService.decrypt(encrypted, keys.getD(), keys.getN());

        assertEquals(originalText, decrypted);
    }

    // Dešifravimui privalomas bent vienas cipher elementas.
    @Test
    void shouldThrowExceptionWhenCipherValuesAreEmpty() {
        RsaKeyPair keys = rsaService.generateKeys(BigInteger.valueOf(61), BigInteger.valueOf(53));

        assertThrows(IllegalArgumentException.class,
                () -> rsaService.decrypt(List.of(), keys.getD(), keys.getN()));
    }

    // Null vietoje cipher sąrašo taip pat laikoma netinkama įvestimi.
    @Test
    void shouldThrowExceptionWhenCipherValuesAreNull() {
        RsaKeyPair keys = rsaService.generateKeys(BigInteger.valueOf(61), BigInteger.valueOf(53));

        assertThrows(IllegalArgumentException.class,
                () -> rsaService.decrypt(null, keys.getD(), keys.getN()));
    }

    // Atakos metodas turi atkurti tą patį privatų eksponentą d.
    @Test
    void shouldRecoverPrivateKeyFromPublicKey() {
        RsaKeyPair keys = rsaService.generateKeys(BigInteger.valueOf(61), BigInteger.valueOf(53));

        BigInteger recoveredD = rsaService.recoverPrivateKey(keys.getN(), keys.getE());

        assertEquals(keys.getD(), recoveredD);
    }

    // Atkurtas privatus raktas turi leisti sėkmingai dešifruoti žinutę.
    @Test
    void recoveredPrivateKeyShouldDecryptMessage() {
        RsaKeyPair keys = rsaService.generateKeys(BigInteger.valueOf(61), BigInteger.valueOf(53));
        String message = "RSA";

        List<BigInteger> encrypted = rsaService.encrypt(message, keys.getE(), keys.getN());
        BigInteger recoveredD = rsaService.recoverPrivateKey(keys.getN(), keys.getE());
        String decrypted = rsaService.decrypt(encrypted, recoveredD, keys.getN());

        assertEquals(message, decrypted);
    }
}
