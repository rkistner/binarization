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

package rkistner.filters;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Random;

/**
 *
 */
public class WhiteNoise implements Filter {
    public static FilterFactory FACTORY = new FilterFactory() {
        public Filter createFilter(float... parameters) {
            return new WhiteNoise(parameters[0] * 0.15f);
        }

        public int parameters() {
            return 1;
        }

        public String[] parameterNames() {
            return new String[]{ "Noise" };
        }
    };

    private float amount;
    private final Random random = new Random(0);    // Constant seed

    public WhiteNoise(float amount) {
        this.amount = amount;
    }

    public BufferedImage filter(BufferedImage source, boolean reuse) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage newImage;
        if (reuse)
            newImage = source;
        else
            newImage = new BufferedImage(width, height, source.getType());
        Raster src = source.getRaster();
        WritableRaster dst = newImage.getRaster();
        int[] pixel = new int[src.getNumBands()];
        int d = (int) (amount * 256);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixel = src.getPixel(x, y, pixel);
                int diff = d > 0 ? random.nextInt(2 * d) - d : 0;
                for (int i = 0; i < pixel.length; i++) {
                    pixel[i] += diff;
                    if (pixel[i] < 0)
                        pixel[i] = 0;
                    else if (pixel[i] > 255)
                        pixel[i] = 255;
                }
                dst.setPixel(x, y, pixel);
            }
        }
        return newImage;
    }
}
