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
public class OtsuThresholder implements ThresholdFinder {

    public int findThreshold(int[] h) {
        int N = h.length;
        int H = 0;
        int M = 0;
        for (int i = 0; i < N; i++) {
            H += h[i];
            M += i * h[i];
        }

        float max = 0f;
        int best = 0;
        int Hf = 0;
        int Mf = 0;
        for (int T = 1; T < N; T++) {
            // Separate into two clusters.
            // First is from 0 to T-1
            // Second is from T to N-1
            Hf += h[T - 1];
            int Hb = H - Hf;
            
            Mf += (T - 1) * h[T - 1];
            int Mb = M - Mf;

            if (Hf == 0 || Hb == 0)
                continue;

            float mu_f = (float) Mf / Hf;
            float mu_b = (float) Mb / Hb;
            float Pf = (float) Hf / H;
            float Pb = (float) Hb / H;

            float var_between = Pf * Pb * (mu_f - mu_b) * (mu_f - mu_b);
            if (var_between > max) {
                max = var_between;
                best = T;
            }
        }
        
        return best;
    }

    public String toString() {
        return "Otsu";
    }
}
