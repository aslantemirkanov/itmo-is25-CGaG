package ru.squad1332.cg.histogram;

import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.squad1332.cg.entities.Pixel;
import ru.squad1332.cg.modes.Channel;
import ru.squad1332.cg.modes.Mode;


public class HistogramService {

    public static void drawHistogram(Pixel[] pixelData, Mode mode, Channel channel) {
        if (channel.equals(Channel.ALL)) {

            int[] first = HistogramService.getChannelHistValues(pixelData, Channel.FIRST);
            int[] second = HistogramService.getChannelHistValues(pixelData, Channel.SECOND);
            int[] third = HistogramService.getChannelHistValues(pixelData, Channel.THIRD);

            Stage stage = new Stage();

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();

            CategoryAxis xAxis1 = new CategoryAxis();
            NumberAxis yAxis1 = new NumberAxis();

            CategoryAxis xAxis2 = new CategoryAxis();
            NumberAxis yAxis2 = new NumberAxis();

            CategoryAxis xAxis3 = new CategoryAxis();
            NumberAxis yAxis3 = new NumberAxis();

            BarChart<String, Number> barChart0 = new BarChart<>(xAxis, yAxis);
            XYChart.Series<String, Number> dataSeries0 = new XYChart.Series<>();

            BarChart<String, Number> barChart1 = new BarChart<>(xAxis1, yAxis1);
            XYChart.Series<String, Number> dataSeries1 = new XYChart.Series<>();

            BarChart<String, Number> barChart2 = new BarChart<>(xAxis2, yAxis2);
            XYChart.Series<String, Number> dataSeries2 = new XYChart.Series<>();

            BarChart<String, Number> barChart3 = new BarChart<>(xAxis3, yAxis3);
            XYChart.Series<String, Number> dataSeries3 = new XYChart.Series<>();

            for (int i = 0; i < 256; i++) {
                dataSeries0.getData().add(new XYChart.Data<>(String.valueOf(i), first[i] + second[i] + third[i]));
                dataSeries1.getData().add(new XYChart.Data<>(String.valueOf(i), first[i]));
                dataSeries2.getData().add(new XYChart.Data<>(String.valueOf(i), second[i]));
                dataSeries3.getData().add(new XYChart.Data<>(String.valueOf(i), third[i]));
            }

            barChart0.getData().add(dataSeries0);
            barChart0.setTitle("Значение");

            barChart1.getData().add(dataSeries1);
            barChart1.setTitle("Первый канал");

            barChart2.getData().add(dataSeries2);
            barChart2.setTitle("Второй канал");

            barChart3.getData().add(dataSeries3);
            barChart3.setTitle("Третий канал");

            VBox vBox = new VBox(barChart0, barChart1, barChart2, barChart3);
            Scene scene = new Scene(vBox, 300, 800);
            stage.setScene(scene);
            stage.setTitle(mode.toString());
            stage.show();

        } else {

            int[] first = HistogramService.getChannelHistValues(pixelData, channel);

            Stage stage = new Stage();

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();

            BarChart<String, Number> barChart0 = new BarChart<>(xAxis, yAxis);
            XYChart.Series<String, Number> dataSeries0 = new XYChart.Series<>();

            for (int i = 0; i < 256; i++) {
                dataSeries0.getData().add(new XYChart.Data<>(String.valueOf(i), first[i]));
            }

            barChart0.getData().add(dataSeries0);
            barChart0.setTitle("Гистограмма канала " + channel.toString());

            VBox vBox = new VBox(barChart0);
            Scene scene = new Scene(vBox, 500, 300);
            stage.setScene(scene);
            stage.setTitle(mode.toString());
            stage.show();

        }

    }

    public static int[] getChannelHistValues(Pixel[] pixelData, Channel channel) {
        int[] pixelCount = new int[256];
        int size = pixelData.length;
        for (int i = 0; i < size; i++) {
            Pixel curPixel = pixelData[i];
            int value;
            switch (channel) {
                case FIRST:
                    value = (int) (curPixel.getFirst() * 255);
                    break;
                case SECOND:
                    value = (int) (curPixel.getSecond() * 255);
                    break;
                case THIRD:
                    value = (int) (curPixel.getThird() * 255);
                    break;
                default:
                    throw new IllegalArgumentException("Неподдерживаемый канал: " + channel);
            }
            if (value >= 0 && value <= 255) {
                pixelCount[value]++;
            } else {
                throw new IllegalArgumentException("Недопустимое значение пикселя: " + value);
            }
        }
        return pixelCount;
    }

    public static void applyCorrection(Pixel[] pixelData, Mode mode, Channel channel, double alpha) {


        if (channel.equals(Channel.ALL)) {

            int[] first = HistogramService.getChannelHistValues(pixelData, Channel.FIRST);
            int[] second = HistogramService.getChannelHistValues(pixelData, Channel.SECOND);
            int[] third = HistogramService.getChannelHistValues(pixelData, Channel.THIRD);

            int totalPixels = 0;
            for (int count : first) {
                totalPixels += count;
            }

            int ignorePixelCount = (int) (alpha * totalPixels / 2);

            int min = 0;
            int sumFirst = 0;
            int sumSecond = 0;
            int sumThird = 0;

            for (int i = 0; i < first.length; i++) {
                sumFirst += first[i];
                sumSecond += second[i];
                sumThird += third[i];
                if (sumFirst > ignorePixelCount || sumSecond > ignorePixelCount || sumThird > ignorePixelCount) {
                    min = i;
                    break;
                }
            }

            int max = 255;
            sumFirst = 0;
            sumSecond = 0;
            sumThird = 0;

            for (int i = first.length - 1; i >= 0; i--) {
                sumFirst += first[i];
                sumSecond += second[i];
                sumThird += third[i];
                if (sumFirst > ignorePixelCount || sumSecond > ignorePixelCount || sumThird > ignorePixelCount) {
                    max = i;
                    break;
                }
            }

            System.out.println("!!!!!!!!!!!!!!" + min);
            System.out.println("!!!!!!!!!!!!!!" + max);

            double maxim = max / 255.0;
            double minim = min / 255.0;


            for (int i = 0; i < pixelData.length; i++) {
                pixelData[i].setFirst(Math.min(Math.max(0, (pixelData[i].getFirst() - minim) / (maxim - minim)), 1));
                pixelData[i].setSecond(Math.min(Math.max(0, (pixelData[i].getSecond() - minim) / (maxim - minim)), 1));
                pixelData[i].setThird(Math.min(Math.max(0, (pixelData[i].getThird() - minim) / (maxim - minim)), 1));
            }


        } else {

            int[] first = HistogramService.getChannelHistValues(pixelData, channel);

            int totalPixels = 0;
            for (int count : first) {
                totalPixels += count;
            }

            int ignorePixelCount = (int) (alpha * totalPixels / 2);

            int min = 0;
            int sumFirst = 0;

            for (int i = 0; i < first.length; i++) {
                sumFirst += first[i];
                if (sumFirst > ignorePixelCount) {
                    min = i;
                    break;
                }
            }

            int max = 255;
            sumFirst = 0;

            for (int i = first.length - 1; i >= 0; i--) {
                sumFirst += first[i];
                if (sumFirst > ignorePixelCount) {
                    max = i;
                    break;
                }
            }


            double maxim = max / 255.0;
            double minim = min / 255.0;


            for (int i = 0; i < pixelData.length; i++) {
                if (channel.equals(Channel.FIRST)) {
                    pixelData[i].setFirst(Math.min(Math.max(0, (pixelData[i].getFirst() - minim) / (maxim - minim)), 1));
                }
                if (channel.equals(Channel.SECOND)) {
                    pixelData[i].setSecond(Math.min(Math.max(0, (pixelData[i].getSecond() - minim) / (maxim - minim)), 1));
                }
                if (channel.equals(Channel.THIRD)) {
                    pixelData[i].setThird(Math.min(Math.max(0, (pixelData[i].getThird() - minim) / (maxim - minim)), 1));
                }
            }


        }
    }

    public static int[] applyAutoCorrection(int[] histogram, double correctionCoefficient) {
        // Определение пороговых значений для обрезки гистограммы
        int totalPixels = 0;
        for (int count : histogram) {
            totalPixels += count;
        }

        int ignorePixelCount = (int) (correctionCoefficient * totalPixels / 2);

        int minIndex = 0;
        int maxIndex = 255;

        // Находим минимальный индекс
        int sum = 0;
        for (int i = 0; i < histogram.length; i++) {
            sum += histogram[i];
            if (sum > ignorePixelCount) {
                minIndex = i;
                break;
            }
        }

        // Находим максимальный индекс
        sum = 0;
        for (int i = histogram.length - 1; i >= 0; i--) {
            sum += histogram[i];
            if (sum > ignorePixelCount) {
                maxIndex = i;
                break;
            }
        }

        // Растягиваем гистограмму
        int[] stretchedHistogram = new int[histogram.length];
        for (int i = 0; i < histogram.length; i++) {
            if (i < minIndex) {
                stretchedHistogram[i] = 0;
            } else if (i > maxIndex) {
                stretchedHistogram[i] = 255;
            } else {
                stretchedHistogram[i] = (int) ((i - minIndex) * 255.0 / (maxIndex - minIndex));
            }
        }

        return stretchedHistogram;
    }

}

