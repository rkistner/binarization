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

import rkistner.algorithms.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author Ralf Kister <ralf.kistner@gmail.com>
 */
public class BatchTest {
    private static final FileFilter IMAGE_FILTER = new FileFilter() {
        public boolean accept(File pathname) {
            return pathname.isFile() && pathname.getName().endsWith(".jpg");
        }
    };

    private static final FileFilter FOLDER_FILTER = new FileFilter() {
        public boolean accept(File pathname) {
            return pathname.isDirectory() && !pathname.getName().contains("junk") && !pathname.getName().contains("move");
        }
    };

    private static List<File> allFiles(File root, int limit) {
        List<File> files = new ArrayList<File>();
        List<File> images = Arrays.asList(root.listFiles(IMAGE_FILTER));
        if(limit > 0 && images.size() > limit)
            files.addAll(images.subList(0, limit));
        else
            files.addAll(images);
        
        for(File folder : root.listFiles(FOLDER_FILTER)) {
            files.addAll(allFiles(folder, limit));
        }
        return files;
    }

    private static final String root = "images";

    private static Map<Category, List<File>> catFiles = new HashMap<Category, List<File>>();

    private static List<File> getFiles(Category cat) {
        if(!catFiles.containsKey(cat)) {
            catFiles.put(cat, new ArrayList<File>());
        }
        return catFiles.get(cat);
    }

    static List<File> allFiles;

    private static void locateFiles(int limit) {
        File f = new File(root);
        allFiles = allFiles(f, limit);
        for(File file : allFiles) {
            List<File> files = getFiles(Category.fromPath(file.getPath()));
            files.add(file);
        }
    }



    private static void allTest() throws IOException {
        int count = 0;
        
        BinarizerFactory[] factories = new BinarizerFactory[] {
                GlobalBinarizer.createFactory(new OtsuThresholder()),
                GlobalBinarizer.createFactory(new ZXingThresholder()),
                rkistner.algorithms.HybridBinarizer.createFactory(),
                SimpleWindowBinarizer.createFactory(0.13f),
        };
        int nf = factories.length;

        PrintWriter out = new PrintWriter(new File("results.csv"));

        out.print(Category.getCSVHeader());
        out.print(",file");
        for(BinarizerFactory factory : factories) {
            out.print("," + factory);
            out.print("," + factory + "-T");
            out.print("," + factory + "-TT");
        }
        out.println();

        // Just to trigger JIT optimizations before we start testing the performance
        int counter = 0;
        outer:
        for (List<File> files : catFiles.values()) {
            for (File file : files) {
                if(++counter == 100)
                    break outer;
                BufferedImage img = ImageIO.read(file);
                TestImageSource source = new TestImageSource(img, null);
                for(BinarizerFactory factory : factories) {
                    BinarizerTest test = new BinarizerTest(factory);
                    test.test(source, false);
                }
            }
        }

        for(int scale = 0; scale < 1; scale++) {
            for(Map.Entry<Category, List<File>> entry : catFiles.entrySet()) {
                Category cat = entry.getKey();
                List<File> files = entry.getValue();
                System.out.println();
                System.out.println();
                System.out.println(cat);


                for(File file : files) {
                    System.out.println("Loading " + file + " (" + ++count + " out of " + allFiles.size() + ")");


                    BufferedImage img = ImageIO.read(file);
                    if(scale == 1)
                        img = Util.scale(img, 0.5);
                    TestImageSource source = new TestImageSource(img, null);
                    source.setName(file.getPath());

                    int i = 0;

                    if(scale == 1) {
                        out.print("scaled" + cat.getCSV());
                        out.print(",scaled/" + file.getPath());
                    } else {
                        out.print(cat.getCSV());
                        out.print("," + file.getPath());
                    }

                    for(BinarizerFactory factory : factories) {
                        BinarizerTest test = new BinarizerTest(factory);
                        TestResult result = test.test(source, false);

                        out.print("," + (result.isDecoded() ? 1 : 0));
                        out.print("," + (result.getBinarizeTime()));
                        out.print("," + (result.getTotalTime()));
                        i++;
                    }
                    out.println();

                }
            }
        }

        out.flush();
        out.close();
    }


    public static void main(String[] args) throws IOException {
        locateFiles(0);
        allTest();
    }
}
