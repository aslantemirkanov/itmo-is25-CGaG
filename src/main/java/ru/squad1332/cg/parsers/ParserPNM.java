package ru.squad1332.cg.parsers;

import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.PicturePNM;
import ru.squad1332.cg.entities.Pixel;

import java.io.*;

public class ParserPNM implements Parser {
    @Override
    public Picture parse(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        char[] buf = new char[1026];
        reader.read(buf);
        int bufSize = 1026;
        int offset=0;
        int byteCount = 0;
        while ((byteCount = (reader.read(buf, offset, bufSize))) != -1){

        }
        PicturePNM picture = new PicturePNM();

        // Прочитать заголовок (P1, P2, P3 или P4)
        picture.setFormatType(reader.readLine().trim());

        // Пропустить комментарии
        String line = reader.readLine();

        // Прочитать ширину и высоту
        String[] dimensions = line.trim().split("\\s+");
        picture.setWidth(Integer.parseInt(dimensions[0]));
        picture.setHeight(Integer.parseInt(dimensions[1]));
        picture.setMaxColorValue(Integer.parseInt(reader.readLine().trim()));

        String byteData = reader.readLine();
        System.out.println(byteData.length());
        // Прочитать данные пикселей
        Pixel[][] pixelData = new Pixel[picture.getHeight()][picture.getWidth()];

        for (int i = 0; i < picture.getHeight(); ++i){
            for (int j = 0; j < picture.getWidth() * 3 - 1; j += 3){
                int red = (int)(byteData.charAt(i * picture.getWidth() + j));
                int green = (int)(byteData.charAt(i * picture.getWidth() + j + 1));
                int blue = (int)(byteData.charAt(i * picture.getWidth() + j + 2));
                pixelData[i][j/3] = (new Pixel(red, green, blue));
            }
        }

        if (picture.getFormatType().equals("P5")) {
            for (int i = 0; i < picture.getHeight(); ++i) {
                for (int j = 0; j < picture.getWidth(); ++j) {
                    Pixel newPixel = new Pixel(reader.read(), 0, 0);
                    pixelData[i][j] = newPixel;
                }
            }
        }

        if (picture.getFormatType().equals("P6")) {
            for (int i = 0; i < picture.getHeight(); ++i) {
                for (int j = 0; j < picture.getWidth(); ++j) {
                    Pixel newPixel = new Pixel(reader.read(), reader.read(), reader.read());
                    pixelData[i][j] = newPixel;
                }
            }
        }



        picture.setPixelData(pixelData);
        reader.close();

        return new PicturePNM();
    }
}
