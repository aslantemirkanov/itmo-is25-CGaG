package ru.squad1332.cg.controllers;

import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.PicturePNM;
import ru.squad1332.cg.parsers.Parser;
import ru.squad1332.cg.services.PictureService;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PictureController {
    PictureService pictureService;
    public PicturePNM openPicture(String filePath) throws IOException {
        pictureService = new PictureService();
        return pictureService.openPicture(filePath);
    }

    public void cancelPicture() {
        pictureService.cancelPicture();
    }
}
