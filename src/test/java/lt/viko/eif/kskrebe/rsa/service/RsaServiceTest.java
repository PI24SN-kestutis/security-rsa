package lt.viko.eif.kskrebe.rsa.service;

import lt.viko.eif.kskrebe.rsa.model.RsaKeyPair;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

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
}