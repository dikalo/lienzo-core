/*
   Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.NativeContext2D;
import com.ait.lienzo.client.core.Path2D;
import com.ait.lienzo.client.core.types.*;
import com.ait.lienzo.shared.core.types.*;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;

/**
 * Wrapper around a JSO that serves as a proxy to access the native capabilities of Canvas 2D.
 * @see {@link NativeContext2D}
 */
public class ReplicatingContext2D extends Context2D
{
    private final Context2D m_replicatedContext;

    private boolean m_drag;

    public ReplicatingContext2D(final CanvasElement element, final Context2D replicatedContext)
    {
        super(element);
        m_drag = isDrag();

        m_replicatedContext = replicatedContext;
    }

    @Override
    public NativeContext2D getNativeContext()
    {
        return super.getNativeContext();
    }

    @Override
    public void save()
    {
        m_replicatedContext.save();
        super.save();
    }

    @Override
    public void restore()
    {
        m_replicatedContext.restore();
        super.restore();
    }

    @Override
    public void beginPath()
    {
        m_replicatedContext.beginPath();
        super.beginPath();
    }

    @Override
    public void closePath()
    {
        m_replicatedContext.closePath();
        super.closePath();
    }

    @Override
    public void rect(final double x, final double y, final double w, final double h)
    {
        m_replicatedContext.rect(x, y, w, h);
        super.rect(x, y, w, h);
    }

    @Override
    public void fillRect(final double x, final double y, final double w, final double h)
    {
        m_replicatedContext.fillRect(x, y, w, h);
        super.fillRect(x, y, w, h);
    }

    @Override
    public void fill()
    {
        m_replicatedContext.fill();
        super.fill();
    }

    @Override
    public void stroke()
    {
        m_replicatedContext.stroke();
        super.stroke();
    }

    @Override
    public void setFillColor(final String color)
    {
        m_replicatedContext.setFillColor(color);
        super.setFillColor(color);
    }

    /**
     * Sets the fill color
     * 
     * @param color {@link ColorName} or {@link Color}
     * 
     * @return this Context2D
     */
    @Override
    public void setFillColor(final IColor color)
    {
        m_replicatedContext.setFillColor((null != color) ? color.getColorString() : null);
        super.setFillColor((null != color) ? color.getColorString() : null);
    }

    @Override
    public void arc(final double x, final double y, final double radius, final double startAngle, final double endAngle, final boolean antiClockwise)
    {
        m_replicatedContext.arc(x, y, radius, startAngle, endAngle, antiClockwise);
        super.arc(x, y, radius, startAngle, endAngle, antiClockwise);
    }

    @Override
    public void arc(final double x, final double y, final double radius, final double startAngle, final double endAngle)
    {
        m_replicatedContext.arc(x, y, radius, startAngle, endAngle, false);
        super.arc(x, y, radius, startAngle, endAngle, false);
    }

    @Override
    public void ellipse(final double x, final double y, final double radiusX, final double radiusY, final double rotation, final double startAngle, final double endAngle, final boolean antiClockwise)
    {
        m_replicatedContext.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle, antiClockwise);
        super.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle, antiClockwise);
    }

    @Override
    public void ellipse(final double x, final double y, final double radiusX, final double radiusY, final double rotation, final double startAngle, final double endAngle)
    {
        m_replicatedContext.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle);
        super.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle);
    }

    @Override
    public void arcTo(final double x1, final double y1, final double x2, final double y2, final double radius)
    {
        m_replicatedContext.arcTo(x1, y1, x2, y2, radius);
        super.arcTo(x1, y1, x2, y2, radius);
    }

    @Override
    public void setStrokeColor(final String color)
    {
        m_replicatedContext.setStrokeColor(color);
        super.setStrokeColor(color);
    }

    /**
     * Sets the stroke color
     * 
     * @param color {@link ColorName} or {@link Color}
     * 
     * @return this Context2D
     */
    @Override
    public void setStrokeColor(final IColor color)
    {
        m_replicatedContext.setStrokeColor((null != color) ? color.getColorString() : null);
        super.setStrokeColor((null != color) ? color.getColorString() : null);
    }

    @Override
    public void setStrokeWidth(final double width)
    {
        m_replicatedContext.setStrokeWidth(width);
        super.setStrokeWidth(width);
    }

    @Override
    public void setLineCap(final LineCap linecap)
    {
        m_replicatedContext.setLineCap(linecap);
        super.setLineCap(linecap);
    }

    @Override
    public void setLineJoin(final LineJoin linejoin)
    {
        m_replicatedContext.setLineJoin(linejoin);
        super.setLineJoin(linejoin);
    }

    @Override
    public void transform(final double d0, final double d1, final double d2, final double d3, final double d4, final double d5)
    {
        m_replicatedContext.transform(d0, d1, d2, d3, d4, d5);
        super.transform(d0, d1, d2, d3, d4, d5);
    }

    @Override
    public void setTransform(final double d0, final double d1, final double d2, final double d3, final double d4, final double d5)
    {
        m_replicatedContext.setTransform(d0, d1, d2, d3, d4, d5);
        super.setTransform(d0, d1, d2, d3, d4, d5);
    };

    @Override
    public void setToIdentityTransform()
    {
        m_replicatedContext.setToIdentityTransform();
        super.setToIdentityTransform();
    };

    @Override
    public void moveTo(final double x, final double y)
    {
        m_replicatedContext.moveTo(x, y);
        super.moveTo(x, y);
    }

    @Override
    public void bezierCurveTo(final double cp1x, final double cp1y, final double cp2x, final double cp2y, final double x, final double y)
    {
        m_replicatedContext.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
        super.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
    }

    @Override
    public void lineTo(final double x, final double y)
    {
        m_replicatedContext.lineTo(x, y);
        super.lineTo(x, y);

    }

    @Override
    public void setFillGradient(final LinearGradient gradient)
    {
        m_replicatedContext.setFillGradient(gradient);
        super.setFillGradient(gradient);
    }

    @Override
    public void setFillGradient(final RadialGradient gradient)
    {
        m_replicatedContext.setFillGradient(gradient);
        super.setFillGradient(gradient);
    }

    @Override
    public void setFillGradient(final PatternGradient gradient)
    {
        m_replicatedContext.setFillGradient(gradient);
        super.setFillGradient(gradient);
    }

    @Override
    public void quadraticCurveTo(final double cpx, final double cpy, final double x, final double y)
    {
        m_replicatedContext.quadraticCurveTo(cpx, cpy, x, y);
        super.quadraticCurveTo(cpx, cpy, x, y);
    }

    @Override
    public void transform(final Transform transform)
    {
        m_replicatedContext.transform(transform);
        super.transform(transform);
    }

    @Override
    public void setTransform(final Transform transform)
    {
        m_replicatedContext.setTransform(transform);
        super.setTransform(transform);
    }

    @Override
    public void fillTextWithGradient(final String text, final double x, final double y, final double sx, final double sy, final double ex, final double ey, final String color)
    {
        m_replicatedContext.fillTextWithGradient(text, x, y, sx, sy, ex, ey, color);
        super.fillTextWithGradient(text, x, y, sx, sy, ex, ey, color);
    }

    @Override
    public void setTextFont(final String font)
    {
        m_replicatedContext.setTextFont(font);
        super.setTextFont(font);
    }

    @Override
    public void setTextBaseline(final TextBaseLine baseline)
    {
        m_replicatedContext.setTextBaseline(baseline);
        super.setTextBaseline(baseline);
    }

    @Override
    public void setTextAlign(final TextAlign textAlign)
    {
        m_replicatedContext.setTextAlign(textAlign);
        super.setTextAlign(textAlign);
    }

    @Override
    public void fillText(final String text, final double x, final double y)
    {
        m_replicatedContext.fillText(text, x, y);
        super.fillText(text, x, y);
    }

    @Override
    public void strokeText(final String text, final double x, final double y)
    {
        m_replicatedContext.strokeText(text, x, y);
        super.strokeText(text, x, y);
    }

    @Override
    public void setGlobalAlpha(final double alpha)
    {
        m_replicatedContext.setGlobalAlpha(alpha);
        super.setGlobalAlpha(alpha);
    }

    @Override
    public void translate(final double x, final double y)
    {
        m_replicatedContext.translate(x, y);
        super.translate(x, y);
    }

    @Override
    public void rotate(final double rot)
    {
        m_replicatedContext.rotate(rot);
        super.rotate(rot);
    }

    @Override
    public void scale(final double sx, final double sy)
    {
        m_replicatedContext.scale(sx, sy);
        super.scale(sx, sy);
    }

    @Override
    public void clearRect(final double x, final double y, final double wide, final double high)
    {
        m_replicatedContext.clearRect(x, y, wide, high);
        super.clearRect(x, y, wide, high);
    }

    @Override
    public void setShadow(final Shadow shadow)
    {
        m_replicatedContext.setShadow(shadow);
        super.setShadow(shadow);
    }

    @Override
    public void clip()
    {
        m_replicatedContext.clip();
        super.clip();
    }

    @Override
    public void resetClip()
    {
        m_replicatedContext.resetClip();
        super.resetClip();
    }

    @Override
    public void setMiterLimit(final double limit)
    {
        m_replicatedContext.setMiterLimit(limit);
        super.setMiterLimit(limit);
    }

    @Override
    public boolean path(final PathPartList list)
    {
        m_replicatedContext.path(list);
        return super.path(list);
    }

    @Override
    public boolean clip(final PathPartList list)
    {
        m_replicatedContext.clip(list);
        return super.clip(list);
    }

    @Override
    public boolean isSupported(final String feature)
    {
        return super.isSupported(feature);
    }

    @Override
    public boolean isPointInPath(final double x, final double y)
    {
        return super.isPointInPath(x, y);
    }

    @Override
    public ImageDataPixelColor getImageDataPixelColor(final int x, final int y)
    {
        return new ImageDataPixelColor(getImageData(x, y, 1, 1));
    }

    @Override
    public ImageData getImageData(final int x, final int y, final int width, final int height)
    {
        return super.getImageData(x, y, width, height);
    }

    @Override
    public void putImageData(final ImageData imageData, final int x, final int y)
    {
        m_replicatedContext.putImageData(imageData, x, y);
        super.putImageData(imageData, x, y);
    }

    @Override
    public void putImageData(final ImageData imageData, final int x, final int y, final int dirtyX, final int dirtyY, final int dirtyWidth, final int dirtyHeight)
    {
        m_replicatedContext.putImageData(imageData, x, y, dirtyX, dirtyY, dirtyWidth, dirtyHeight);
        super.putImageData(imageData, x, y, dirtyX, dirtyY, dirtyWidth, dirtyHeight);
    }

    @Override
    public ImageData createImageData(final double width, final double height)
    {
        return super.createImageData(width, height);
    }

    @Override
    public ImageData createImageData(final ImageData data)
    {
        return super.createImageData(data);
    }

    @Override
    public TextMetrics measureText(final String text)
    {
        return super.measureText(text);
    }

    @Override
    public void setGlobalCompositeOperation(final CompositeOperation operation)
    {
        m_replicatedContext.setGlobalCompositeOperation(operation);
        super.setGlobalCompositeOperation(operation);
    }

    @Override
    public void setImageSmoothingEnabled(final boolean enabled)
    {
        m_replicatedContext.setImageSmoothingEnabled(enabled);
        super.setImageSmoothingEnabled(enabled);
    }

    @Override
    public void drawImage(final Element image, final double x, final double y)
    {
        m_replicatedContext.drawImage(image, x, y);
        super.drawImage(image, x, y);
    }

    @Override
    public void drawImage(final Element image, final double x, final double y, final double w, final double h)
    {
        m_replicatedContext.drawImage(image, x, y, w, h);
        super.drawImage(image, x, y, w, h);
    }

    @Override
    public void drawImage(final Element image, final double sx, final double sy, final double sw, final double sh, final double x, final double y, final double w, final double h)
    {
        m_replicatedContext.drawImage(image, sx, sy, sw, sh, x, y, w, h);
        super.drawImage(image, sx, sy, sw, sh, x, y, w, h);
    }

    @Override
    public void setLineDash(final DashArray dashes)
    {
        m_replicatedContext.setLineDash(dashes);
        super.setLineDash(dashes);
    }

    @Override
    public void setLineDashOffset(final double offset)
    {
        m_replicatedContext.setLineDashOffset(offset);
        super.setLineDashOffset(offset);
    }

    @Override
    public double getBackingStorePixelRatio()
    {
        return super.getBackingStorePixelRatio();
    }

    @Override
    public void fill(final Path2D path)
    {
        m_replicatedContext.fill(path);
        super.fill(path);
    }

    @Override
    public void stroke(final Path2D path)
    {
        m_replicatedContext.stroke(path);
        super.stroke(path);
    }

    @Override
    public void clip(final Path2D path)
    {
        m_replicatedContext.clip(path);
        super.clip(path);
    }

    @Override
    public Path2D getCurrentPath()
    {
        return super.getCurrentPath();
    }

    @Override
    public boolean isSelection()
    {
        return super.isSelection();
    }

    public void setDrag(boolean drag) {
        m_drag = drag;
    }

    @Override
    public boolean isDrag()
    {
        return m_drag;
    }

    @Override
    public boolean isRecording()
    {
        return true;
    }

    public ReplicatingContext2D start()
    {
        return this;
    }

    public ReplicatingContext2D stop()
    {
        return this;
    }

    public ReplicatingContext2D reset()
    {
        return this;
    }
}
