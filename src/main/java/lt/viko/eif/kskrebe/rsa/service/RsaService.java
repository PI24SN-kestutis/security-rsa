package lt.viko.eif.kskrebe.rsa.service;

import lt.viko.eif.kskrebe.rsa.model.RsaKeyPair;

import java.math.BigInteger;

/**
 * RSA logikos klasė.
 *
 * Atsakinga už:
 * - raktų generavimą
 * - šifravimą
 * - dešifravimą
 */
public class RsaService {

    private final MathService mathService = new MathService();

    /**
     * Sugeneruoja RSA raktų porą iš pirminių skaičių p ir q.
     *
     * @param p pirmas pirminis skaičius
     * @param q antras pirminis skaičius
     * @return RSA raktų pora
     */
    public RsaKeyPair generateKeys(BigInteger p, BigInteger q) {

        if (!mathService.isPrime(p) || !mathService.isPrime(q)) {
            throw new IllegalArgumentException("p ir q turi būti pirminiai.");
        }
        //skaičiuoja n
        BigInteger n = p.multiply(q);
        //skaičiuoja phi Eulerio funkcija
        BigInteger phi =
                p.subtract(BigInteger.ONE)
                        .multiply(q.subtract(BigInteger.ONE));

        BigInteger e = mathService.findPublicExponent(phi);

        BigInteger d = mathService.findPrivateExponent(e, phi);

        return new RsaKeyPair(p, q, n, phi, e, d);
    }

}