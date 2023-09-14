package ru.squad1332.cg.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ButtonsController {
    @FXML
    private Button openButton;

    @FXML
    protected void onOpen(MouseEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
        try {
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Pnm", "*.ppm");
            FileChooser fileChooser = new FileChooser();
//            fileChooser.getExtensionFilters().setAll(extensionFilter);
            File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getRoot().getScene().getWindow());

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ru/squad1332/cg/canvas.fxml"));
            CanvasController canvasController = new CanvasController();
            canvasController.setFile(file);

            fxmlLoader.setController(canvasController);

            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println(String.format("Error: %s", e.getMessage()));
        }
    }
}
