package lt.viko.eif.kskrebe.rsa.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lt.viko.eif.kskrebe.rsa.model.EncryptedFileData;
import lt.viko.eif.kskrebe.rsa.model.PrimeFactors;
import lt.viko.eif.kskrebe.rsa.model.RsaKeyPair;
import lt.viko.eif.kskrebe.rsa.service.FileService;
import lt.viko.eif.kskrebe.rsa.service.MathService;
import lt.viko.eif.kskrebe.rsa.service.RsaService;
import lt.viko.eif.kskrebe.rsa.util.AlertUtil;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
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
    private final FileService fileService = new FileService();

    private RsaKeyPair currentKeyPair;
    private List<BigInteger> currentEncryptedValues;
    private EncryptedFileData loadedFileData;

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

    @FXML
    private Button saveButton;

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

            loadedFileData = null;

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
     * Dešifruoja tekstą naudodamas matematinės atakos metu atkurtą privatų raktą.
     */
    @FXML
    private void handleAttackDecrypt() {
        try {
            String nText = attackNField.getText().trim();
            String dText = recoveredDField.getText().trim();

            if (nText.isEmpty()) {
                AlertUtil.showWarning(
                        "Trūksta duomenų",
                        "Neįvestas modulis n",
                        "Viešasis modulis n turi būti užpildytas."
                );
                return;
            }

            if (dText.isEmpty()) {
                AlertUtil.showWarning(
                        "Trūksta duomenų",
                        "Neatkurtas privatus raktas d",
                        "Pirmiausia atkurkite privatų raktą."
                );
                return;
            }

            List<BigInteger> cipherValues;

            if (currentEncryptedValues != null && !currentEncryptedValues.isEmpty()) {
                cipherValues = currentEncryptedValues;
            } else {
                String loadedCipherText = loadedEncryptedTextArea.getText().trim();

                if (loadedCipherText.isEmpty()) {
                    AlertUtil.showWarning(
                            "Trūksta duomenų",
                            "Nėra užšifruoto teksto",
                            "Pirmiausia užšifruokite tekstą arba įkelkite užšifruotą failą."
                    );
                    return;
                }

                cipherValues = parseEncryptedValues(loadedCipherText);
            }

            BigInteger n = new BigInteger(nText);
            BigInteger d = new BigInteger(dText);

            String decryptedText = rsaService.decrypt(cipherValues, d, n);

            attackDecryptedTextArea.setText(decryptedText);

            AlertUtil.showInformation(
                    "Matematinė RSA ataka",
                    "Tekstas atkurtas",
                    "Tekstas sėkmingai dešifruotas naudojant atkurtą privatų raktą."
            );

        } catch (NumberFormatException exception) {
            attackDecryptedTextArea.clear();

            AlertUtil.showError(
                    "Netinkama įvestis",
                    "Blogas skaičiaus formatas",
                    "Nepavyko perskaityti užšifruotų reikšmių arba rakto."
            );

        } catch (IllegalArgumentException exception) {
            attackDecryptedTextArea.clear();

            AlertUtil.showError(
                    "Dešifravimo klaida",
                    "Nepavyko dešifruoti teksto",
                    exception.getMessage()
            );

        } catch (Exception exception) {
            attackDecryptedTextArea.clear();

            AlertUtil.showError(
                    "Nenumatyta klaida",
                    "Įvyko netikėta klaida",
                    exception.getMessage()
            );
        }
    }

    /**
     * Išsaugo užšifruotus duomenis į failą.
     *
     * Faile viena eilute saugoma:
     * n;e;cipher1;cipher2;cipher3;...
     */
    @FXML
    private void handleSaveEncryptedData() {
        try {
            if (currentKeyPair == null || currentEncryptedValues == null || currentEncryptedValues.isEmpty()) {
                AlertUtil.showWarning(
                        "Nėra duomenų",
                        "Nėra ką išsaugoti",
                        "Pirmiausia sugeneruokite raktus ir užšifruokite tekstą."
                );
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Išsaugoti užšifruotus duomenis");
            fileChooser.setInitialFileName("rsa-duomenys.txt");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Tekstiniai failai (*.txt)", "*.txt")
            );

            Window window = saveButton.getScene().getWindow();
            File selectedFile = fileChooser.showSaveDialog(window);

            if (selectedFile == null) {
                fileStatusField.setText("Išsaugojimas atšauktas.");
                return;
            }

            EncryptedFileData data = new EncryptedFileData(
                    currentKeyPair.getN(),
                    currentKeyPair.getE(),
                    currentEncryptedValues
            );

            Path filePath = selectedFile.toPath();
            fileService.saveEncryptedData(filePath, data);

            fileStatusField.setText("Duomenys išsaugoti: " + filePath.toAbsolutePath());

            AlertUtil.showInformation(
                    "Išsaugojimas sėkmingas",
                    "Duomenys išsaugoti",
                    "Užšifruoti duomenys sėkmingai įrašyti į failą."
            );

        } catch (IllegalArgumentException exception) {
            fileStatusField.setText("Klaida išsaugant duomenis.");

            AlertUtil.showError(
                    "Išsaugojimo klaida",
                    "Nepavyko išsaugoti duomenų",
                    exception.getMessage()
            );

        } catch (IOException exception) {
            fileStatusField.setText("Klaida rašant failą.");

            AlertUtil.showError(
                    "Failo įrašymo klaida",
                    "Nepavyko įrašyti failo",
                    exception.getMessage()
            );

        } catch (Exception exception) {
            fileStatusField.setText("Nenumatyta klaida išsaugant duomenis.");

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
     * Paverčia tekstinį užšifruotų reikšmių vaizdą į BigInteger sąrašą.
     *
     * Palaikomi skyrikliai:
     * - kabliataškis ;
     * - kablelis ,
     *
     * @param text tekstas su užšifruotomis reikšmėmis
     * @return užšifruotų reikšmių sąrašas
     */
    private List<BigInteger> parseEncryptedValues(String text) {
        return List.of(text.split("[;,]"))
                .stream()
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .map(BigInteger::new)
                .collect(Collectors.toList());
    }

    /**
     * Išvalo sugeneruotų rezultatų laukus.
     */
    private void clearGeneratedFields() {
        currentKeyPair = null;
        currentEncryptedValues = null;
        loadedFileData = null;

        nField.clear();
        phiField.clear();
        eField.clear();
        dField.clear();
        encryptedTextArea.clear();
        attackNField.clear();
        attackEField.clear();
        fileStatusField.clear();
        loadedEncryptedTextArea.clear();
        decryptedTextArea.clear();
        clearAttackFields();
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

    /**
     * Nuskaito užšifruotus duomenis iš failo ir atvaizduoja juos formoje.
     */
    @FXML
    private void handleLoadEncryptedData() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pasirinkti užšifruotą failą");

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Tekstiniai failai (*.txt)", "*.txt")
            );

            Window window = saveButton.getScene().getWindow();
            File selectedFile = fileChooser.showOpenDialog(window);

            if (selectedFile == null) {
                fileStatusField.setText("Failo pasirinkimas atšauktas.");
                return;
            }

            Path filePath = selectedFile.toPath();
            loadedFileData = fileService.readEncryptedData(filePath);

            currentEncryptedValues = loadedFileData.getCipherValues();

            loadedEncryptedTextArea.setText(formatEncryptedValues(loadedFileData.getCipherValues()));
            attackNField.setText(loadedFileData.getN().toString());
            attackEField.setText(loadedFileData.getE().toString());

            fileStatusField.setText("Failas sėkmingai nuskaitytas: " + filePath.toAbsolutePath());

            decryptedTextArea.clear();
            clearAttackFields();

            AlertUtil.showInformation(
                    "Failas nuskaitytas",
                    "Duomenys įkelti",
                    "Užšifruoti duomenys sėkmingai nuskaityti iš failo."
            );

        } catch (IllegalArgumentException exception) {
            loadedFileData = null;
            currentEncryptedValues = null;
            loadedEncryptedTextArea.clear();
            attackNField.clear();
            attackEField.clear();
            decryptedTextArea.clear();
            clearAttackFields();
            fileStatusField.setText("Neteisingas failo formatas.");

            AlertUtil.showError(
                    "Nuskaitymo klaida",
                    "Nepavyko perskaityti failo",
                    exception.getMessage()
            );

        } catch (IOException exception) {
            loadedFileData = null;
            currentEncryptedValues = null;
            loadedEncryptedTextArea.clear();
            attackNField.clear();
            attackEField.clear();
            decryptedTextArea.clear();
            clearAttackFields();
            fileStatusField.setText("Klaida skaitant failą.");

            AlertUtil.showError(
                    "Failo skaitymo klaida",
                    "Nepavyko nuskaityti failo",
                    exception.getMessage()
            );

        } catch (Exception exception) {
            loadedFileData = null;
            currentEncryptedValues = null;
            loadedEncryptedTextArea.clear();
            attackNField.clear();
            attackEField.clear();
            decryptedTextArea.clear();
            clearAttackFields();
            fileStatusField.setText("Nenumatyta klaida nuskaitant failą.");

            AlertUtil.showError(
                    "Nenumatyta klaida",
                    "Įvyko netikėta klaida",
                    exception.getMessage()
            );
        }
    }

    /**
     * Dešifruoja tekstą iš nuskaityto failo, atkuriant privatų raktą
     * per matematinę RSA ataką.
     */
    @FXML
    private void handleDecrypt() {
        try {
            if (loadedFileData == null) {
                AlertUtil.showWarning(
                        "Nėra duomenų",
                        "Failas neįkeltas",
                        "Pirmiausia pasirinkite užšifruotą failą."
                );
                return;
            }

            BigInteger n = loadedFileData.getN();
            BigInteger e = loadedFileData.getE();
            List<BigInteger> cipherValues = loadedFileData.getCipherValues();

            PrimeFactors factors = mathService.factorizeN(n);

            BigInteger p = factors.getP();
            BigInteger q = factors.getQ();
            BigInteger phi = mathService.recoverPhi(p, q);
            BigInteger d = mathService.findPrivateExponent(e, phi);

            String decryptedText = rsaService.decrypt(cipherValues, d, n);

            decryptedTextArea.setText(decryptedText);

            recoveredPField.setText(p.toString());
            recoveredQField.setText(q.toString());
            recoveredPhiField.setText(phi.toString());
            recoveredDField.setText(d.toString());

            attackNField.setText(n.toString());
            attackEField.setText(e.toString());

            AlertUtil.showInformation(
                    "Dešifravimas sėkmingas",
                    "Tekstas atkurtas",
                    "Užšifruotas tekstas sėkmingai dešifruotas."
            );

        } catch (IllegalArgumentException exception) {
            decryptedTextArea.clear();
            clearAttackFields();

            AlertUtil.showError(
                    "Dešifravimo klaida",
                    "Nepavyko apdoroti duomenų",
                    exception.getMessage()
            );

        } catch (IllegalStateException exception) {
            decryptedTextArea.clear();
            clearAttackFields();

            AlertUtil.showError(
                    "Matematinės atakos klaida",
                    "Nepavyko atkurti privataus rakto",
                    exception.getMessage()
            );

        } catch (Exception exception) {
            decryptedTextArea.clear();
            clearAttackFields();

            AlertUtil.showError(
                    "Nenumatyta klaida",
                    "Įvyko netikėta klaida",
                    exception.getMessage()
            );
        }
    }
}