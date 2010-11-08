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
 * @author Ralf Kister <ralf.kistner@gmail.com>
 */
public class KapurThresholder implements ThresholdFinder {
    public int findThreshold(int[] h) {
        int n = h.length;
        int total = 0;

        for (int i = 0; i < n; i++) {
            total += h[i];
        }

        double[] p = new double[n];
        for (int i = 0; i < n; i++) {
            p[i] = (double) h[i] / total;
        }
        

        double max = Double.MIN_VALUE;
        int best = 0;
        for (int T = 1; T < n; T++) {
            // Separate into two clusters.
            // First is from 0 to T-1
            // Second is from T to n-1
            double sum1a = 0;
            double sum1b = 0;
            for(int i = 0; i < T; i++) {
                if(p[i] > 0) {
                    sum1a += p[i];
                    sum1b += p[i] * Math.log(p[i]);
                }
            }

            double sum2a = 0;
            double sum2b = 0;
            for(int i = T; i < n; i++) {
                if(p[i] > 0) {
                    sum2a += p[i];
                    sum2b += p[i] * Math.log(p[i]);
                }
            }


            double theTotal = Math.log(sum1a) + Math.log(sum2a) - sum1b/sum1a - sum2b/sum2a;

            if (theTotal > max) {
                max = theTotal;
                best = T;
            }
        }
        return best;
    }

    public String toString() {
        return "Kapur";
    }
}
