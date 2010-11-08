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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import rkistner.algorithms.*;
import rkistner.filters.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class VisualTest implements ChangeListener {
    private FilterFactory filters;
    private BinarizerFactory[] factories;


    private JFrame form;
    private List<JSlider> sliders;
    private JTextComponent barcodeField;
    private Container resultBox;

    private static final int L = 4000;

    private static Component hfill() {
        return new Box.Filler(new Dimension(0, 0), new Dimension(L, 0), new Dimension(L, 0));
    }

    private static Component vfill() {
        return new Box.Filler(new Dimension(0, 0), new Dimension(0, L), new Dimension(0, L));
    }

    public VisualTest(FilterFactory filters, BinarizerFactory[] factories) {
        this.filters = filters;
        this.factories = factories;


        form = new JFrame("Test");
        Box globalBox = Box.createHorizontalBox();
        Box sliderBox = Box.createVerticalBox();
        Box rBox = Box.createVerticalBox();
        
        GridLayout layout = new GridLayout(3, 2);
        layout.setHgap(10);
        layout.setVgap(10);
        resultBox = new JPanel(layout);


        sliderBox.add(new JLabel("Barcode"));
        barcodeField = new JTextField("Test");
        barcodeField.setMaximumSize(new Dimension(250, barcodeField.getMaximumSize().height));
        barcodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                update();
            }
        });
        sliderBox.add(barcodeField);

        sliders = new ArrayList<JSlider>();
        for(String param : filters.parameterNames()) {
            JSlider slider = new JSlider();
            sliderBox.add(new JLabel(param));
            sliderBox.add(slider);
            sliders.add(slider);
            slider.addChangeListener(this);
        }
        sliderBox.add(vfill());


        rBox.add(resultBox);
        rBox.add(vfill());

        globalBox.add(sliderBox);
        globalBox.add(rBox);
        globalBox.add(hfill());

        form.setLayout(new BorderLayout());
        form.add(globalBox, BorderLayout.CENTER);
        form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        update();
    }

    private void update() {
        int p = filters.parameters();
        float[] params = new float[p];
        for(int i = 0; i < p; i++) {
            int selected = sliders.get(i).getValue();
            params[i] = selected / 100f;
        }
        Filter filter = filters.createFilter(params);
        QRCodeWriter writer = new QRCodeWriter();

        try {
            BitMatrix matrix = writer.encode(barcodeField.getText(), BarcodeFormat.QR_CODE, 1, 1);
            BufferedImage base = Util.fromBitMatrix(matrix);
            base = Util.scale(base, 5);
            BufferedImage filtered = filter.filter(base, false);
            resultBox.removeAll();

            TestImageSource source = new TestImageSource(filtered, base);

            resultBox.add(new JLabel(new ImageIcon(Util.scale(source.getBinaryImage(), 2))));
            resultBox.add(new JLabel(new ImageIcon(Util.scale(source.getTestImage(), 2))));

            for (BinarizerFactory factory : factories) {
                BinarizerTest test = new BinarizerTest(factory);
                TestResult result = test.test(source, true);
                Map<String, BufferedImage> scaled = result.getScaledImages(2);

                Box box = Box.createVerticalBox();

                JLabel nameLabel = new JLabel(factory.toString());
                nameLabel.setOpaque(true);
                Color c;
                if(result.isDecoded()) {
                    c = Color.GREEN;
                } else {
                    c = Color.RED;
                }
                box.add(nameLabel);
                BufferedImage image = scaled.get("result");
                ImageIcon icon = new ImageIcon(image);
                JLabel label = new JLabel(icon);
                label.setBorder(new LineBorder(c, 7));
                box.add(label);
                resultBox.add(box);

            }

            resultBox.validate();



        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void stateChanged(ChangeEvent e) {
        update();
    }

    public void show() {
        form.setVisible(true);
    }

    public static void main(String[] args) {
        BinarizerFactory[] factories = new BinarizerFactory[] {
                WindowRNBinarizer.createFactory(4, 0.129f, 6),
                FastWindowBinarizer.createFactory(4, 0.129f),
                HybridBinarizer.createFactory(),
                SplitBinarizer.createFactory(3, 3),
                GlobalBinarizer.createFactory(new OtsuThresholder()),
                GlobalBinarizer.createFactory(new KittlerThresholder()),
                GlobalBinarizer.createFactory(new ZXingThresholder()),
                GlobalBinarizer.createFactory(new MedianThresholder()),
                GlobalBinarizer.createFactory(new AverageThresholder()),
                GlobalBinarizer.createFactory(new ConstantThresholder()),
        };

        FilterFactory filter = new ChainedFilter.ChainFactory(Arrays.asList(IntensityFilter.FACTORY, BlurFilter.FACTORY, SpotLight.FACTORY, WhiteNoise.FACTORY));
        VisualTest vt = new VisualTest(filter, factories);
        vt.show();
    }
}
