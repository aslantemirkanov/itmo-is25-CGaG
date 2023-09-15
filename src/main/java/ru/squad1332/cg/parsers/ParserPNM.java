package ru.squad1332.cg.parsers;

import ru.squad1332.cg.entities.PicturePNM;

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
            picture.setPath(path);
            //Получение формата
            picture.setFormatType(bufferedReader.readLine().trim());

            //Получение размеров изображения
            String[] dimensions = bufferedReader.readLine().trim().split("\\s+");
            picture.setWidth(Integer.parseInt(dimensions[0]));
            picture.setHeight(Integer.parseInt(dimensions[1]));
            //Получение максимального значения цвета
            picture.setMaxColorValue(Integer.parseInt(bufferedReader.readLine().trim()));

            //Пропуск метаинформации
            int newLineCounter = 0;
            char curChar;
            while (newLineCounter < 3) {
                curChar = (char) reader.read();
                if (curChar == '\n') {
                    newLineCounter++;
                }
            }

            int bufferSize = 8193;
            byte[] buffer = new byte[bufferSize];
            int offset = 0;
            int byteCount = 0;

            int[] pixelData = new int[picture.getHeight() * picture.getWidth()];
            int h = 0;
            int w = 0;
            int p = 0;
            if (picture.getFormatType().equals("P6")) {
                while ((byteCount = (reader.read(buffer, offset, bufferSize))) != -1) {
                    for (int b = 0; b < byteCount; b += 3) {
                        int r = ((buffer[b] < 0) ? 256 + buffer[b] : buffer[b]);
                        int g = ((buffer[b + 1] < 0) ? 256 + buffer[b + 1] : buffer[b + 1]);
                        int bl = ((buffer[b + 2] < 0) ? 256 + buffer[b + 2] : buffer[b + 2]);
                        int alpha = 255;
                        pixelData[p++] = (alpha << 24) + (r << 16) + (g << 8) + (bl);
                    }
                }
            }

            if (picture.getFormatType().equals("P5")) {
                while ((byteCount = (reader.read(buffer, offset, bufferSize))) != -1) {
                    for (int b = 0; b < byteCount; b++) {
                        int r = ((buffer[b] < 0) ? 256 + buffer[b] : buffer[b]);
                        int alpha = 255;
                        pixelData[p++] = (alpha << 24) + (r << 16) + (r << 8) + (r);
                    }
                }
            }

            picture.setPixelData(pixelData);
            return picture;
        } catch (IOException e) {
            throw e;
        }

    }
}
