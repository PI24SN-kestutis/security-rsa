package lt.viko.eif.kskrebe.rsa.model;

import java.math.BigInteger;

/**
 * Dviejų n faktorių rezultatas.
 *
 * Naudojama matematinės atakos metu,
 * kai iš n bandoma rasti p ir q.
 */
public class PrimeFactors {

    private final BigInteger p;
    private final BigInteger q;

    /**
     * Sukuria naują faktorių porą.
     *
     * @param p pirmas faktorius
     * @param q antras faktorius
     */
    public PrimeFactors(BigInteger p, BigInteger q) {
        this.p = p;
        this.q = q;
    }

    /**
     * @return pirmas faktorius
     */
    public BigInteger getP() {
        return p;
    }

    /**
     * @return antras faktorius
     */
    public BigInteger getQ() {
        return q;
    }
}