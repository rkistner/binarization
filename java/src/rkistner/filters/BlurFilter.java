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
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 *
 */
public class BlurFilter implements Filter {
    public static FilterFactory FACTORY = new FilterFactory() {
        public Filter createFilter(float... parameters) {
            return new BlurFilter(parameters[0] * .1f);
        }

        public int parameters() {
            return 1;
        }

        public String[] parameterNames() {
            return new String[]{"Blur"};
        }
    };

    public static Kernel circularKernel(float radius) {
        if (radius < 1f)
            return new Kernel(1, 1, new float[]{1f});
        int iradius = (int) (radius + 0.99f);    // Round up
        int s = iradius * 2 + 1;
        float[] data = new float[s * s];
        double area = Math.PI * radius * radius;
        float intensity = (float) (1 / area);
        for (int x = 0; x < s; x++) {
            for (int y = 0; y < s; y++) {
                float dx = iradius - x;
                float dy = iradius - y;

                // We do some rough anti-aliasing
                float r = (float) Math.sqrt(dx * dx + dy * dy);
                float k = radius + 0.5f - r;
                if (k < 0f)
                    k = 0f;
                else if (k > 1f)
                    k = 1f;
                data[y * s + x] = k * intensity;
            }
        }

        return new Kernel(s, s, data);
    }


    private float fraction;

    public BlurFilter(float fraction) {
        this.fraction = fraction;
    }

    public BufferedImage filter(BufferedImage source, boolean reuse) {
        float radius = source.getWidth() * fraction / 2;
        ConvolveOp op = new ConvolveOp(circularKernel(radius), ConvolveOp.EDGE_NO_OP, null);
        return op.filter(source, null);
    }
}
