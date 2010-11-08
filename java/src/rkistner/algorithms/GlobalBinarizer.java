/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
 * Adapted from com.google.zxing.common.GlobalHistogramBinarizer, but adapted to allow different
 * threshold selection algorithms.
 *
 * -------------------------------------------------------------------------------------------
 *
 * This Binarizer implementation uses the old ZXing global histogram approach. It is suitable
 * for low-end mobile devices which don't have enough CPU or memory to use a local thresholding
 * algorithm. However, because it picks a global black point, it cannot handle difficult shadows
 * and gradients.
 *
 * Faster mobile devices and all desktop applications should probably use HybridBinarizer instead.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public class GlobalBinarizer extends Binarizer {
    public static BinarizerFactory createFactory(final ThresholdFinder thresholder) {
        return new BinarizerFactory() {
            public GlobalBinarizer getBinarizer(LuminanceSource source) {
                return new GlobalBinarizer(source, thresholder);
            }

            public String toString() {
                return "GlobalBinarizer [" + thresholder + "]";
            }
        };
    }

    private static final int LUMINANCE_BITS = 6;
    private static final int LUMINANCE_SHIFT = 8 - LUMINANCE_BITS;
    private static final int LUMINANCE_BUCKETS = 1 << LUMINANCE_BITS;

    private byte[] luminances = null;
    private int[] buckets = null;

    private ThresholdFinder thresholder;

    public GlobalBinarizer(LuminanceSource source, ThresholdFinder thresholder) {
        super(source);
        this.thresholder = thresholder;
    }

    // Applies simple sharpening to the row data to improve performance of the 1D Readers.

    public BitArray getBlackRow(int y, BitArray row) throws NotFoundException {
        LuminanceSource source = getLuminanceSource();
        int width = source.getWidth();
        if (row == null || row.getSize() < width) {
            row = new BitArray(width);
        } else {
            row.clear();
        }

        initArrays(width);
        byte[] localLuminances = source.getRow(y, luminances);
        int[] localBuckets = buckets;
        for (int x = 0; x < width; x++) {
            int pixel = localLuminances[x] & 0xff;
            localBuckets[pixel >> LUMINANCE_SHIFT]++;
        }
        int blackPoint = thresholder.findThreshold(localBuckets) << LUMINANCE_SHIFT;

        int left = localLuminances[0] & 0xff;
        int center = localLuminances[1] & 0xff;
        for (int x = 1; x < width - 1; x++) {
            int right = localLuminances[x + 1] & 0xff;
            // A simple -1 4 -1 box filter with a weight of 2.
            int luminance = ((center << 2) - left - right) >> 1;
            if (luminance < blackPoint) {
                row.set(x);
            }
            left = center;
            center = right;
        }
        return row;
    }

    // Does not sharpen the data, as this call is intended to only be used by 2D Readers.

    public BitMatrix getBlackMatrix() throws NotFoundException {
        LuminanceSource source = getLuminanceSource();
        int width = source.getWidth();
        int height = source.getHeight();
        BitMatrix matrix = new BitMatrix(width, height);

        // Quickly calculates the histogram by sampling four rows from the image. This proved to be
        // more robust on the blackbox tests than sampling a diagonal as we used to do.

        int blackPoint = thresholder.findThreshold(getHistogram()) << LUMINANCE_SHIFT;

        // We delay reading the entire image luminance until the black point estimation succeeds.
        // Although we end up reading four rows twice, it is consistent with our motto of
        // "fail quickly" which is necessary for continuous scanning.
        byte[] localLuminances = source.getMatrix();
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                int pixel = localLuminances[offset + x] & 0xff;
                if (pixel < blackPoint) {
                    matrix.set(x, y);
                }
            }
        }

        return matrix;
    }

    public Binarizer createBinarizer(LuminanceSource source) {
        return new GlobalBinarizer(source, thresholder);
    }

    private void initArrays(int luminanceSize) {
        if (luminances == null || luminances.length < luminanceSize) {
            luminances = new byte[luminanceSize];
        }
        if (buckets == null) {
            buckets = new int[LUMINANCE_BUCKETS];
        } else {
            for (int x = 0; x < LUMINANCE_BUCKETS; x++) {
                buckets[x] = 0;
            }
        }
    }

    private int[] getHistogram() {
        LuminanceSource source = getLuminanceSource();
        int width = source.getWidth();
        int height = source.getHeight();
        initArrays(width);
        int[] localBuckets = buckets;
        for (int y = 1; y < 5; y++) {
            int row = height * y / 5;
            byte[] localLuminances = source.getRow(row, luminances);
            int right = (width << 2) / 5;
            for (int x = width / 5; x < right; x++) {
                int pixel = localLuminances[x] & 0xff;
                localBuckets[pixel >> LUMINANCE_SHIFT]++;
            }
        }
        return localBuckets;
    }
}
