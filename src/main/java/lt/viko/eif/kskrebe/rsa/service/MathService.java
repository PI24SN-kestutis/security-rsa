package lt.viko.eif.kskrebe.rsa.service;

import java.math.BigInteger;
import lt.viko.eif.kskrebe.rsa.model.ExtendedGcdResult;
import lt.viko.eif.kskrebe.rsa.model.PrimeFactors;

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

    /**
     * Apskaičiuoja išplėstinio Euklido algoritmo rezultatą.
     *
     * <p>Grąžina ne tik didžiausią bendrą daliklį, bet ir
     * koeficientus x ir y, kad galiotų Bezout tapatybė:</p>
     *
     * <pre>
     * a*x + b*y = gcd(a, b)
     * </pre>
     *
     * <p>Šis metodas RSA algoritme naudojamas modulinio inverso
     * radimui, kai reikia apskaičiuoti privatų eksponentą d.</p>
     *
     * @param a pirmas skaičius
     * @param b antras skaičius
     * @return objektas, kuriame yra gcd, x ir y
     */
    public ExtendedGcdResult extendedGcd(BigInteger a, BigInteger b) {
        //Kai b = 0, turime: a*1+0*0=a
        if (b.equals(BigInteger.ZERO)) {
            return new ExtendedGcdResult(a, BigInteger.ONE, BigInteger.ZERO);
        }
        //pagal formulę: extendedGcd(a,b)→extendedGcd(b,a mod b)
        ExtendedGcdResult nextResult = extendedGcd(b, a.mod(b));

        BigInteger gcd = nextResult.getGcd();
        BigInteger x1 = nextResult.getX();
        BigInteger y1 = nextResult.getY();

        BigInteger x = y1;
        BigInteger y = x1.subtract(a.divide(b).multiply(y1));

        return new ExtendedGcdResult(gcd, x, y);
    }


    /**
     * Suranda tinkamą viešąjį eksponentą e RSA algoritmui.
     *
     * <p>Viešasis eksponentas turi tenkinti šias sąlygas:</p>
     * <ul>
     *     <li>1 < e < phi</li>
     *     <li>gcd(e, phi) = 1</li>
     * </ul>
     *
     * <p>Metodas pradeda paiešką nuo 3 ir nuosekliai ieško
     * pirmo tinkamo skaičiaus.</p>
     *
     * @param phi Euler funkcijos reikšmė
     * @return tinkamas viešasis eksponentas e
     * @throws IllegalArgumentException jei phi yra per mažas
     * @throws IllegalStateException jei nepavyksta rasti tinkamo e
     */
    public BigInteger findPublicExponent(BigInteger phi) {
        if (phi.compareTo(BigInteger.TWO) <= 0) {
            throw new IllegalArgumentException("Phi turi būti didesnis už 2.");
        }

        BigInteger e = BigInteger.valueOf(3);

        while (e.compareTo(phi) < 0) {
            if (gcd(e, phi).equals(BigInteger.ONE)) {
                return e;
            }

            e = e.add(BigInteger.ONE);
        }

        throw new IllegalStateException("Nepavyko rasti galiojančio viešojo laipsnio rodiklio e.");
    }

    /**
     * Apskaičiuoja privačią eksponentę d RSA algoritmui.
     *
     * <p>Privati eksponentė tai:</p>
     *
     * <pre>
     * d * e ≡ 1 (mod phi)
     * </pre>
     *
     * <p>Tai reiškia, kad d yra modulinis inversas skaičiui e
     * pagal modulį phi.</p>
     *
     * <p>Šis metodas naudoja išplėstinį Euklido algoritmą
     * modulinio inverso radimui.</p>
     *
     * @param e viešoji eksponentė
     * @param phi Euler funkcijos reikšmė
     * @return privati eksponentė d
     * @throws IllegalArgumentException jei e ir phi nėra tarpusavyje pirminiai
     */
    public BigInteger findPrivateExponent(BigInteger e, BigInteger phi) {

        //ar gcd(e,phi)=1
        if (!gcd(e, phi).equals(BigInteger.ONE)) {
            throw new IllegalArgumentException("e ir phi turi būti bendri pirminiai skaičiai.");
        }

        //Paleidžiame išplėstinį Euklido algoritmą.
        ExtendedGcdResult result = extendedGcd(e, phi);

        //x yra Bezout koeficientas.
        BigInteger x = result.getX();

        //d=x mod phi
        return x.mod(phi);
    }

    /**
     * Bando išskaidyti n į du pirminius faktorius p ir q.
     *
     * <p>Šis metodas naudoja paprastą trial division principą:
     * tikrina daliklius nuo 2 iki √n.</p>
     *
     * <p>Metodas skirtas mokomajai RSA atakai, kai n yra pakankamai mažas,
     * kad faktorizacija būtų praktiškai įmanoma.</p>
     *
     * @param n RSA modulis
     * @return rasti pirminiai faktoriai p ir q
     * @throws IllegalArgumentException jei n yra per mažas
     * @throws IllegalStateException jei nepavyksta rasti dviejų pirminių faktorių
     */
    public PrimeFactors factorizeN(BigInteger n) {
        if (n.compareTo(BigInteger.TWO) <= 0) {
            throw new IllegalArgumentException("turi būti didesnis už 2.");
        }

        BigInteger divisor = BigInteger.TWO;

        while (divisor.multiply(divisor).compareTo(n) <= 0) {
            if (n.mod(divisor).equals(BigInteger.ZERO)) {
                BigInteger p = divisor;
                BigInteger q = n.divide(divisor);

                if (isPrime(p) && isPrime(q)) {
                    return new PrimeFactors(p, q);
                }
            }

            divisor = divisor.add(BigInteger.ONE);
        }

        throw new IllegalStateException("Nepavyko suskaidyti n į du pirminius veiksnius.");
    }
}