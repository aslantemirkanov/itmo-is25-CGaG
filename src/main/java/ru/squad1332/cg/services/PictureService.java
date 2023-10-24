package ru.squad1332.cg.services;

import ru.squad1332.cg.convertor.ColorConvertor;
import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.PicturePNM;
import ru.squad1332.cg.entities.Pixel;
import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.Mode;
import ru.squad1332.cg.parsers.Parser;
import ru.squad1332.cg.parsers.ParserPNM;

import java.io.*;

public class PictureService {
    private Picture picture;
    private Parser parser = new ParserPNM();
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
            PicturePNM picture = parser.parse(filePath);
            return picture;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}