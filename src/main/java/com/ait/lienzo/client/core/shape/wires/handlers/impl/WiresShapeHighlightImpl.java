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

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import com.ait.lienzo.client.core.types.Point2D;

public class WiresShapeHighlightImpl implements WiresShapeHighlight<PickerPart.ShapePart>
{
    private final int  m_borderSize;

    private WiresShape m_parent;

    private String     m_priorColor;

    private Double     m_priorSize;

    private Double     m_priorAlpha;

    private MultiPath  m_path;

    public WiresShapeHighlightImpl(final int borderSize)
    {
        this.m_borderSize = borderSize;
    }

    @Override
    public void highlight(final WiresShape shape, final PickerPart.ShapePart part)
    {
        highlight(shape, part, "#0000FF");
    }

    @Override
    public void error(final WiresShape shape, final PickerPart.ShapePart part)
    {
        highlight(shape, part, "#FF0000");
    }

    @Override
    public void restore()
    {
        doRestore();
    }

    private void highlight(final WiresShape shape, final PickerPart.ShapePart part, final String color)
    {
        switch (part)
        {
            case BODY:
                highlightBody(shape, color);
                break;
            default:
                highlightBorder(shape);
        }
    }

    private void highlightBody(final WiresShape parent, final String color)
    {
        if (!isBodyHighlight())
        {
            m_priorColor = parent.getPath().getStrokeColor();
            m_priorAlpha = parent.getPath().getStrokeAlpha();
            m_priorSize = parent.getPath().getStrokeWidth();
            parent.getPath().setStrokeColor(color);
            parent.getPath().setStrokeAlpha(0.8);
            parent.getPath().setStrokeWidth(m_priorSize > 0 ? m_priorSize * 2.5 : 3d);
            this.m_parent = parent;

            drawLayer();
        }
    }

    private void highlightBorder(final WiresShape parent)
    {
        if (null == m_path)
        {
            final MultiPath path = parent.getPath();

            m_path = path.copy();

            m_path.setStrokeWidth(m_borderSize);

            final Point2D absLoc = path.getComputedLocation();

            m_path.setX(absLoc.getX());

            m_path.setY(absLoc.getY());

            m_path.setStrokeColor("#0000FF");

            m_path.setStrokeAlpha(0.8);

            parent.getGroup().getOverLayer().add(m_path);

            this.m_parent = parent;

            drawOverLayer();
        }
    }

    private void doRestore()
    {
        restoreBody();

        restoreBorder();

        m_parent = null;
    }

    private void restoreBody()
    {
        if (isBodyHighlight())
        {
            m_parent.getPath().setStrokeColor(m_priorColor);
            m_parent.getPath().setStrokeAlpha(getPriorAlpha());
            m_parent.getPath().setStrokeWidth(m_priorSize);
            m_priorColor = null;
            m_priorSize = null;
            m_priorAlpha = null;

            drawLayer();
        }
    }

    private void restoreBorder()
    {
        if (null != m_path)
        {
            m_path.removeFromParent();

            m_path = null;

            drawOverLayer();
        }
    }

    private boolean isBodyHighlight()
    {
        return (null != m_priorColor) || (null != m_priorAlpha);
    }

    private double getPriorAlpha()
    {
        return null != m_priorAlpha ? m_priorAlpha : 0d;
    }

    private void drawLayer()
    {
        if (null != m_parent)
        {
            m_parent.getGroup().getLayer().batch();
        }
    }

    private void drawOverLayer()
    {
        if (null != m_parent)
        {
            m_parent.getGroup().getOverLayer().batch();
        }
    }
}
