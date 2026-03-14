package lt.viko.eif.kskrebe.rsa.service;

import lt.viko.eif.kskrebe.rsa.model.RsaKeyPair;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RsaServiceTest {

    private final RsaService rsaService = new RsaService();

    @Test
    void shouldGenerateValidRsaKeys() {

        BigInteger p = BigInteger.valueOf(61);
        BigInteger q = BigInteger.valueOf(53);

        RsaKeyPair keys = rsaService.generateKeys(p, q);

        assertEquals(BigInteger.valueOf(3233), keys.getN());

        BigInteger result =
                keys.getE()
                        .multiply(keys.getD())
                        .mod(keys.getPhi());

        assertEquals(BigInteger.ONE, result);
    }

    @Test
    void shouldEncryptText() {
        RsaKeyPair keys = rsaService.generateKeys(
                BigInteger.valueOf(61),
                BigInteger.valueOf(53)
        );

        List<BigInteger> encrypted = rsaService.encrypt("A", keys.getE(), keys.getN());

        //ar yra rezultatas
        assertNotNull(encrypted);
        //ar simbolis duoda vieną reikšmę
        assertEquals(1, encrypted.size());
        //patikrina ar nėra reikšmė lygi užšifruotai reikšmei
        assertNotEquals(BigInteger.valueOf((int) 'A'), encrypted.get(0));
    }

    @Test //testas tuščiam tekstui
    void shouldThrowExceptionWhenPlainTextIsEmpty() {
        RsaKeyPair keys = rsaService.generateKeys(
                BigInteger.valueOf(61),
                BigInteger.valueOf(53)
        );

        assertThrows(IllegalArgumentException.class,
                () -> rsaService.encrypt("", keys.getE(), keys.getN()));
    }

    @Test
    void shouldDecryptEncryptedTextBackToOriginal() {
        //sugeneruoja raktus
        RsaKeyPair keys = rsaService.generateKeys(
                BigInteger.valueOf(61),
                BigInteger.valueOf(53)
        );

        String originalText = "LABAS";
        //užšifruojamas tekstas
        List<BigInteger> encrypted = rsaService.encrypt(
                originalText,
                keys.getE(),
                keys.getN()
        );
        //iššifruojamas tekstas
        String decrypted = rsaService.decrypt(
                encrypted,
                keys.getD(),
                keys.getN()
        );

        //palyginamas originalus tekstas su iššifruotu tekstu
        assertEquals(originalText, decrypted);
    }

    @Test //testas tuščiam Cipher tekstui
    void shouldThrowExceptionWhenCipherValuesAreEmpty() {
        RsaKeyPair keys = rsaService.generateKeys(
                BigInteger.valueOf(61),
                BigInteger.valueOf(53)
        );

        assertThrows(IllegalArgumentException.class,
                () -> rsaService.decrypt(List.of(), keys.getD(), keys.getN()));
    }

    @Test
    void shouldRecoverPrivateKeyFromPublicKey() {

        //sugeneruoja raktus
        RsaKeyPair keys = rsaService.generateKeys(
                BigInteger.valueOf(61),
                BigInteger.valueOf(53)
        );

        //ataka
        BigInteger recoveredD = rsaService.recoverPrivateKey(
                keys.getN(),
                keys.getE()
        );

        //tikrinam ar atkurtas d sutampa su tikruoju
        assertEquals(keys.getD(), recoveredD);
    }

    @Test //RSA matematinės atakos demonstracija
    void recoveredPrivateKeyShouldDecryptMessage() {

        RsaKeyPair keys = rsaService.generateKeys(
                BigInteger.valueOf(61),
                BigInteger.valueOf(53)
        );

        String message = "RSA";

        List<BigInteger> encrypted =
                rsaService.encrypt(message, keys.getE(), keys.getN());

        BigInteger recoveredD =
                rsaService.recoverPrivateKey(keys.getN(), keys.getE());

        String decrypted =
                rsaService.decrypt(encrypted, recoveredD, keys.getN());

        assertEquals(message, decrypted);
    }
}