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

/**
 *
 */
public class Common {
    public static int[][] cumulative(int[][] data) {
        int ah = data.length;
        int aw = data[0].length;
        int[][] totals = new int[ah + 1][aw + 1];
        int[][] rowTotals = new int[ah][];


        for(int y = 0; y < ah; y++) {
            int[] row = new int[aw + 1];
            int[] rowdata = data[y];
            int t = 0;
            row[0] = t;
            for(int x = 0; x < aw; x++) {
                t += rowdata[x];
                row[x + 1] = t;
            }
            rowTotals[y] = row;
        }
        totals[0] = new int[aw + 1];
        for(int x = 0; x <= aw; x++) {
            int t = 0;
            for(int y = 0; y < ah; y++) {
                t += rowTotals[y][x];
                totals[y + 1][x] = t;
            }
        }
        return totals;
    }

    public static int[][] cumulative(byte[] data, int width, int height) {
        int[][] totals = new int[height + 1][width + 1];
        int[][] rowTotals = new int[height][];
        for(int y = 0; y < height; y++) {
            int[] row = new int[width + 1];
            int t = 0;
            row[0] = t;
            int offset = y*width;
            for(int x = 0; x < width; x++) {
                t += (data[offset+x] & 0xff);
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
        
        return totals;
    }

    public static int[][] cumulativeSquares(byte[] data, int width, int height) {
        int[][] totals = new int[height + 1][width + 1];
        int[][] rowTotals = new int[height][];
        for(int y = 0; y < height; y++) {
            int[] row = new int[width + 1];
            int t = 0;
            row[0] = t;
            int offset = y*width;
            for(int x = 0; x < width; x++) {
                int v = data[offset+x] & 0xff;
                t += v*v;
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

        return totals;
    }


    public static int window(int[][] cumulative, int top, int left, int bottom, int right) {
        return cumulative[bottom][right] + cumulative[top][left] - cumulative[top][right] - cumulative[bottom][left];
    }

    public static int[][] cumulativeSquares(int[][] data) {
        int ah = data.length;
        int aw = data[0].length;
        int[][] totals = new int[ah + 1][aw + 1];
        int[][] rowTotals = new int[ah][];


        for(int y = 0; y < ah; y++) {
            int[] row = new int[aw + 1];
            int[] rowdata = data[y];
            int t = 0;
            row[0] = t;
            for(int x = 0; x < aw; x++) {
                int v = rowdata[x];
                t += v*v;
                row[x + 1] = t;
            }
            rowTotals[y] = row;
        }
        totals[0] = new int[aw + 1];
        for(int x = 0; x <= aw; x++) {
            int t = 0;
            for(int y = 0; y < ah; y++) {
                t += rowTotals[y][x];
                totals[y + 1][x] = t;
            }
        }
        return totals;
    }
}
