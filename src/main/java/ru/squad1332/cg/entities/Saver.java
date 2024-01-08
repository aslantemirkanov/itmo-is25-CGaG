package ru.squad1332.cg.entities;

import ru.squad1332.cg.dither.DitheringService;
import ru.squad1332.cg.gamma.GammaCorrection;
import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.Mode;
import ru.squad1332.cg.parsers.Chunk;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static ru.squad1332.cg.entities.PicturePNG.compressIDAT;
import static ru.squad1332.cg.entities.PicturePNM.RGB_TO_OTHER;
import static ru.squad1332.cg.services.PictureService.PNG_SIGNATURE;

public class Saver {
    public static void writeToFile(File file, Picture picture, Mode mode, Channel channel, double curGamma, Object dither, int bit) {
        String[] words = file.toString().split("\\.");
        String extension = words[words.length - 1];
        if (extension.equals("png") || extension.equals("PNG")){
            try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file))) {
                Pixel[] pixelData = picture.getPixelData();
                String formatType = picture.getFormatType();
                int width = picture.getWidth();
                int height = picture.getHeight();

                if (dither != null) {
                    DitheringService.applyDithering(pixelData, dither.toString(), formatType, bit, width, height, curGamma);
                }
                pixelData = GammaCorrection.convertGamma(pixelData, curGamma, 0);
                int colorType = picture.getColorType();
                int bytesPerPixel = 3;
                if (colorType == 0){
                    bytesPerPixel = 1;
                }
                if (colorType == 3){
                    colorType = 2;
                }

                //signature
                for (byte b : PNG_SIGNATURE) {
                    dataOutputStream.writeByte(b);
                }

                // ihdr
                int bitDepth = 8;
                int compressionMethod = 0;
                int filterMethod = 0;
                int interlaceMethod = 0;

                byte[] ihdrData = new byte[13];
                ByteBuffer ihdrBuffer = ByteBuffer.wrap(ihdrData);

                ihdrBuffer.putInt(picture.getWidth());
                ihdrBuffer.putInt(picture.getHeight());
                ihdrBuffer.put((byte) bitDepth);
                ihdrBuffer.put((byte) colorType);
                ihdrBuffer.put((byte) compressionMethod);
                ihdrBuffer.put((byte) filterMethod);
                ihdrBuffer.put((byte) interlaceMethod);

                Chunk ihdrChunk = new Chunk("IHDR", ihdrData);
                ihdrChunk.writeChunk(dataOutputStream);

                //idat
                Chunk idat = compressIDAT(pixelData, width, height, bytesPerPixel, colorType);
                idat.writeChunk(dataOutputStream);


                //gamma
                double gamma = picture.getGamma();
                int gammaInt = (int) (gamma * 100000.0);
                byte[] gammaBytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(gammaInt).array();
                Chunk gammaChunk = new Chunk("gAMA", gammaBytes);
                gammaChunk.writeChunk(dataOutputStream);

                //iend
                Chunk endChunk = new Chunk("IEND", new byte[0]);
                endChunk.writeChunk(dataOutputStream);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return;
        }
        if (extension.equals("pnm") || extension.equals("PNM") || extension.equals("PNN") || extension.equals("ppm")
        || extension.equals("PPM")){
            try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file))) {
                Pixel[] pixelData = picture.getPixelData();
                String formatType = picture.getFormatType();
                if (formatType.equals("PNG")){
                    formatType = "P6";
                }
                int width = picture.getWidth();
                int height = picture.getHeight();
                int maxColorValue = 255;
                if (dither != null) {
                    DitheringService.applyDithering(pixelData, dither.toString(), formatType, bit, width, height, curGamma);
                }
                pixelData = RGB_TO_OTHER.get(mode).apply(pixelData, channel);
                if (channel.equals(Channel.ALL)) {
                    dataOutputStream.writeBytes(formatType + (char) (10));
                    dataOutputStream.writeBytes(String.valueOf(width) + (char) (32) + String.valueOf(height) + (char) (10));
                    dataOutputStream.writeBytes(String.valueOf(maxColorValue) + (char) (10));

                    if (formatType.equals("P6")) {
                        byte[] pixels = new byte[3 * height * width];
                        int cur = 0;
                        for (int i = 0; i < pixelData.length; i++) {
                            cur = i * 3;
                            Pixel curPixel = pixelData[i];
                            int alpha = 255;
                            double[] convert = curPixel.getColors();
                            int first = (int) (convert[0] * 255);
                            int second = (int) (convert[1] * 255);
                            int third = (int) (convert[2] * 255);
                            pixels[cur] = (byte) (first > 127 ? first - 256 : first);
                            pixels[cur + 1] = (byte) (second > 127 ? second - 256 : second);
                            pixels[cur + 2] = (byte) (third > 127 ? third - 256 : third);
                        }
                        ;
                        dataOutputStream.write(pixels);
                        dataOutputStream.close();
                    }

                    if (formatType.equals("P5")) {
                        byte[] pixels = new byte[height * width];
                        for (int i = 0; i < pixelData.length; i++) {
                            int first = (int) (pixelData[i].getColors()[0] * 255);
                            pixels[i] = (byte) (first > 127 ? first - 256 : first);
                        }
                        dataOutputStream.write(pixels);
                    }
                } else {
                    dataOutputStream.writeBytes("P5" + (char) (10));
                    dataOutputStream.writeBytes(String.valueOf(width) + (char) (32) + String.valueOf(height) + (char) (10));
                    dataOutputStream.writeBytes(String.valueOf(maxColorValue) + (char) (10));

                    byte[] pixels = new byte[height * width];
                    for (int i = 0; i < pixelData.length; i++) {
                        int first = 0;
                        switch (channel) {
                            case FIRST -> first = (int) (pixelData[i].getColors()[0] * 255);
                            case SECOND -> first = (int) (pixelData[i].getColors()[1] * 255);
                            case THIRD -> first = (int) (pixelData[i].getColors()[2] * 255);
                        }
                        pixels[i] = (byte) (first > 127 ? first - 256 : first);
                    }
                    dataOutputStream.write(pixels);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
    }
}
