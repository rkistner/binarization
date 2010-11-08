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

/**
 * @author Ralf Kister <ralf.kistner@gmail.com>
 */
public class Category {
    private String phone;
    private String baseLight;
    private String shadowType;
    private String barcodeType;
    private String backgroundType;


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBaseLight() {
        return baseLight;
    }

    public void setBaseLight(String baseLight) {
        this.baseLight = baseLight;
    }

    public String getShadowType() {
        return shadowType;
    }

    public void setShadowType(String shadowType) {
        this.shadowType = shadowType;
    }

    public String getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeType(String barcodeType) {
        this.barcodeType = barcodeType;
    }

    public String getBackgroundType() {
        return backgroundType;
    }

    public void setBackgroundType(String backgroundType) {
        this.backgroundType = backgroundType;
    }

    public static Category fromPath(String path) {
        String[] tokens = path.split("/");
        Category c = new Category();
        c.setPhone(tokens[1]);
        c.setBaseLight(tokens[2]);
        if(tokens[3].contains("bg")) {
            c.setBackgroundType("pamphlet");
        } else {
            c.setBackgroundType("blank");
        }
        if(tokens[3].contains("al")) {
            c.setBarcodeType("al");
        } else if(tokens[3].contains("paper")) {
            c.setBarcodeType("paper");
        }

        c.setShadowType(tokens[4]);
        return c;
    }

    @Override
    public String toString() {
        return "Category{" +
                "phone='" + phone + '\'' +
                ", baseLight='" + baseLight + '\'' +
                ", shadowType='" + shadowType + '\'' +
                ", barcodeType='" + barcodeType + '\'' +
                ", backgroundType='" + backgroundType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Category category = (Category) o;

        if(backgroundType != null ? !backgroundType.equals(category.backgroundType) : category.backgroundType != null) {
            return false;
        }
        if(barcodeType != null ? !barcodeType.equals(category.barcodeType) : category.barcodeType != null) {
            return false;
        }
        if(baseLight != null ? !baseLight.equals(category.baseLight) : category.baseLight != null) {
            return false;
        }
        if(phone != null ? !phone.equals(category.phone) : category.phone != null) {
            return false;
        }
        if(shadowType != null ? !shadowType.equals(category.shadowType) : category.shadowType != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = phone != null ? phone.hashCode() : 0;
        result = 31 * result + (baseLight != null ? baseLight.hashCode() : 0);
        result = 31 * result + (shadowType != null ? shadowType.hashCode() : 0);
        result = 31 * result + (barcodeType != null ? barcodeType.hashCode() : 0);
        result = 31 * result + (backgroundType != null ? backgroundType.hashCode() : 0);
        return result;
    }

    public static String getCSVHeader() {
        return "Phone,Light,Shadow,Barcode,Background";
    }

    public String getCSV() {
        return phone + "," + baseLight + "," + shadowType + "," + barcodeType + "," + backgroundType;
    }
}
