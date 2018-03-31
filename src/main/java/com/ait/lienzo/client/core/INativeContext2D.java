/*
 * Copyright (c) 2018 Ahome' Innovation Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.client.core;

import com.ait.lienzo.client.core.Path2D.NativePath2D;
import com.ait.lienzo.client.core.types.ImageData;
import com.ait.lienzo.client.core.types.LinearGradient.LinearGradientJSO;
import com.ait.lienzo.client.core.types.PathPartList.PathPartListJSO;
import com.ait.lienzo.client.core.types.PatternGradient.PatternGradientJSO;
import com.ait.lienzo.client.core.types.RadialGradient.RadialGradientJSO;
import com.ait.lienzo.client.core.types.Shadow.ShadowJSO;
import com.ait.lienzo.client.core.types.TextMetrics;
import com.ait.lienzo.client.core.types.Transform.TransformJSO;
import com.ait.tooling.nativetools.client.collection.NFastDoubleArrayJSO;
import com.google.gwt.dom.client.Element;

public interface INativeContext2D
{
    public void initDeviceRatio();

    public void saveContainer();

    public void restoreContainer();

    public void save();

    public void restore();

    public void beginPath();

    public void closePath();

    public void moveTo(double x, double y);

    public void lineTo(double x, double y);

    public void setGlobalCompositeOperation(String operation);

    public void setLineCap(String lineCap);

    public void setLineJoin(String lineJoin);

    public void quadraticCurveTo(double cpx, double cpy, double x, double y);

    public void arc(double x, double y, double radius, double startAngle, double endAngle);

    public void arc(double x, double y, double radius, double startAngle, double endAngle, boolean antiClockwise);

    public void ellipse(double x, double y, double rx, double ry, double ro, double sa, double ea, boolean ac);

    public void ellipse(double x, double y, double rx, double ry, double ro, double sa, double ea);

    public void arcTo(double x1, double y1, double x2, double y2, double radius);

    public void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y);

    public void clearRect(double x, double y, double w, double h);

    public void clip();

    public void fill();

    public void stroke();

    public void fillRect(double x, double y, double w, double h);

    public void fillText(String text, double x, double y);

    public void fillTextWithGradient(String text, double x, double y, double sx, double sy, double ex, double ey, String color);

    public void fillText(String text, double x, double y, double maxWidth);

    public void setFillColor(String fill);

    public void rect(double x, double y, double w, double h);

    public void rotate(double angle);

    public void scale(double sx, double sy);

    public void setStrokeColor(String color);

    public void setStrokeWidth(double width);

    public void setImageSmoothingEnabled(boolean enabled);

    public void setFillGradient(LinearGradientJSO grad);

    public void setFillGradient(PatternGradientJSO grad);

    public void setFillGradient(RadialGradientJSO grad);

    public void transform(TransformJSO jso);

    public void transform(double d0, double d1, double d2, double d3, double d4, double d5);

    public void setTransform(TransformJSO jso);

    public void setTransform(double d0, double d1, double d2, double d3, double d4, double d5);

    public void setToIdentityTransform();

    public void setTextFont(String font);

    public void setTextBaseline(String baseline);

    public void setTextAlign(String align);

    public void strokeText(String text, double x, double y);

    public void setGlobalAlpha(double alpha);

    public void translate(double x, double y);

    public void setShadow(ShadowJSO shadow);

    public boolean isSupported(String feature);

    public boolean isPointInPath(double x, double y);

    public ImageData getImageData(double x, double y, double width, double height);

    public ImageData createImageData(double width, double height);

    public ImageData createImageData(ImageData data);

    public void putImageData(ImageData imageData, double x, double y);

    public void putImageData(ImageData imageData, double x, double y, double dx, double dy, double dw, double dh);

    public TextMetrics measureText(String text);

    public void drawImage(Element image, double x, double y);

    public void drawImage(Element image, double x, double y, double w, double h);

    public void drawImage(Element image, double sx, double sy, double sw, double sh, double x, double y, double w, double h);

    public void resetClip();

    public void setMiterLimit(double limit);

    public void setLineDash(NFastDoubleArrayJSO dashes);

    public void setLineDashOffset(double offset);

    public double getBackingStorePixelRatio();

    public boolean path(PathPartListJSO list);

    public boolean clip(PathPartListJSO list);

    public void fill(NativePath2D path);

    public void stroke(NativePath2D path);

    public void clip(NativePath2D path);

    public NativePath2D getCurrentPath();

    public void setCurrentPath(NativePath2D path);
}