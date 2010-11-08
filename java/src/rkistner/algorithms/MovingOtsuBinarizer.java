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
public class MovingOtsuBinarizer extends Binarizer {
    public static BinarizerFactory createFactory(final int blockRadius) {
        return new BinarizerFactory() {
            public Binarizer getBinarizer(LuminanceSource source) {
                return new MovingOtsuBinarizer(source, blockRadius);
            }

            public String toString() {
                return "MovingOtsuBinarizer [" + blockRadius + "]";
            }
        };
    }

    private int blockRadius;
    private int[][] totals;

    public MovingOtsuBinarizer(LuminanceSource source, int blockRadius) {
        super(source);
        this.blockRadius = blockRadius;
    }

    @Override
    public BitArray getBlackRow(int y, BitArray row) throws NotFoundException {
        return null;
    }

    private void calculateTotals() {
        if(totals == null) {
            LuminanceSource source = getLuminanceSource();
            int width = source.getWidth();
            int height = source.getHeight();
            totals = new int[height + 1][width + 1];
            int[][] rowTotals = new int[height][];
            byte[] rowData = new byte[width];
            for(int y = 0; y < height; y++) {
                int[] row = new int[width + 1];
                rowData = source.getRow(y, rowData);
                int t = 0;
                row[0] = t;
                for(int x = 0; x < width; x++) {
                    t += (rowData[x] & 0xff);
                    row[x + 1] = t;
                }
                rowTotals[y] = row;
            }
            totals[0] = new int[width + 1];
            for(int x = 0; x <= width; x++) {
                int t = 0;
                for(int y = 0; y < height; y++) {
                    t += rowTotals[y][x];
                    totals[y + 1][x] = t;
                }
            }

        }
    }

    @Override
    public BitMatrix getBlackMatrix() throws NotFoundException {
        calculateTotals();
        LuminanceSource source = getLuminanceSource();
        final int width = source.getWidth();
        final int height = source.getHeight();
        BitMatrix matrix = new BitMatrix(width, height);
        final byte[] resultData = new byte[width*height];

        byte[] rowData = new byte[width];
        for(int y = 0; y < height; y++) {
            rowData = source.getRow(y, rowData);
            for(int x = 0; x < width; x++) {
                int top = Math.max(0, y - blockRadius + 1);
                int left = Math.max(0, x - blockRadius + 1);
                int bottom = Math.min(height, y + blockRadius);
                int right = Math.min(width, x + blockRadius);
                int block = totals[bottom][right] + totals[top][left] - totals[top][right] - totals[bottom][left];
                int pixels = (bottom - top) * (right - left);
                int avg = block / pixels;

                int r = (rowData[x] & 0xff) + 128 - avg;
                resultData[y*width + x] = (byte)r;
            }
        }


        LuminanceSource newSource = new LuminanceSource(width, height) {
            @Override
            public byte[] getRow(int y, byte[] row) {
                if(row.length < width)
                    row = new byte[width];
                System.arraycopy(resultData, y*width, row, 0, width);
                return row;
            }

            @Override
            public byte[] getMatrix() {
                return resultData;
            }
        };
        return new GlobalBinarizer(newSource, new OtsuThresholder()).getBlackMatrix();
    }

    @Override
    public Binarizer createBinarizer(LuminanceSource source) {
        return new MovingOtsuBinarizer(source, blockRadius);
    }

}
