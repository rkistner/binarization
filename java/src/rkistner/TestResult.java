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

import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class TestResult {
    private int falseBlack;
    private int falseWhite;

    private TestImageSource source;

    private boolean decoded = false;

    private int binarizeTime;
    private int totalTime;

    private Map<String, BufferedImage> images = new LinkedHashMap<String, BufferedImage>();


    /**
     * A score for the image. 1 is the best score, and 0 the worst.
     *
     * @return the score
     */
    public float getScore() {
        return (falseBlack + falseWhite) / (float) getTotalPixels();
    }

    public int getBinarizeTime() {
        return binarizeTime;
    }

    public void setBinarizeTime(int binarizeTime) {
        this.binarizeTime = binarizeTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public String toString() {
        return "" + getScore();
    }

    public void addImage(String name, BufferedImage image) {
        images.put(name, image);
    }

    public TestImageSource getSource() {
        return source;
    }

    public void setSource(TestImageSource source) {
        this.source = source;
        addImage("expected", source.getBinaryImage());
        addImage("input", source.getTestImage());
    }

    public Map<String, BufferedImage> allImages() {
        return images;
    }

    public int getFalseBlack() {
        return falseBlack;
    }

    public void setFalseBlack(int falseBlack) {
        this.falseBlack = falseBlack;
    }

    public int getFalseWhite() {
        return falseWhite;
    }

    public void setFalseWhite(int falseWhite) {
        this.falseWhite = falseWhite;
    }

    public boolean isDecoded() {
        return decoded;
    }

    public void setDecoded(boolean decoded) {
        this.decoded = decoded;
    }

    public int getTotalPixels() {
        return source.getTestImage().getWidth() * source.getTestImage().getHeight();
    }

    public Map<String, BufferedImage> getScaledImages(int scale) {
        Map<String, BufferedImage> result = new LinkedHashMap<String, BufferedImage>();
        for(Map.Entry<String, BufferedImage> entry : images.entrySet()) {
            result.put(entry.getKey(), Util.scale(entry.getValue(), scale));
        }
        return result;
    }
}
