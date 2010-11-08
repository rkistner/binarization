/*
 * Copyright 2010 Ralf Kistner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rkistner;

import com.google.zxing.LuminanceSource;
import com.google.zxing.common.BitMatrix;


import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 */
public class Util {
    private static final int BLACK = Color.BLACK.getRGB();
    private static final int WHITE = Color.WHITE.getRGB();
    private static final int BROKEN_BLACK = Color.red.darker().darker().getRGB();
    private static final int BROKEN_WHITE = Color.green.brighter().brighter().getRGB();

    public static BufferedImage drawHistogram(int[] data, int width, int height) {
        int total = 0;
        int max = 0;
        for (int i : data) {
            total += i;
            if(i > max)
                max = i;
        }
        double xmult = 1.0 * width / data.length;
        double ymult = 0.9 * height / max;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.setColor(Color.BLACK);
        graphics.clearRect(0, 0, width, height);
        for(int i = 0; i < data.length; i++) {
            int h = (int)(data[i]*ymult);
            graphics.fillRect((int)(i*xmult), height-h-1, (int)((i+1)*xmult)-(int)(i*xmult), h);
        }
        image.flush();

        return image;
    }

    public static void compare(BitMatrix matrix, BufferedImage actual, TestResult result, boolean storeImages) {
        if(actual == null)
            return;
        int width = actual.getWidth();
        int height = actual.getHeight();
        int falseBlack = 0;
        int falseWhite = 0;

        BufferedImage diff = null;
        if(storeImages)
            diff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                boolean black = actual.getRGB(x, y) == BLACK;
                boolean matrixBlack = matrix.get(x, y);
                if(black != matrixBlack)
                    falseBlack += 1;
                int c;
                if(black && matrixBlack) {
                    c = BLACK;
                } else if(!black && !matrixBlack) {
                    c = WHITE;
                } else if(black && !matrixBlack) {
                    c = BROKEN_WHITE;
                } else {
                    c = BROKEN_BLACK;
                }
                if(storeImages)
                    diff.setRGB(x, y, c);
            }
        }
        result.setFalseBlack(falseBlack);
        result.setFalseWhite(falseWhite);
        if(storeImages)
            result.addImage("diff", diff);
    }

    public static BufferedImage fromBitMatrix(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                boolean black = matrix.get(x, y);
                image.setRGB(x, y, black ? 0 : 0x00FFFFFF);
            }
        }
        return image;

    }

    public static BufferedImage fromBitMatrix(BitMatrix matrix, int w, int h) {
        int width = Math.min(matrix.getWidth(), w);
        int height = Math.min(matrix.getHeight(), h);
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                boolean black = matrix.get(x, y);
                image.setRGB(x, y, black ? 0 : 0x00FFFFFF);
            }
        }
        return image;

    }

    public static BitMatrix toBitMatrix(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BitMatrix matrix = new BitMatrix(width, height);
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                boolean black = image.getRGB(x, y) == BLACK;
                if(black)
                    matrix.set(x, y);
            }
        }
        return matrix;
    }

    public static BufferedImage fromLuminanceSource(LuminanceSource source) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        byte[] data = source.getMatrix();
        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = data[y*width+x];
                int rgb = gray | gray << 8 | gray << 16;
                result.setRGB(x, y, rgb);
            }
        }
        return result;
    }


    public static BufferedImage scale(BufferedImage input, double scale) {
        int width = (int)(input.getWidth() * scale);
        int height = (int)(input.getHeight() * scale);
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        result.createGraphics().drawImage(input, 0, 0, width, height, null);
        return result;
    }
}
