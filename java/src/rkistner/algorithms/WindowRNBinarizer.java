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


public final class WindowRNBinarizer extends Binarizer {

    public static BinarizerFactory createFactory(final int bs, final float frac, final int threshold) {
        return new BinarizerFactory() {
            public WindowRNBinarizer getBinarizer(LuminanceSource source) {
                return new WindowRNBinarizer(source, bs, frac, threshold);
            }

            public String toString() {
                return "Window (Reduced Noise) [" + bs + "|" + frac + "|" + threshold + "]";
            }
        };
    }

    private int bs = 8;
    private float frac = 3;
    private int threshold = 8;


    public WindowRNBinarizer(LuminanceSource source, int bs, float frac, int threshold) {
        super(source);
        this.bs = bs;
        this.frac = frac;
        this.threshold = threshold;
    }


    private void blockTotals(byte[] data, int[][] total, int[][] squares) {
        LuminanceSource source = getLuminanceSource();
        int width = source.getWidth();
        int height = source.getHeight();
        int aw = width / bs;
        int ah = height / bs;

        for(int by = 0; by < ah; by++) {
            int ey = (by+1)*bs;
            for(int bx = 0; bx < aw; bx++) {
                int t = 0;
                int t2 = 0;

                for(int y = by*bs; y < ey; y++) {
                    int offset = y*width+bx*bs;
                    int ex = offset+bs;
                    for(; offset < ex; offset++) {
                        int v = data[offset] & 0xff;
                        t += v;
                        t2 += v*v;
                    }
                }
                total[by][bx] = t;
                squares[by][bx] = t2;
            }
        }
    }

    private static int variance(int[][] cumulative, int[][] cumulativeSquares, int x, int y, int r, int width, int height, int mult) {
        int top = Math.max(0, y - r + 1);
        int left = Math.max(0, x - r + 1);
        int bottom = Math.min(height, y + r);
        int right = Math.min(width, x + r);

        int block = Common.window(cumulative, top, left, bottom, right);
        int pixels = (bottom - top) * (right - left) * mult;
        int avg = block / pixels;
        int blockSquare = Common.window(cumulativeSquares, top, left, bottom, right);
        int variance = blockSquare / pixels - avg*avg;

        return variance;
    }


    @Override
    public BitArray getBlackRow(int y, BitArray row) throws NotFoundException {
        return null;
    }

    @Override
    public BitMatrix getBlackMatrix() throws NotFoundException {
        LuminanceSource source = getLuminanceSource();
        int width = source.getWidth();
        int height = source.getHeight();
        int r = (int)(Math.min(width, height) * frac / bs / 2 + 1);

        int aw = width / bs;
        int ah = height / bs;
        byte[] data = source.getMatrix();

        int[][] blockTotal = new int[ah][aw];
        int[][] blockSquares = new int[ah][aw];
        blockTotals(data, blockTotal, blockSquares);

        int[][] totals = Common.cumulative(blockTotal);
        int[][] squares = Common.cumulative(blockSquares);

        int mean = totals[ah][aw] / width / height;
        int var = squares[ah][aw] / width / height - mean*mean;

        BitMatrix matrix = new BitMatrix(width, height);
        for(int by = 0; by < ah; by++) {
            for(int bx = 0; bx < aw; bx++) {
                int top = Math.max(0, by - r + 1);
                int left = Math.max(0, bx - r + 1);
                int bottom = Math.min(ah, by + r);
                int right = Math.min(aw, bx + r);

                int block = Common.window(totals, top, left, bottom, right);
                int pixels = (bottom - top) * (right - left) * bs * bs;
                int avg = block / pixels;

                int variance = variance(totals, squares, bx, by, r, aw, ah, bs*bs);


                if(variance * threshold > var) {
                    for(int y = by*bs; y < (by+1)*bs; y++) {
                        for(int x = bx*bs; x < (bx+1)*bs; x++) {
                            int pixel = data[y*width + x] & 0xff;
                            if(pixel < avg)
                                matrix.set(x, y);
                        }
                    }
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