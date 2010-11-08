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
public class SimpleWindowBinarizer extends Binarizer {
    public static BinarizerFactory createFactory(final float fraction) {
        return new BinarizerFactory() {
            public Binarizer getBinarizer(LuminanceSource source) {
                return new SimpleWindowBinarizer(source, fraction);
            }

            public String toString() {
                return "SimpleWindow [" + fraction + "]";
            }
        };
    }

    private float fraction;

    public SimpleWindowBinarizer(LuminanceSource source, float fraction) {
        super(source);
        this.fraction = fraction;
    }


    @Override
    public BitArray getBlackRow(int y, BitArray row) throws NotFoundException {
        return null;
    }

    public BitMatrix getBlackMatrix() throws NotFoundException {
        LuminanceSource source = getLuminanceSource();
        int width = source.getWidth();
        int height = source.getHeight();
        byte[] data = source.getMatrix();
        int[][] totals = Common.cumulative(data, width, height);
        int r = (int)((Math.min(width, height) * fraction) / 2 + 1);
        BitMatrix matrix = new BitMatrix(width, height);
        for(int y = 0; y < height; y++) {
            int top = Math.max(0, y - r + 1);
            int bottom = Math.min(height, y + r);
            int offset = y*width;
            for(int x = 0; x < width; x++) {
                int left = Math.max(0, x - r + 1);
                int right = Math.min(width, x + r);
                int block = Common.window(totals, top, left, bottom, right);
                int pixels = (bottom - top) * (right - left);
                int avg = block / pixels;

                if((data[offset+x] & 0xff) < avg) {
                    matrix.set(x, y);
                }
            }
        }
        return matrix;
    }

    @Override
    public Binarizer createBinarizer(LuminanceSource source) {
        return null;
    }
}
