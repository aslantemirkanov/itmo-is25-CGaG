package ru.squad1332.cg.convertor;

import ru.squad1332.cg.entities.Pixel;
import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.Mode;
import ru.squad1332.cg.modes.YCbCrFormat;

import java.util.Map;

public class ColorConvertor {
    private static final Map<Channel, int[]> POSITIONS_MAP = Map.of(
            Channel.ALL, new int[]{1, 1, 1},
            Channel.FIRST, new int[]{1, 0, 0},
            Channel.SECOND, new int[]{0, 1, 0},
            Channel.THIRD, new int[]{0, 0, 1}
    );

    public static int[] convertToRgb(Pixel[] pixelData, Channel channel) {
        int[] intRgba = new int[pixelData.length];
        int[] pos = POSITIONS_MAP.get(channel);
        for (int i = 0; i < pixelData.length; i++) {
            double[] rgba = pixelData[i].getRgba();
            int r = pos[0] * (int) (rgba[0] * 255);
            int g = pos[1] * (int) (rgba[1] * 255);
            int bl = pos[2] * (int) (rgba[2] * 255);
            int alpha = 255;
            intRgba[i] = (alpha << 24) + (r << 16) + (g << 8) + (bl);
        }
        return intRgba;
    }

    public static int[] convertToHSL(Pixel[] pixelData, Channel channel) {
        int[] rgba = new int[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getRed();
            double g = color.getGreen();
            double b = color.getBlue();
            double max = Math.max(r, Math.max(g, b));
            double min = Math.min(r, Math.min(g, b));
            double d = max - min;
            double l = (max + min) / 2;
            double s = d / (1 - Math.abs(1 - (max + min)));
            double h = 0;

            if (max == min) {
                h = 0;
                s = 0;
            } else if (max == r && g >= b) {
                h = 60 * (g - b) / d;
            } else if (max == r && g < b) {
                h = 60 * (g - b) / d + 360;
            } else if (max == g) {
                h = 60 * (b - r) / d + 120;
            } else if (max == b) {
                h = 60 * (r - g) / d + 240;
            }


            switch (channel) {
                case ALL -> {
                    h = h / 360 + s + l;
                    s = h;
                    l = h;
                }
                case FIRST -> {
                    h = h / 360;
                    s = h;
                    l = h;
                }
                case SECOND -> {
                    h = s;
                    s = s;
                    l = s;
                }
                case THIRD -> {
                    h = l;
                    s = l;
                    l = l;
                }
            }


//            double c = (1.0 - Math.abs(2.0 * l - 1.0)) * s;
//            double x = c * (1.0 - Math.abs((h / 60.0) % 2.0 - 1.0));
//            double m = l - c / 2.0;
//
//            if (h >= 0 && h < 60) {
//                r = c;
//                g = x;
//                b = 0;
//            } else if (h >= 60 && h < 120) {
//                r = x;
//                g = c;
//                b = 0;
//            } else if (h >= 120 && h < 180) {
//                r = 0;
//                g = c;
//                b = x;
//            } else if (h >= 180 && h < 240) {
//                r = 0;
//                g = x;
//                b = c;
//            } else if (h >= 240 && h < 300) {
//                r = x;
//                g = 0;
//                b = c;
//            } else {
//                r = c;
//                g = 0;
//                b = x;
//            }
//
//            int red = (int) ((r + m) * 255);
//            int green = (int) ((g + m) * 255);
//            int blue = (int) ((b + m) * 255);
            int red = (int) (h * 255);
            int green = (int) (s * 255);
            int blue = (int) (l * 255);
            int alpha = 255;
            rgba[i] = (alpha << 24) + (red << 16) + (green << 8) + (blue);
        }

        return rgba;
    }

    public static int[] convertToHSV(Pixel[] pixelData, Channel channel) {
        int[] rgba = new int[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getRed();
            double g = color.getGreen();
            double b = color.getBlue();

            double max = Math.max(Math.max(r, g), b);
            double min = Math.min(Math.min(r, g), b);
            double delta = max - min;

            double h;
            if (delta == 0) {
                h = 0;
            } else if (max == r && g >= b) {
                h = 60 * (g - b) / delta;
            } else if (max == r) {
                h = 60 * (g - b) / delta + 360;
            } else if (max == g) {
                h = 60 * (b - r) / delta + 120;
            } else {
                h = 60 * (r - g) / delta + 240;
            }

            double s = max == 0 ? 0 : 1 - min / max;
            double v = max;

            switch (channel) {
                case ALL -> {
                    h = h;
                    s = s;
                    v = v;
                }
                case FIRST -> {
                    h = h / 360;
                    s = h;
                    v = h;
                }
                case SECOND -> {
                    h = s;
                    s = s;
                    v = s;
                }
                case THIRD -> {
                    h = v;
                    s = v;
                    v = v;
                }
            }


//            double c = v * s;
//            double x = c * (1 - Math.abs((h / 60) % 2 - 1));
//            double m = v - c;
//
//            double r1, g1, b1;
//
//            if (0 <= h && h < 60) {
//                r1 = c;
//                g1 = x;
//                b1 = 0;
//            } else if (60 <= h && h < 120) {
//                r1 = x;
//                g1 = c;
//                b1 = 0;
//            } else if (120 <= h && h < 180) {
//                r1 = 0;
//                g1 = c;
//                b1 = x;
//            } else if (180 <= h && h < 240) {
//                r1 = 0;
//                g1 = x;
//                b1 = c;
//            } else if (240 <= h && h < 300) {
//                r1 = x;
//                g1 = 0;
//                b1 = c;
//            } else {
//                r1 = c;
//                g1 = 0;
//                b1 = x;
//            }
//
//            int red = (int) ((r1 + m) * 255);
//            int green = (int) ((g1 + m) * 255);
//            int blue = (int) ((b1 + m) * 255);
            int red = (int) (h * 255);
            int green = (int) (s * 255);
            int blue = (int) (v * 255);
            int alpha = 255;
            rgba[i] = (alpha << 24) + (red << 16) + (green << 8) + (blue);
        }
        return rgba;
    }

    public static int[] convertToYCbCr601(Pixel[] pixelData, Channel channel) {
        return convertYCbCr(pixelData, channel, YCbCrFormat.YCBCR601);
    }

    public static int[] convertToYCbCr709(Pixel[] pixelData, Channel channel) {
        return convertYCbCr(pixelData, channel, YCbCrFormat.YCBCR709);
    }

    private static int[] convertYCbCr(Pixel[] pixelData, Channel channel, YCbCrFormat format) {
        int[] rgba = new int[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getRed() * 255;
            double g = color.getGreen() * 255;
            double b = color.getBlue() * 255;

            double y = 0;
            double cb = 0;
            double cr = 0;

            switch (format) {
                case YCBCR601 -> {
                    y = 0.299 * r + 0.587 * g + 0.114 * b;
                    cb = -0.1687 * r - 0.3313 * g + 0.5 * b + 128;
                    cr = 0.5 * r - 0.4187 * g - 0.0813 * b + 128;
                }
                case YCBCR709 -> {
                    y = 0.2126 * r + 0.7152 * g + 0.0722 * b;
                    cb = -0.1146 * r - 0.3854 * g + 0.5 * b + 128;
                    cr = 0.5 * r - 0.4542 * g - 0.0458 * b + 128;
                }
            }

            switch (channel) {
                case ALL -> {
                    y = y;
                    cb = cb;
                    cr = cr;
                }
                case FIRST -> {
                    y = y;
                    cb = y;
                    cr = y;
                }
                case SECOND -> {
                    y = cb;
                    cb = cb;
                    cr = cb;
                }
                case THIRD -> {
                    y = cr;
                    cb = cr;
                    cr = cr;
                }
            }

            double yd = y;
            double cbd = cb - 128;
            double crd = cr - 128;

            int red = 0;
            int green = 0;
            int blue = 0;

            switch (format) {
                case YCBCR601 -> {
                    red = (int) Math.round(yd + 1.402 * crd);
                    green = (int) Math.round(yd - 0.3441 * cbd - 0.7141 * crd);
                    blue = (int) Math.round(yd + 1.772 * cbd);
                }
                case YCBCR709 -> {
                    red = (int) Math.round(yd + 1.5748 * crd);
                    green = (int) Math.round(yd - 0.1873 * cbd - 0.4681 * crd);
                    blue = (int) Math.round(yd + 1.8556 * cbd);
                }
            }

            red = Math.min(255, Math.max(0, red));
            green = Math.min(255, Math.max(0, green));
            blue = Math.min(255, Math.max(0, blue));

            int alpha = 255;
            rgba[i] = (alpha << 24) + (red << 16) + (green << 8) + (blue);
        }
        return rgba;
    }

    public static int[] convertToYCoCg(Pixel[] pixelData, Channel channel) {
        int[] rgba = new int[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getRed();
            double g = color.getGreen();
            double b = color.getBlue();

            double y = r / 4 + g / 2 + b / 4;
            double co = r / 2 - b / 2;
            double cg = -r / 4 + g / 2 - b / 4;

            switch (channel) {
                case ALL -> {
                    y = y;
                    co = co;
                    cg = cg;
                }
                case FIRST -> {
                    y = y;
                    co = 0;
                    cg = 0;
                }
                case SECOND -> {
                    y = y;
                    co = co >= 0 ? co : co + 1;
                    cg = 0;
                }
                case THIRD -> {
                    y = y;
                    co = 0;
                    cg = cg;
                }
            }

            int red = (int) ((y + co - cg) * 255);
            int green = (int) ((y + cg) * 255);
            int blue = (int) ((y - co - cg) * 255);

            int alpha = 255;
            rgba[i] = (alpha << 24) + (red << 16) + (green << 8) + (blue);
        }
        return rgba;
    }

    public static int[] convertToCmy(Pixel[] pixelData, Channel channel) {
        int[] rgba = new int[pixelData.length];
        for (int i = 0; i < pixelData.length; i++) {
            Pixel color = pixelData[i];
            double r = color.getRed();
            double g = color.getGreen();
            double b = color.getBlue();

            double c = 1.0 - r;
            double m = 1.0 - g;
            double y = 1.0 - b;

            switch (channel) {
                case ALL -> {
                    c = c;
                    m = m;
                    y = y;
                }
                case FIRST -> {
                    c = c;
                    m = 0;
                    y = 0;
                }
                case SECOND -> {
                    c = 0;
                    m = m;
                    y = 0;
                }
                case THIRD -> {
                    c = 0;
                    m = 0;
                    y = y;
                }
            }

            int red = (int) ((1 - c) * 255);
            int green = (int) ((1 - m) * 255);
            int blue = (int) ((1 - y) * 255);
            int alpha = 255;
            rgba[i] = (alpha << 24) + (red << 16) + (green << 8) + (blue);
        }
        return rgba;
    }

    public static int[] convertRgb(Pixel pixel, Mode mode, Channel channel) {
        return new int[0];
    }
}
