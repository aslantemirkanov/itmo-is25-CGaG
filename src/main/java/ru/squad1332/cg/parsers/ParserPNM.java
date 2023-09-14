package ru.squad1332.cg.parsers;

import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.PicturePNM;
import ru.squad1332.cg.entities.Pixel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ParserPNM implements Parser {
    @Override
    public Picture parse(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

            PicturePNM picture = new PicturePNM();
            picture.setFormatType(reader.readLine().trim());


            String[] dimensions = reader.readLine().trim().split("\\s+");
            picture.setWidth(Integer.parseInt(dimensions[0]));
            picture.setHeight(Integer.parseInt(dimensions[1]));
            picture.setMaxColorValue(Integer.parseInt(reader.readLine().trim()));

            char[] buffer = new char[1026];
            int bufferSize = 1026;
            int offset = 0;
            int byteCount = 0;

            Pixel[][] pixelData = new Pixel[picture.getHeight()][picture.getWidth()];
            int h = 0;
            int w = 0;
            while ((byteCount = (reader.read(buffer, offset, bufferSize))) != -1) {
                for (int b = 0; b < byteCount; b += 3) {
                    int red = buffer[b];
                    int green = buffer[b + 1];
                    int blue = buffer[b + 2];
                    pixelData[((w + 1 >= picture.getWidth()) ? h++ : h) % picture.getHeight()][(w++) % picture.getWidth()] = new Pixel(red, green, blue);
                }
            }

            picture.setPixelData(pixelData);
            System.out.println(bufferSize);
            System.out.println(offset);
            return picture;
        } catch (IOException e) {
            throw e;
        }

    }
}
