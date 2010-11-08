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

package rkistner.algorithms;

import com.google.zxing.Binarizer;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;

/**
 * @author Ralf Kister <ralf.kistner@gmail.com>
 */
public class SplitBinarizer extends Binarizer {
    public static BinarizerFactory createFactory(final int numx, final int numy) {
        return new BinarizerFactory() {
            public Binarizer getBinarizer(LuminanceSource source) {
                return new SplitBinarizer(source, numx, numy);
            }

            public String toString() {
                return "Split [" + numx + "]";
            }
        };
    }

    private int numx;
    private int numy;

    public SplitBinarizer(LuminanceSource source, int numx, int numy) {
        super(source);
        this.numx = numx;
        this.numy = numy;
    }


    @Override
    public BitArray getBlackRow(int y, BitArray row) throws NotFoundException {
        return null;
    }

    private static void copy(BitMatrix source, BitMatrix destination, int destinationX, int destinationY) {
        for(int x = 0; x < source.getWidth(); x++) {
            for(int y = 0; y < source.getHeight(); y++) {
                if(source.get(x, y)) {
                    destination.set(x + destinationX, y + destinationY);
                }
            }
        }
    }

    @Override
    public BitMatrix getBlackMatrix() throws NotFoundException {
        LuminanceSource source = getLuminanceSource();
        int width = source.getWidth();
        int height = source.getHeight();
        BitMatrix result = new BitMatrix(width, height);
        for(int x = 0; x < numx; x++) {
            int left = x * width / numx;
            int right = (x + 1) * width / numx;
            for(int y = 0; y < numy; y++) {
                int top = y * height / numy;
                int bot = (y + 1) * height / numy;
                LuminanceSource cropped = source.crop(left, top, right - left, bot - top);
                GlobalBinarizer binarizer = new GlobalBinarizer(cropped, new OtsuThresholder());
                BitMatrix matrix = binarizer.getBlackMatrix();
                copy(matrix, result, left, top);
            }
        }
        return result;
    }

    @Override
    public Binarizer createBinarizer(LuminanceSource source) {
        return new SplitBinarizer(source, numx, numy);
    }
}
