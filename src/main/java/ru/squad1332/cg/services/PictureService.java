package ru.squad1332.cg.services;

import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.PicturePNM;
import ru.squad1332.cg.parsers.Parser;
import ru.squad1332.cg.parsers.ParserPNG;
import ru.squad1332.cg.parsers.ParserPNM;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PictureService {
    private Picture picture;
    private Parser parserPNM = new ParserPNM();

    private Parser parserPNG = new ParserPNG();
    private String path;

    private static final int SIGNATURE_SIZE = 8;
    private static final byte[] PNG_SIGNATURE = new byte[]{(byte) 137, 80, 78, 71, 13, 10, 26, 10};

    public String getType(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String type = reader.readLine().trim();
            if (type.equals("P5") || type.equals("P6")) {
                parserPNM = new ParserPNM();
                return "PNM";
            } else {
                try (FileInputStream fis = new FileInputStream(filePath)) {
                    byte[] signature = new byte[SIGNATURE_SIZE];
                    if (fis.read(signature) != SIGNATURE_SIZE || !Arrays.equals(signature, PNG_SIGNATURE)) {
                        throw new IOException("Not a valid PNG file.");
                    } else {
                        parserPNG = new ParserPNG();
                        return "PNG";
                    }
                }
            }
        }
    }

    public Picture openPicture(String filePath) throws IOException {
        try {
            String fileFormat = getType(filePath);
            if (fileFormat.equals("PNM")){
                return parserPNM.parse(filePath);
            } else if (fileFormat.equals("PNG")){
                System.out.println("AAAAAAAAAAAAAAAAAAA!!!!!!!!!!!!");
                return parserPNG.parse(filePath);
            } else {
                throw new IOException();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}