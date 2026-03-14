package lt.viko.eif.kskrebe.rsa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lt.viko.eif.kskrebe.rsa.model.PrimeFactors;
import lt.viko.eif.kskrebe.rsa.model.RsaKeyPair;
import lt.viko.eif.kskrebe.rsa.service.MathService;
import lt.viko.eif.kskrebe.rsa.service.RsaService;
import lt.viko.eif.kskrebe.rsa.util.AlertUtil;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JavaFX kontroleris RSA projektui.
 *
 * Ši klasė valdo vartotojo sąsajos veiksmus ir sujungia UI su RSA logika.
 */
public class RsaController {

    private final RsaService rsaService = new RsaService();
    private final MathService mathService = new MathService();

    private RsaKeyPair currentKeyPair;
    private List<BigInteger> currentEncryptedValues;

    @FXML
    private TextField pField;

    @FXML
    private TextField qField;

    @FXML
    private TextArea plainTextArea;

    @FXML
    private TextArea encryptedTextArea;

    @FXML
    private TextField nField;

    @FXML
    private TextField phiField;

    @FXML
    private TextField eField;

    @FXML
    private TextField dField;

    @FXML
    private TextField attackNField;

    @FXML
    private TextField attackEField;

    @FXML
    private TextField fileStatusField;

    @FXML
    private TextArea loadedEncryptedTextArea;

    @FXML
    private TextArea decryptedTextArea;

    @FXML
    private TextField recoveredPField;

    @FXML
    private TextField recoveredQField;

    @FXML
    private TextField recoveredPhiField;

    @FXML
    private TextField recoveredDField;

    @FXML
    private TextArea attackDecryptedTextArea;

    /**
     * Sugeneruoja RSA raktus pagal vartotojo įvestus p ir q,
     * užšifruoja įvestą tekstą ir atvaizduoja rezultatą formoje.
     */
    @FXML
    private void handleGenerateKeysAndEncrypt() {
        try {
            String pText = pField.getText().trim();
            String qText = qField.getText().trim();
            String plainText = plainTextArea.getText();

            if (pText.isEmpty()) {
                AlertUtil.showWarning(
                        "Trūksta duomenų",
                        "Neįvestas p",
                        "Įveskite pirminį skaičių p."
                );
                return;
            }

            if (qText.isEmpty()) {
                AlertUtil.showWarning(
                        "Trūksta duomenų",
                        "Neįvestas q",
                        "Įveskite pirminį skaičių q."
                );
                return;
            }

            if (plainText == null || plainText.isBlank()) {
                AlertUtil.showWarning(
                        "Trūksta duomenų",
                        "Neįvestas tekstas",
                        "Įveskite pradinį tekstą, kurį norite užšifruoti."
                );
                return;
            }

            BigInteger p = new BigInteger(pText);
            BigInteger q = new BigInteger(qText);

            currentKeyPair = rsaService.generateKeys(p, q);
            currentEncryptedValues = rsaService.encrypt(
                    plainText,
                    currentKeyPair.getE(),
                    currentKeyPair.getN()
            );

            nField.setText(currentKeyPair.getN().toString());
            phiField.setText(currentKeyPair.getPhi().toString());
            eField.setText(currentKeyPair.getE().toString());
            dField.setText(currentKeyPair.getD().toString());

            encryptedTextArea.setText(formatEncryptedValues(currentEncryptedValues));

            attackNField.setText(currentKeyPair.getN().toString());
            attackEField.setText(currentKeyPair.getE().toString());

            fileStatusField.setText("Užšifruota, bet dar neišsaugota į failą.");

            decryptedTextArea.clear();
            loadedEncryptedTextArea.clear();
            recoveredPField.clear();
            recoveredQField.clear();
            recoveredPhiField.clear();
            recoveredDField.clear();
            attackDecryptedTextArea.clear();

        } catch (NumberFormatException exception) {
            clearGeneratedFields();

            AlertUtil.showError(
                    "Netinkama įvestis",
                    "Blogas skaičiaus formatas",
                    "Laukai p ir q turi būti sveikieji skaičiai."
            );

        } catch (IllegalArgumentException exception) {
            clearGeneratedFields();

            AlertUtil.showError(
                    "Klaida generuojant arba šifruojant",
                    "Nepavyko apdoroti duomenų",
                    exception.getMessage()
            );

        } catch (Exception exception) {
            clearGeneratedFields();

            AlertUtil.showError(
                    "Nenumatyta klaida",
                    "Įvyko netikėta klaida",
                    exception.getMessage()
            );
        }
    }

    /**
     * Paverčia užšifruotų skaičių sąrašą į tekstinį vaizdą,
     * tinkamą parodyti TextArea lauke.
     *
     * @param encryptedValues užšifruotų reikšmių sąrašas
     * @return reikšmės, atskirtos kableliais
     */
    private String formatEncryptedValues(List<BigInteger> encryptedValues) {
        return encryptedValues.stream()
                .map(BigInteger::toString)
                .collect(Collectors.joining(", "));
    }

    /**
     * Išvalo sugeneruotų rezultatų laukus.
     */
    private void clearGeneratedFields() {
        currentKeyPair = null;
        currentEncryptedValues = null;

        nField.clear();
        phiField.clear();
        eField.clear();
        dField.clear();
        encryptedTextArea.clear();
        attackNField.clear();
        attackEField.clear();
        fileStatusField.clear();
    }

    /**
     * Vykdo matematinę RSA ataką:
     * iš viešo rakto (n, e) bando atkurti p, q, phi ir d.
     */
    @FXML
    private void handleRecoverPrivateKey() {
        try {
            String nText = attackNField.getText().trim();
            String eText = attackEField.getText().trim();

            if (nText.isEmpty()) {
                AlertUtil.showWarning(
                        "Trūksta duomenų",
                        "Neįvestas modulis n",
                        "Viešasis modulis n turi būti užpildytas."
                );
                return;
            }

            if (eText.isEmpty()) {
                AlertUtil.showWarning(
                        "Trūksta duomenų",
                        "Neįvestas viešasis eksponentas e",
                        "Viešasis eksponentas e turi būti užpildytas."
                );
                return;
            }

            BigInteger n = new BigInteger(nText);
            BigInteger e = new BigInteger(eText);

            PrimeFactors factors = mathService.factorizeN(n);

            BigInteger p = factors.getP();
            BigInteger q = factors.getQ();
            BigInteger phi = mathService.recoverPhi(p, q);
            BigInteger d = mathService.findPrivateExponent(e, phi);

            recoveredPField.setText(p.toString());
            recoveredQField.setText(q.toString());
            recoveredPhiField.setText(phi.toString());
            recoveredDField.setText(d.toString());

            AlertUtil.showInformation(
                    "Matematinė RSA ataka",
                    "Privatus raktas atkurtas",
                    "Iš viešo rakto sėkmingai atkurti p, q, Φ ir d."
            );

        } catch (NumberFormatException exception) {
            clearAttackFields();

            AlertUtil.showError(
                    "Netinkama įvestis",
                    "Blogas skaičiaus formatas",
                    "Laukai n ir e turi būti sveikieji skaičiai."
            );

        } catch (IllegalArgumentException exception) {
            clearAttackFields();

            AlertUtil.showError(
                    "Atakos klaida",
                    "Nepavyko apdoroti duomenų",
                    exception.getMessage()
            );

        } catch (IllegalStateException exception) {
            clearAttackFields();

            AlertUtil.showError(
                    "Atakos klaida",
                    "Nepavyko atkurti privataus rakto",
                    exception.getMessage()
            );

        } catch (Exception exception) {
            clearAttackFields();

            AlertUtil.showError(
                    "Nenumatyta klaida",
                    "Įvyko netikėta klaida",
                    exception.getMessage()
            );
        }
    }

    /**
     * Išvalo matematinės atakos rezultatų laukus.
     */
    private void clearAttackFields() {
        recoveredPField.clear();
        recoveredQField.clear();
        recoveredPhiField.clear();
        recoveredDField.clear();
        attackDecryptedTextArea.clear();
    }
}