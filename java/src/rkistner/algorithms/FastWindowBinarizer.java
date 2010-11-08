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


public final class FastWindowBinarizer extends Binarizer {
    
    public static BinarizerFactory createFactory(final int bs, final float frac) {
        return new BinarizerFactory() {
            public FastWindowBinarizer getBinarizer(LuminanceSource source) {
                return new FastWindowBinarizer(source, bs, frac);
            }

            public String toString() {
                return "Window [" + bs + "|" + frac + "]";
            }
        };
    }

    private int bs = 8;
    private float frac = 3;


    public FastWindowBinarizer(LuminanceSource source, int bs, float frac) {
        super(source);
        this.bs = bs;
        this.frac = frac;
    }


    private void blockTotals(byte[] data, int[][] total) {
        LuminanceSource source = getLuminanceSource();
        int width = source.getWidth();
        int height = source.getHeight();
        int aw = width / bs;
        int ah = height / bs;

        for(int by = 0; by < ah; by++) {
            int ey = (by+1)*bs;
            for(int bx = 0; bx < aw; bx++) {
                int t = 0;

                for(int y = by*bs; y < ey; y++) {
                    int offset = y*width+bx*bs;
                    int ex = offset+bs;
                    for(; offset < ex; offset++) {
                        int v = data[offset] & 0xff;
                        t += v;
                    }
                }
                total[by][bx] = t;
            }
        }
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
        blockTotals(data, blockTotal);

        int[][] totals = Common.cumulative(blockTotal);
        
        
        BitMatrix matrix = new BitMatrix(width, height);
        for(int by = 0; by < ah; by++) {
            for(int bx = 0; bx < aw; bx++) {
                int top = Math.max(0, by - r + 1);
                int left = Math.max(0, bx - r + 1);
                int bottom = Math.min(ah, by + r);
                int right = Math.min(aw, bx + r);

                int block = totals[bottom][right] + totals[top][left] - totals[top][right] - totals[bottom][left];


                int pixels = (bottom - top) * (right - left) * bs * bs;
                int avg = block / pixels;

                for(int y = by*bs; y < (by+1)*bs; y++) {
                    for(int x = bx*bs; x < (bx+1)*bs; x++) {
                        int pixel = data[y*width + x] & 0xff;
                        if(pixel < avg)
                            matrix.set(x, y);
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