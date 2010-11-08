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
 * Algorithm from:
 * <p/>
 * Lecture 4: Thresholding
 * c Bryan S. Morse, Brigham Young University, 1998–2000
 * Last modified on Wednesday, January 12, 2000 at 10:00 AM.
 */
public class KittlerThresholder implements ThresholdFinder {
    public int findThreshold(int[] h) {
        int N = h.length;
        int H = 0;
        int M = 0;
        for (int i = 0; i < N; i++) {
            H += h[i];
            M += i * h[i];
        }

        double min = Float.MAX_VALUE;
        int best = 0;
        int Hf = 0;
        int Mf = 0;
        for (int T = 1; T < N; T++) {
            // Separate into two clusters.
            // First is from 0 to T-1
            // Second is from T to n-1
            Hf += h[T - 1];
            int Hb = H - Hf;

            Mf += (T - 1) * h[T - 1];
            int Mb = M - Mf;

            if (Hf == 0 || Hb == 0)
                continue;

            double mu_f = (double) Mf / Hf;
            double mu_b = (double) Mb / Hb;
            double Pf = (double) Hf / H;
            double Pb = (double) Hb / H;


            double var_f = 0;
            for(int i = 0; i < T; i++) {
                var_f += (i - mu_f)*(i - mu_f) * h[i];
            }
            var_f /= Hf;


            double var_b = 0;
            for(int i = T; i < N; i++) {
                var_b += (i - mu_b)*(i - mu_b) * h[i];
            }
            var_b /= Hb;


            double J = 1 + Pf *Math.log(var_f) + Pb *Math.log(var_b) - 2* Pf *Math.log(Pf) - 2* Pb *Math.log(Pb);

            if (J > 0 && J < min) {
                min = J;
                best = T;
            }
        }
        
        return best;
    }

    public String toString() {
        return "Kittler";
    }
}