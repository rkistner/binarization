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
public class MedianThresholder implements ThresholdFinder {
    public int findThreshold(int[] buckets) {
        int numBuckets = buckets.length;
        int total = 0;
        for (int x = 0; x < numBuckets; x++) {
            total += buckets[x];
        }

        int count = 0;
        for (int x = 0; x < numBuckets; x++) {
            count += buckets[x];
            if (count * 2 >= total) {
                return x;
            }
        }
        return buckets.length - 1;
    }

    public String toString() {
        return "Median";
    }
}