package lt.viko.eif.kskrebe.rsa.service;

import java.math.BigInteger;

/**
 * Klasė, atsakinga už matematines operacijas,
 * reikalingas RSA algoritmui.
 */
public class MathService {

    /**
     * Patikrina, ar pateiktas skaičius yra pirminis.
     *
     * <p>Pirminis skaičius yra didesnis už 1 ir dalijasi
     * tik iš 1 ir savęs paties.</p>
     *
     * <p>Metodas veikia taip:</p>
     * <ol>
     *     <li>Atmeta skaičius, mažesnius už 2.</li>
     *     <li>2 laiko pirminiu skaičiumi.</li>
     *     <li>Atmeta visus kitus lyginius skaičius.</li>
     *     <li>Tikrina nelyginius daliklius nuo 3 iki √n.</li>
     * </ol>
     *
     * @param number tikrinamas skaičius
     * @return {@code true}, jei skaičius pirminis; {@code false}, jei ne
     */
    public boolean isPrime(BigInteger number) {
        if (number.compareTo(BigInteger.TWO) < 0) {
            return false;
        }

        if (number.equals(BigInteger.TWO)) {
            return true;
        }

        if (number.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return false;
        }
        //tikrinam nuo 3
        BigInteger divisor = BigInteger.valueOf(3);
        //kvadratinė šaknis
        while (divisor.multiply(divisor).compareTo(number) <= 0) {
            if (number.mod(divisor).equals(BigInteger.ZERO)) {
                return false;
            }
        //judame tik per nelyginius
            divisor = divisor.add(BigInteger.TWO);
        }

        return true;
    }

    /**
     * Apskaičiuoja dviejų skaičių didžiausią bendrą daliklį
     * naudodamas Euklido algoritmą.
     *
     * <p>Algoritmo esmė:</p>
     * <pre>
     * gcd(a, b) = gcd(b, a mod b)
     * </pre>
     *
     * <p>Kai {@code b = 0}, rezultatas yra {@code a}.</p>
     *
     * @param a pirmas skaičius
     * @param b antras skaičius
     * @return didžiausias bendras daliklis
     */
    public BigInteger gcd(BigInteger a, BigInteger b) {
        //absoliučios reikšmės
        a = a.abs();
        b = b.abs();
        //kol b nėra 0(nulis)
        while (!b.equals(BigInteger.ZERO)) {
            BigInteger remainder = a.mod(b); //liekana
            // poros perstūmimas
            a = b;
            b = remainder;
        }
        //kai b = 0, a - didžiausias bendras daliklis
        return a;
    }
}