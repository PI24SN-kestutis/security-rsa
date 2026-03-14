package lt.viko.eif.kskrebe.rsa.util;

import javafx.scene.control.Alert;

/**
 * Pagalbinė klasė pranešimų langams rodyti.
 */
public final class AlertUtil {

    private AlertUtil() {
    }

    /**
     * Parodo informacinį pranešimą.
     *
     * @param title lango pavadinimas
     * @param header antraštė
     * @param content pranešimo turinys
     */
    public static void showInformation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Parodo klaidos pranešimą.
     *
     * @param title lango pavadinimas
     * @param header antraštė
     * @param content pranešimo turinys
     */
    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Parodo įspėjimo pranešimą.
     *
     * @param title lango pavadinimas
     * @param header antraštė
     * @param content pranešimo turinys
     */
    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}