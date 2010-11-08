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

#ifndef LOCALAVERAGEBINARIZER_H_
#define LOCALAVERAGEBINARIZER_H_


#include <zxing/Binarizer.h>
#include <zxing/common/BitMatrix.h>

namespace zxing {
class LocalAverageBinarizer : public Binarizer {
public:
	LocalAverageBinarizer(Ref<LuminanceSource> source);
  virtual ~LocalAverageBinarizer();

  virtual Ref<BitMatrix> estimateBlackMatrix();
  Ref<BitArray> estimateBlackRow(int y, Ref<BitArray> row);
  
private:
  void calcBlockTotals(unsigned char* data, int* output, int width, int height, int aw, int ah);
  void cumulative(int* data, int* output, int width, int height);
};

}

#endif /* LOCALAVERAGEBINARIZER_H_ */

