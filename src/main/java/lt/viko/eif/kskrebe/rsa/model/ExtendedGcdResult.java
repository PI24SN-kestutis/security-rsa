package lt.viko.eif.kskrebe.rsa.model;

import java.math.BigInteger;

/**
 * Rezultatas išplėstiniam Euklido algoritmui.
 *
 * Saugo tris reikšmes:
 * gcd - didžiausias bendras daliklis
 * x ir y - koeficientai Bezout tapatybei:
 *
 * a*x + b*y = gcd(a, b)
 */

//Galima būtų grąžinti masyvą, pvz. BigInteger[], bet tai neaišku kas yra [0], [1], [2]
public class ExtendedGcdResult {

    private final BigInteger gcd;
    private final BigInteger x;
    private final BigInteger y;

    /**
     * Sukuria naują išplėstinio Euklido algoritmo rezultatą.
     *
     * @param gcd didžiausias bendras daliklis
     * @param x pirmasis koeficientas
     * @param y antrasis koeficientas
     */
    public ExtendedGcdResult(BigInteger gcd, BigInteger x, BigInteger y) {
        this.gcd = gcd;
        this.x = x;
        this.y = y;
    }

    /**
     * @return didžiausias bendras daliklis
     */
    public BigInteger getGcd() {
        return gcd;
    }

    /**
     * @return Bezout koeficientas x
     */
    public BigInteger getX() {
        return x;
    }

    /**
     * @return Bezout koeficientas y
     */
    public BigInteger getY() {
        return y;
    }
}