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

import com.google.zxing.Binarizer;
import com.google.zxing.LuminanceSource;

/**
 * @author Ralf Kister <ralf.kistner@gmail.com>
 */
public class HybridBinarizer {
    public static BinarizerFactory createFactory() {
        return new BinarizerFactory() {
            public Binarizer getBinarizer(LuminanceSource source) {
                return new com.google.zxing.common.HybridBinarizer(source);
            }
        };
    }
}
