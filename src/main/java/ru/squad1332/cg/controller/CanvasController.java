package ru.squad1332.cg.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.File;

public class CanvasController {

    @FXML
    private Label filename;
    private File file;


    public void setFile(File file) {
        this.file = file;
        this.filename = new Label();
        this.filename.setText(this.file.getName());
    }
//
//    @FXML
//    private void receiveData(MouseEvent event) {
//        this.filename.setText(this.file.getName());
//    }
}
