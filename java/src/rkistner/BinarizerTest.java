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

package rkistner;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeReader;
import rkistner.algorithms.BinarizerFactory;

/**
 *
 */
public class BinarizerTest {

    private BinarizerFactory factory;

    public BinarizerTest(BinarizerFactory factory) {
        this.factory = factory;
    }

    public TestResult test(TestImageSource source, boolean storeImages) {
        LuminanceSource lum = new BufferedImageLuminanceSource(source.getTestImage());
        Binarizer bin = factory.getBinarizer(lum);
        BitMatrix binary = null;
        TestResult result = new TestResult();
        long start = System.nanoTime();
        try {
            BinaryBitmap bitmap = new BinaryBitmap(bin);
            binary = bitmap.getBlackMatrix();
            result.setBinarizeTime((int)(System.nanoTime()-start));
            Util.compare(binary, source.getBinaryImage(), result, storeImages);
            if(storeImages) {
                result.addImage("result", Util.fromBitMatrix(binary));
            }

            QRCodeReader reader = new QRCodeReader();
            Result decodeResult = reader.decode(bitmap);

            if(source.getData() != null && !source.getData().equals(decodeResult.getText()))
                result.setDecoded(false);   // false positive
            else
                result.setDecoded(true);
//            System.out.println(decodeResult.getText());
            
        } catch (NotFoundException e) {
//            e.printStackTrace();
        } catch (ChecksumException e) {
//            e.printStackTrace();
        } catch (FormatException e) {
//            e.printStackTrace();
        } catch(ArithmeticException e) {
//            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        result.setTotalTime((int)(System.nanoTime()-start));

        result.setSource(source);
        return result;
    }


}
