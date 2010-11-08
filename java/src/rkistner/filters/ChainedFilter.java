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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class ChainedFilter implements Filter {
    public static class ChainFactory implements FilterFactory {
        private List<FilterFactory> factories;

        public ChainFactory(List<FilterFactory> factories) {
            this.factories = factories;
        }

        public Filter createFilter(float... parameters) {
            int i = 0;
            List<Filter> filters = new ArrayList<Filter>();
            for (FilterFactory factory : factories) {
                int p = factory.parameters();
                float[] pa = new float[p];
                for(int j = 0; j < p; j++) {
                    pa[j] = parameters[i+j];
                }
                i += p;
                filters.add(factory.createFilter(pa));

            }
            return new ChainedFilter(filters);
        }

        public int parameters() {
            int total = 0;
            for (FilterFactory factory : factories) {
                total += factory.parameters();
            }
            return total;
        }

        public String[] parameterNames() {
            List<String> names = new ArrayList<String>();
            for (FilterFactory factory : factories) {
                names.addAll(Arrays.asList(factory.parameterNames()));
            }
            return names.toArray(new String[names.size()]);
        }
    }

    private List<Filter> filters = new ArrayList<Filter>();

    public ChainedFilter(Collection<Filter> filters) {
        this.filters.addAll(filters);
    }

    public ChainedFilter() {
    }

    public void addFilter(Filter f) {
        this.filters.add(f);
    }

    public BufferedImage filter(BufferedImage source, boolean reuse) {
        if(filters.isEmpty())
            return source;
        BufferedImage image = source;
        boolean first = true;
        for (Filter filter : filters) {
            if(first) {
                image = filter.filter(image, reuse);
                first = false;
            } else {
                image = filter.filter(image, true);
            }
        }
        return image;
    }
}
