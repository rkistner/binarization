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

/**
 *
 */
public class SpotLight implements Filter {
    public static FilterFactory FACTORY = new FilterFactory() {
        public Filter createFilter(float... parameters) {
            return new SpotLight(parameters[0], 0.5f, .2f + parameters[1]);
        }

        public int parameters() {
            return 2;
        }

        public String[] parameterNames() {
            return new String[]{"Light Position", "Light Radius"};
        }
    };

    public static FilterFactory RADIUS_FACTORY = new FilterFactory() {
        public Filter createFilter(float... parameters) {
            return new SpotLight(0.5f, 0.5f, 1.2f - parameters[0]);
        }

        public int parameters() {
            return 1;
        }

        public String[] parameterNames() {
            return new String[]{ "Light" };
        }
    };

    private float px;
    private float py;
    private float radius;

    public SpotLight(float px, float py, float radius) {
        this.px = px;
        this.py = py;
        this.radius = radius;
    }

    public BufferedImage filter(BufferedImage source, boolean reuse) {
        int width = source.getWidth();
        int height = source.getHeight();
        int px = (int) (this.px * width);
        int py = (int) (this.py * height);
        int r = (int) (this.radius * height);
        BufferedImage newImage;
        if (reuse)
            newImage = source;
        else
            newImage = new BufferedImage(width, height, source.getType());
        Raster src = source.getRaster();
        WritableRaster dst = newImage.getRaster();
        int[] pixel = new int[src.getNumBands()];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixel = src.getPixel(x, y, pixel);
                int dx = px - x;
                int dy = py - y;
                double d = Math.sqrt(dx * dx + dy * dy);
                double f = (1.0 - Math.atan(d / r * 2.5 - 2.5)) / 2.0;
//                double f = (1.0 - Math.tanh(d/r*2.5-2.5))/2.0;
                for (int i = 0; i < pixel.length; i++) {
                    pixel[i] *= f;
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
