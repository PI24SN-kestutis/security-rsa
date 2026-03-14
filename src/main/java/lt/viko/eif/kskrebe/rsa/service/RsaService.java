package lt.viko.eif.kskrebe.rsa.service;

import lt.viko.eif.kskrebe.rsa.model.PrimeFactors;
import lt.viko.eif.kskrebe.rsa.model.RsaKeyPair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Užšifruoja tekstą RSA algoritmu, šifruodamas kiekvieną simbolį atskirai.
     *
     * <p>Kiekvienas simbolis paverčiamas į skaitinę Unicode reikšmę,
     * tada pritaikoma formulė:</p>
     *
     * <pre>
     * C = M^e mod n
     * </pre>
     *
     * @param plainText pradinis tekstas
     * @param e viešoji eksponentė
     * @param n RSA modulis
     * @return užšifruotų skaičių sąrašas
     * @throws IllegalArgumentException jei tekstas tuščias arba simbolio kodas nėra mažesnis už n
     */
    public List<BigInteger> encrypt(String plainText, BigInteger e, BigInteger n) {
        if (plainText == null || plainText.isBlank()) {
            throw new IllegalArgumentException("Laukas negali būti tuščias.");
        }

        List<BigInteger> encryptedValues = new ArrayList<>();

        for (char character : plainText.toCharArray()) {
            BigInteger messageValue = BigInteger.valueOf(character);

            if (messageValue.compareTo(n) >= 0) {
                throw new IllegalArgumentException(
                        "Simbolio kodas turi būti mažesnis nei n.: " + character
                );
            }

            //C=M^e mod n
            BigInteger encryptedValue = messageValue.modPow(e, n);
            encryptedValues.add(encryptedValue);
        }

        return encryptedValues;
    }

    /**
     * Dešifruoja RSA algoritmu užšifruotų reikšmių sąrašą.
     *
     * <p>Kiekvienai užšifruotai reikšmei taikoma formulė:</p>
     *
     * <pre>
     * M = C^d mod n
     * </pre>
     *
     * <p>Gauta skaitinė reikšmė paverčiama atgal į simbolį.</p>
     *
     * @param cipherValues užšifruotų reikšmių sąrašas
     * @param d privatus eksponentas
     * @param n RSA modulis
     * @return atkurtas pradinis tekstas
     * @throws IllegalArgumentException jei užšifruotų reikšmių sąrašas tuščias arba null
     */
    public String decrypt(List<BigInteger> cipherValues, BigInteger d, BigInteger n) {
        if (cipherValues == null || cipherValues.isEmpty()) {
            throw new IllegalArgumentException("Cipher reikšmė turi būti.");
        }

        StringBuilder decryptedText = new StringBuilder();

        for (BigInteger cipherValue : cipherValues) {

            //M=C^d mod n
            BigInteger decryptedValue = cipherValue.modPow(d, n);
            char character = (char) decryptedValue.intValue();
            decryptedText.append(character);
        }

        return decryptedText.toString();
    }

    /**
     * Atkuria RSA privatų raktą d iš viešo rakto (n, e).
     *
     * <p>Metodas demonstruoja matematinę RSA ataką:</p>
     *
     * <ol>
     * <li>faktorizuoja n į p ir q</li>
     * <li>apskaičiuoja phi(n)</li>
     * <li>apskaičiuoja privatų eksponentą d</li>
     * </ol>
     *
     * <p>Veikia tik jei n yra pakankamai mažas, kad būtų galima faktorizuoti.</p>
     *
     * @param n RSA modulis
     * @param e viešoji eksponentė
     * @return atkurta privati eksponentė d
     */
    public BigInteger recoverPrivateKey(BigInteger n, BigInteger e) {

        //pagrindinė RSA ataka
        PrimeFactors factors = mathService.factorizeN(n);

        BigInteger p = factors.getP();
        BigInteger q = factors.getQ();

        //phi atkūrimas
        BigInteger phi = mathService.recoverPhi(p, q);

        //privatus rakto radimas
        return mathService.findPrivateExponent(e, phi);
    }

}