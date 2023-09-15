package ru.squad1332.cg.services;

import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.PicturePNM;
import ru.squad1332.cg.parsers.Parser;
import ru.squad1332.cg.parsers.ParserPNM;

import java.io.*;

public class PictureService {
    private Picture picture;
    private Parser parser;
    private String path;

    private void getType(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String type = reader.readLine().trim();
            if (type.equals("P5") || type.equals("P6")) {
                parser = new ParserPNM();
            }
        }
    }

    public PicturePNM openPicture(String filePath) throws IOException {
        try {
            getType(filePath);
            return parser.parse(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void cancelPicture() {

    }
}