/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

package org.apache.xmlgraphics.ps;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;

import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

import org.apache.xmlgraphics.java2d.Dimension2DDouble;
import org.apache.xmlgraphics.java2d.color.NamedColorSpace;


public class FormGeneratorTestCase {
    @Test
    public void testGeneratePaintProc() throws IOException {
        Dimension2D dimension = new Dimension2DDouble(300, 500);
        BufferedImage im = new BufferedImage(100, 75, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageFormGenerator formImageGen = new  ImageFormGenerator("form", "title", dimension, im, false);
        PSGenerator gen = new PSGenerator(out);
        formImageGen.generatePaintProc(gen);
        String test = out.toString(StandardCharsets.UTF_8.name());

        String  expected = "    form:Data 0 setfileposition\n"
        + "[300 0 0 500 0 0] CT\n"
        + "/DeviceRGB setcolorspace\n"
        + "<<\n";
        Assert.assertTrue(test.contains(expected));
        Assert.assertTrue(test.contains("  /DataSource form:Data"));
        Assert.assertTrue(test.contains("  /ImageMatrix [100 0 0 75 0 0]\n"));
        Assert.assertTrue(test.contains("  /BitsPerComponent 8\n"));
        Assert.assertTrue(test.contains("  /Height 75\n"));
        Assert.assertTrue(test.contains("  /ImageType 1\n"));
        Assert.assertTrue(test.contains("  /Decode [0 1 0 1 0 1]\n"));
        Assert.assertTrue(test.contains(">> image\n"));
        out.reset();
        im = null;

        Color c = Color.BLUE;
        Dimension dimensionPX = new Dimension(200, 400);
        ImageEncoder enco = ImageEncodingHelper.createRenderedImageEncoder(im);
        ColorSpace cs = new NamedColorSpace("myColor", c);
        formImageGen = new  ImageFormGenerator("form", "title", dimension, dimensionPX, enco, cs, false);
        gen = new PSGenerator(out);
        gen.setPSLevel(2);
        formImageGen.generatePaintProc(gen);
        test = out.toString(StandardCharsets.UTF_8.name());
        expected = "    userdict /i 0 put\n"
            + "[300 0 0 500 0 0] CT\n"
            + "/DeviceGray setcolorspace\n"
            + "<<\n";
        Assert.assertTrue(test.contains(expected));
        Assert.assertTrue(test.contains("  /DataSource { form:Data i get /i i 1 add store } bind\n"));
        Assert.assertTrue(test.contains("  /ImageMatrix [200 0 0 400 0 0]\n"));
        Assert.assertTrue(test.contains("  /Height 400\n"));
        Assert.assertTrue(test.contains("  /BitsPerComponent 8\n"));
        Assert.assertTrue(test.contains("  /ImageType 1\n"));
        Assert.assertTrue(test.contains("  /Decode [0 1]\n"));
        Assert.assertTrue(test.contains("  /Width 200\n"));
        Assert.assertTrue(test.contains(">> image\n"));
    }

    @Test
    public void testFlateDecodeCommand() throws IOException {
        Dimension2D dimension = new Dimension2DDouble(300, 500);
        BufferedImage im = new BufferedImage(100, 75, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageFormGenerator formImageGen = new ImageFormGenerator("form", "title", dimension, im, false);
        PSGenerator gen = new PSGenerator(out);
        formImageGen.generate(gen);
        String test = out.toString(StandardCharsets.UTF_8.name());
        Assert.assertTrue(test.contains("/ASCII85Decode filter\n"));
        //FlateDecode at DataSource so executed on page load rather than document load so viewer loads faster
        Assert.assertTrue(test.contains("/DataSource form:Data /FlateDecode filter\n"));
    }

    @Test
    public void testAlphaImage() throws IOException {
        Assert.assertEquals(buildPSImage(BufferedImage.TYPE_4BYTE_ABGR), buildPSImage(BufferedImage.TYPE_INT_RGB));
    }

    private String buildPSImage(int type) throws IOException {
        Dimension2D dimension = new Dimension2DDouble(1, 1);
        BufferedImage im = new BufferedImage(1, 1, type);
        Graphics2D g = (Graphics2D) im.getGraphics();
        if (type == BufferedImage.TYPE_4BYTE_ABGR) {
            g.setBackground(new Color(0, 0, 0, 0));
        } else {
            g.setBackground(Color.white);
        }
        g.clearRect(0, 0, im.getWidth(), im.getHeight());
        g.drawImage(im, 0, 0, null);
        g.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageFormGenerator formImageGen = new  ImageFormGenerator("form", "title", dimension, im, false);
        PSGenerator gen = new PSGenerator(out);
        formImageGen.generate(gen);
        return out.toString(StandardCharsets.UTF_8.name());
    }
}
