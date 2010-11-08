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

#include "LocalAverageBinarizer.h"

namespace zxing {

const int BLOCK_SIZE = 6;
const float WINDOW_FRACTION = 0.13;

LocalAverageBinarizer::LocalAverageBinarizer(Ref<LuminanceSource> source) :
    Binarizer(source) {

}

LocalAverageBinarizer::~LocalAverageBinarizer() {
}


Ref<BitArray> LocalAverageBinarizer::estimateBlackRow(int y, Ref<BitArray> row) {
  //TODO: implement
  return Ref<BitArray>();
}

void LocalAverageBinarizer::calcBlockTotals(unsigned char* data, int* output, int width, int height, int aw, int ah) {
	for(int by = 0; by < ah; by++) {
	   int ey = (by+1)*BLOCK_SIZE;
	   for(int bx = 0; bx < aw; bx++) {
		   int t = 0;

		   for(int y = by*BLOCK_SIZE; y < ey; y++) {
			   int offset = y*width+bx*BLOCK_SIZE;
			   int ex = offset+BLOCK_SIZE;
			   for(; offset < ex; offset++) {
				   int v = data[offset] & 0xff;
				   t += v;
			   }
		   }
		   output[by*aw+bx] = t;
	   }
   }
}

void LocalAverageBinarizer::cumulative(int* data, int* output, int width, int height) {
	int ah = height;
	int aw = width;
	int ow = width + 1;
	//int[][] totals = new int[ah + 1][aw + 1];
	int* rowTotals = new int[ah*ow];


	for(int y = 0; y < ah; y++) {
		int* row = rowTotals + (y*ow);
		int* rowdata = data + (y*aw);
		int t = 0;
		row[0] = t;
		for(int x = 0; x < aw; x++) {
			t += rowdata[x];
			row[x + 1] = t;
		}
	}
	
	for(int x = 0; x <= aw; x++) {
		output[x] = 0;	// First row
		int t = 0;
		for(int y = 0; y < ah; y++) {
			t += rowTotals[y*ow+x];
			output[(y + 1)*ow+x] = t;
		}
	}
	
	delete[] rowTotals;
}

static int min(int a, int b) {
	return a < b ? a : b;
}

static int max(int a, int b) {
	return a > b ? a : b;
}

Ref<BitMatrix> LocalAverageBinarizer::estimateBlackMatrix() {
  Ref<LuminanceSource> source = getSource();
  unsigned char* data = source->copyMatrix();
  int width = source->getWidth();
  int height = source->getHeight();
  
  int r = (int)(min(width, height) * WINDOW_FRACTION / BLOCK_SIZE / 2 + 1);
  
  int aw = width / BLOCK_SIZE;
  int ah = height / BLOCK_SIZE;
  int ow = aw+1;
  
  int* blockTotals = new int[ah*aw];
  calcBlockTotals(data, blockTotals, width, height, aw, ah);
  
  int* totals = new int[(ah+1)*(aw+1)];
  cumulative(blockTotals, totals, aw, ah);
  
  Ref<BitMatrix> matrix(new BitMatrix(width, height));
  for(int by = 0; by < ah; by++) {
	  for(int bx = 0; bx < aw; bx++) {
		  int top = max(0, by - r + 1);
		  int left = max(0, bx - r + 1);
		  int bottom = min(ah, by + r);
		  int right = min(aw, bx + r);

		  int block = totals[bottom*ow+right] + totals[top*ow+left] - totals[top*ow+right] - totals[bottom*ow+left];


		  int pixels = (bottom - top) * (right - left) * BLOCK_SIZE * BLOCK_SIZE;
		  int avg = block / pixels;

		  for(int y = by*BLOCK_SIZE; y < (by+1)*BLOCK_SIZE; y++) {
			  for(int x = bx*BLOCK_SIZE; x < (bx+1)*BLOCK_SIZE; x++) {
				  int pixel = data[y*width + x];
				  if(pixel < avg)
					  matrix->set(x, y);
			  }
		  }
	  }
  }

  delete[] totals;
  delete[] blockTotals;
  delete[] data;

  return matrix;
}

} // namespace zxing

