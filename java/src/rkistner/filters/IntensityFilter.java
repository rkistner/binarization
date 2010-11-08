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
import java.awt.image.RescaleOp;

/**
 *
 */
public class IntensityFilter implements Filter {
    public static FilterFactory FACTORY = new FilterFactory() {
        public Filter createFilter(float... parameters) {
            return new IntensityFilter(1f - 0.8f*parameters[0], 0.5f);
        }

        public int parameters() {
            return 1;
        }

        public String[] parameterNames() {
            return new String[] { "Contrast" };
        }
    };

    private RescaleOp op;

    public IntensityFilter(float scale, float center) {
        op = new RescaleOp(scale, (center-scale/2)*256, null);
    }

    public BufferedImage filter(BufferedImage source, boolean reuse) {
        return op.filter(source, reuse ? source : null);
    }
}
