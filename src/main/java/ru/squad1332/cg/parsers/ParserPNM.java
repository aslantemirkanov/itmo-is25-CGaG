package ru.squad1332.cg.parsers;

import ru.squad1332.cg.entities.PicturePNM;
import ru.squad1332.cg.entities.Pixel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

public class ParserPNM implements Parser {
    @Override
    public PicturePNM parse(String path) throws IOException {
        try (FileInputStream reader = new FileInputStream(path);
             BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            PicturePNM picture = new PicturePNM();
            picture.setFormatType(bufferedReader.readLine().trim());


            String[] dimensions = bufferedReader.readLine().trim().split("\\s+");
            picture.setWidth(Integer.parseInt(dimensions[0]));
            picture.setHeight(Integer.parseInt(dimensions[1]));
            picture.setMaxColorValue(Integer.parseInt(bufferedReader.readLine().trim()));

            int newLineCounter = 0;
            char curChar;
            while (newLineCounter < 3) {
                curChar = (char) reader.read();
                if (curChar == '\n') {
                    newLineCounter++;
                }
            }

            int bufferSize = 1026;
            byte[] buffer = new byte[bufferSize];
            int offset = 0;
            int byteCount = 0;

            Pixel[][] pixelData = new Pixel[picture.getHeight()][picture.getWidth()];
            int h = 0;
            int w = 0;
            int cnt = 0;
            while ((byteCount = (reader.read(buffer, offset, bufferSize))) != -1) {
                cnt += byteCount;
//                System.out.println(cnt);
                for (int b = 0; b < byteCount; b += 3) {
                    int red = buffer[b];
                    int green = buffer[b + 1];
                    int blue = buffer[b + 2];
                    pixelData[h][w++] = new Pixel(red, green, blue);
                    if (w % picture.getWidth() == 0) {
                        h++;
                        w = 0;
                    }
                }
            }
            System.out.println(h + " " + w);

            picture.setPixelData(pixelData);
            System.out.println(bufferSize);
            System.out.println(offset);
            return picture;
        } catch (IOException e) {
            throw e;
        }

    }
}
