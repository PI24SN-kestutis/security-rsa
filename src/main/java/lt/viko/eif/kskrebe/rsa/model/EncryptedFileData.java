package lt.viko.eif.kskrebe.rsa.model;

import java.math.BigInteger;
import java.util.List;

/**
 * Užšifruotų duomenų modelis failų saugojimui ir nuskaitymui.
 *
 * Saugo:
 * - viešąjį modulį n
 * - viešąjį eksponentą e
 * - užšifruotų reikšmių sąrašą
 */
public class EncryptedFileData {

    private final BigInteger n;
    private final BigInteger e;
    private final List<BigInteger> cipherValues;

    /**
     * Sukuria naują užšifruotų failo duomenų objektą.
     *
     * @param n viešasis modulis
     * @param e viešasis eksponentas
     * @param cipherValues užšifruotų reikšmių sąrašas
     */
    public EncryptedFileData(BigInteger n, BigInteger e, List<BigInteger> cipherValues) {
        this.n = n;
        this.e = e;
        this.cipherValues = cipherValues;
    }

    /**
     * @return viešasis modulis n
     */
    public BigInteger getN() {
        return n;
    }

    /**
     * @return viešasis eksponentas e
     */
    public BigInteger getE() {
        return e;
    }

    /**
     * @return užšifruotų reikšmių sąrašas
     */
    public List<BigInteger> getCipherValues() {
        return cipherValues;
    }
}