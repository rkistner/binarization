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
public class AverageThresholder implements ThresholdFinder {
    public int findThreshold(int[] buckets) {
        int numBuckets = buckets.length;
        int total = 0;
        int atotal = 0;
        for (int x = 0; x < numBuckets; x++) {
            total += buckets[x];
            atotal += x * buckets[x];
        }
        if(total > 0)
            return atotal / total;
        else
            return 0;
    }

    public String toString() {
        return "Average";
    }
}
