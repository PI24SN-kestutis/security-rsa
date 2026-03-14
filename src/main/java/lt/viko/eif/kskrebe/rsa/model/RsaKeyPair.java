package lt.viko.eif.kskrebe.rsa.model;

import java.math.BigInteger;

/**
 * RSA raktų pora.
 *
 * Saugo visus svarbius RSA parametrus:
 * p, q, n, phi, e ir d.
 */
public class RsaKeyPair {

    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger n;
    private final BigInteger phi;
    private final BigInteger e;
    private final BigInteger d;

    public RsaKeyPair(BigInteger p,
                      BigInteger q,
                      BigInteger n,
                      BigInteger phi,
                      BigInteger e,
                      BigInteger d) {

        this.p = p;
        this.q = q;
        this.n = n;
        this.phi = phi;
        this.e = e;
        this.d = d;
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getPhi() {
        return phi;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getD() {
        return d;
    }
}