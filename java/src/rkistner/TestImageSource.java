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
public class TestImageSource {
    protected BufferedImage binaryImage;
    protected BufferedImage testImage;
    private String name;
    private Map<String, Float> parameters = new LinkedHashMap<String, Float>();
    private String data;

    public TestImageSource(BufferedImage testImage, BufferedImage binaryImage) {
        this.testImage = testImage;
        this.binaryImage = binaryImage;
    }

    public TestImageSource() {
    }

    public BufferedImage getBinaryImage() {
        return binaryImage;
    }

    public BufferedImage getTestImage() {
        return testImage;
    }

    public Map<String, Float> getParameters() {
        return parameters;
    }

    public void addParameter(String name, float value) {
        parameters.put(name, value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TestImageSource{" +
                "name='" + name + '\'' +
                '}';
    }
}
