package ru.squad1332.cg.parsers;

import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.PicturePNM;
import ru.squad1332.cg.entities.Pixel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import static ru.squad1332.cg.parsers.Chunk.readChunk;

public class ParserPNG implements Parser {
    private static final int SIGNATURE_SIZE = 8;
    private static final byte[] PNG_SIGNATURE = new byte[]{(byte) 137, 80, 78, 71, 13, 10, 26, 10};
    static int width;
    static int height;
    static byte bitDepth;
    static byte colorType;
    static byte compressionMethod;
    static byte filterMethod;
    static byte interlaceMethod;

    @Override
    public Picture parse(String path) throws IOException {
        File file = new File(path);
        try (FileInputStream fis = new FileInputStream(file)) {
            checkSignature(fis);

            ArrayList<Chunk> chunks = new ArrayList<>();
            ArrayList<Chunk> idatChunks = new ArrayList<>();
            byte[] data = new byte[0];

            while (true) {
                Chunk chunk = readChunk(fis);

                if ("IEND".equals(chunk.type)) {
                    System.out.println("IEND");
                    chunks.add(chunk);

                    break;
                }

                if ("IHDR".equals(chunk.type)) {
                    chunks.add(chunk);
                    System.out.println("IHDR");
                    if (chunk.data.length != 13) {
                        throw new IllegalArgumentException("Invalid IHDR data length.");
                    }

                    ByteBuffer ihdrBuffer = ByteBuffer.wrap(chunk.data).order(ByteOrder.BIG_ENDIAN);

                    width = ihdrBuffer.getInt();
                    height = ihdrBuffer.getInt();
                    bitDepth = ihdrBuffer.get();
                    colorType = ihdrBuffer.get();
                    compressionMethod = ihdrBuffer.get();
                    filterMethod = ihdrBuffer.get();
                    interlaceMethod = ihdrBuffer.get();

                } else if ("PLTE".equals(chunk.type)) {
                    chunks.add(chunk);
                    System.out.println("PLTE");
                } else if ("IDAT".equals(chunk.type)) {
                    chunks.add(chunk);
                    idatChunks.add(chunk);
                    System.out.println("IDAT");
                } else if ("sRGB".equals(chunk.type)) {
                    chunks.add(chunk);
                    System.out.println("sRGB");
                } else if ("gAMA".equals(chunk.type)) {
                    chunks.add(chunk);
                    System.out.println("gAMA");
                } else if ("pHYs".equals(chunk.type)) {
                    chunks.add(chunk);
                    System.out.println("pHYs");
                } else {
                    chunks.add(chunk);
                    System.out.println("Undefined chunk!!!");
                }
            }

            //byte[] decompressedData = decompress(data);

            byte[] decompressedData = decompress(idatChunks);
            System.out.println(decompressedData.length + " decompressed LENGHT");
            System.out.println("Wight " + width + " height " + height);
            System.out.println("pixel count " + width * height);

            Pixel[] pixelData = extractPixelData(decompressedData);

            PicturePNM picturePNM = new PicturePNM();
            picturePNM.setWidth(width);
            picturePNM.setHeight(height);
            picturePNM.setMaxColorValue(255);
            picturePNM.setFormatType("P6");
            picturePNM.setPixelData(pixelData);
            return  picturePNM;
        }

    }

    private static void checkSignature(FileInputStream fis) throws IOException {
        byte[] signature = new byte[SIGNATURE_SIZE];
        if (fis.read(signature) != SIGNATURE_SIZE || !ByteBuffer.wrap(signature).equals(ByteBuffer.wrap(PNG_SIGNATURE))) {
            throw new IOException("Not a valid PNG file.");
        }
    }

    private static byte[] concatenate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private static byte[] decompress(List<Chunk> idatChunks) throws IOException {
        // Объединяем данные всех блоков IDAT в один массив
        ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
        for (Chunk chunk : idatChunks) {
            bytesStream.write(chunk.data);
        }
        byte[] compressedData = bytesStream.toByteArray();

        InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(compressedData));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = iis.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        iis.close();
        baos.close();

        return baos.toByteArray();
    }

    private static Pixel[] extractPixelData(byte[] decompressedData) {
        Pixel[] pixels = new Pixel[width * height];

        int bytesPerPixel = calculateBytesPerPixel(colorType, bitDepth);
        int pixelIndex = 0;
        int rowStart = 1;
        for (int row = 0; row < height; row++) {
            int filterType = decompressedData[row * (width * bytesPerPixel + 1)] & 0xFF;
            System.out.println("filter " + filterType);

            for (int col = 0; col < width; col++) {
                int index = rowStart + col * bytesPerPixel;

                int red, green, blue;
                if (filterType == 0) { // None
                    red = decompressedData[index] & 0xFF;
                    green = decompressedData[index + 1] & 0xFF;
                    blue = decompressedData[index + 2] & 0xFF;
                } else {
                    red = applyFilter(decompressedData, filterType, row, col, index, 0, bytesPerPixel);
                    decompressedData[index] = (byte) red;
                    green = applyFilter(decompressedData, filterType, row, col, index, 1, bytesPerPixel);
                    decompressedData[index + 1] = (byte) green;
                    blue = applyFilter(decompressedData, filterType, row, col, index, 2, bytesPerPixel);
                    decompressedData[index + 2] = (byte) blue;
                }
                pixels[pixelIndex++] = new Pixel((double) red / 255.0, (double) green / 255.0, (double) blue / 255.0);
            }
            rowStart += (bytesPerPixel * width) + 1;
        }

        return pixels;
    }
    //TODO gamma, палитра, серые фотки, сохранение

    private static int applyFilter(byte[] data, int filterType, int row, int col, int index, int colorOffset, int bytesPerPixel) {
        int byteValue = data[index + colorOffset] & 0xFF;
        switch (filterType) {
            case 1: // Sub
                return col > 0 ? (byteValue + data[index - bytesPerPixel + colorOffset] & 0xFF) % 256 : byteValue;
            case 2: // Up
                return row > 0 ? (byteValue + data[index - (width * bytesPerPixel + 1) + colorOffset] & 0xFF) % 256 : byteValue;
            case 3: // Average
                int left = col > 0 ? data[index - bytesPerPixel + colorOffset] & 0xFF : 0;
                int up = row > 0 ? data[index - (width * bytesPerPixel + 1) + colorOffset] & 0xFF : 0;
                return (byteValue + Math.floorDiv(left + up, 2)) % 256;
            case 4: // Paeth
                int a = col > 0 ? data[index - bytesPerPixel + colorOffset] & 0xFF : 0;
                int b = row > 0 ? data[index - (width * bytesPerPixel + 1) + colorOffset] & 0xFF : 0;
                int c = (col > 0 && row > 0) ? data[index - (width * bytesPerPixel + 1) - bytesPerPixel + colorOffset] & 0xFF : 0;
                return (byteValue + paethPredictor(a, b, c)) % 256;
            default:
                return byteValue;
        }
    }

    private static int paethPredictor(int a, int b, int c) {
        int p = a + b - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);
        if (pa <= pb && pa <= pc) return a;
        else if (pb <= pc) return b;
        else return c;
    }

    public static byte[] decodeIDATChunks(List<Chunk> idatChunks) throws IOException, DataFormatException {
        // Объединяем данные всех блоков IDAT в один массив
        ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
        for (Chunk chunk : idatChunks) {
            bytesStream.write(chunk.data);
        }
        byte[] compressedData = bytesStream.toByteArray();

        // Расжимаем данные
        Inflater inflater = new Inflater();
        inflater.setInput(compressedData);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();

        // Возвращаем расжатые данные
        return outputStream.toByteArray();
    }

    public static int calculateBytesPerPixel(byte colorType, byte bitDepth) {
        switch (colorType) {
            case 0: // градации серого
                return bitDepth == 16 ? 2 : 1;
            case 2: // RGB
                return bitDepth == 16 ? 6 : 3;
            case 3: // Палитра
                return 1; // Индексы палитры всегда в 1 байт, независимо от битовой глубины
            case 4: // градации серого с альфа-каналом
                return bitDepth == 16 ? 4 : 2;
            case 6: // RGBA
                return bitDepth == 16 ? 8 : 4;
            default:
                throw new IllegalArgumentException("Unsupported color type.");
        }
    }

}
