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

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.MouseEvent;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMouseControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.types.Point2D;

public class WiresParentPickerControlImpl implements WiresParentPickerControl, WiresMouseControl
{
    private final WiresShapeLocationControlImpl shapeLocationControl;

    private final ColorMapBackedPickerProvider  colorMapBackedPickerProvider;

    private WiresContainer                      m_parent;

    private ColorMapBackedPicker                m_picker;

    private PickerPart                          m_parentPart;

    private WiresContainer                      initialParent;

    public WiresParentPickerControlImpl(final WiresShape m_shape, final ColorMapBackedPicker.PickerOptions pickerOptions)
    {
        this(new WiresShapeLocationControlImpl(m_shape), pickerOptions);
    }

    public WiresParentPickerControlImpl(final WiresShapeLocationControlImpl shapeLocationControl, final ColorMapBackedPicker.PickerOptions pickerOptions)
    {
        this.shapeLocationControl = shapeLocationControl;

        this.colorMapBackedPickerProvider = new ColorMapBackedPickerProviderImpl(pickerOptions);
    }

    public WiresParentPickerControlImpl(final WiresShapeLocationControlImpl shapeLocationControl, final ColorMapBackedPickerProvider colorMapBackedPickerProvider)
    {
        this.shapeLocationControl = shapeLocationControl;

        this.colorMapBackedPickerProvider = colorMapBackedPickerProvider;
    }

    @Override
    public void onMoveStart(final double x, final double y)
    {
        shapeLocationControl.onMoveStart(x, y);

        initialParent = getShape().getParent();

        m_parent = getShape().getParent();

        rebuildPicker();

        if ((m_parent != null) && (m_parent instanceof WiresShape))
        {
            if (getShape().getDockedTo() == null)
            {
                m_parentPart = new PickerPart((WiresShape) m_parent, PickerPart.ShapePart.BODY);
            }
            else
            {
                m_parentPart = findShapeAt((int) shapeLocationControl.getShapeStartCenterX(), (int) shapeLocationControl.getShapeStartCenterY());
            }
        }
    }

    public void rebuildPicker()
    {
        m_picker = colorMapBackedPickerProvider.get(getShape().getWiresManager().getLayer());
    }

    @Override
    public Point2D getCurrentLocation()
    {
        return shapeLocationControl.getCurrentLocation();
    }

    @Override
    public boolean onMove(final double dx, final double dy)
    {
        if (!shapeLocationControl.onMove(dx, dy))
        {
            final Point2D currentLocation = getCurrentLocation();

            final double x = currentLocation.getX();

            final double y = currentLocation.getY();

            WiresContainer parent = null;

            PickerPart parentPart = findShapeAt(x, y);

            if (parentPart != null)
            {
                parent = parentPart.getShape();
            }
            if ((parent != m_parent) || (parentPart != m_parentPart))
            {
                parentPart = findShapeAt(x, y);

                parent = null != parentPart ? parentPart.getShape() : null;
            }
            m_parent = parent;

            m_parentPart = parentPart;
        }
        return false;
    }

    @Override
    public void onMoveAdjusted(final Point2D dxy)
    {
        shapeLocationControl.onMoveAdjusted(dxy);
    }

    private PickerPart findShapeAt(final double x, final double y)
    {
        final PickerPart parent = m_picker.findShapeAt((int) x, (int) y);

        // Ensure same shape is not the parent found, even if it
        // has been indexed in the colormap picker.

        if ((null != parent) && (parent.getShape() != getShape()))
        {
            return parent;
        }
        return null;
    }

    @Override
    public Point2D getAdjust()
    {
        return shapeLocationControl.getAdjust();
    }

    @Override
    public boolean onMoveComplete()
    {
        final boolean b = shapeLocationControl.onMoveComplete();

        clear();

        return b;
    }

    @Override
    public void onMouseClick(final MouseEvent event)
    {
    }

    @Override
    public void onMouseDown(final MouseEvent event)
    {
        m_parent = getShape().getParent();
    }

    @Override
    public void onMouseUp(final MouseEvent event)
    {
        if (m_parent != getShape().getParent())
        {
            onMoveComplete();
        }
    }

    @Override
    public void execute()
    {
        shapeLocationControl.execute();
    }

    @Override
    public void clear()
    {
        shapeLocationControl.clear();

        m_parent = null;

        m_parentPart = null;

        m_picker = null;

        initialParent = null;
    }

    @Override
    public void reset()
    {
        shapeLocationControl.reset();

        clear();
    }

    @Override
    public Point2D getShapeLocation()
    {
        return shapeLocationControl.getShapeLocation();
    }

    @Override
    public void setShapeLocation(final Point2D location)
    {
        shapeLocationControl.setShapeLocation(location);
    }

    public ColorMapBackedPicker getPicker()
    {
        return m_picker;
    }

    @Override
    public WiresShape getShape()
    {
        return shapeLocationControl.getShape();
    }

    @Override
    public WiresContainer getParent()
    {
        return null != m_parent ? m_parent : getShape().getWiresManager().getLayer();
    }

    @Override
    public PickerPart.ShapePart getParentShapePart()
    {
        return null != m_parentPart ? m_parentPart.getShapePart() : null;
    }

    public WiresShapeLocationControlImpl getShapeLocationControl()
    {
        return shapeLocationControl;
    }

    public ColorMapBackedPicker.PickerOptions getPickerOptions()
    {
        return colorMapBackedPickerProvider.getOptions();
    }

    public WiresLayer getWiresLayer()
    {
        return getShape().getWiresManager().getLayer();
    }

    public WiresContainer getInitialParent()
    {
        return initialParent;
    }

    public interface ColorMapBackedPickerProvider
    {
        public ColorMapBackedPicker get(WiresLayer layer);

        public ColorMapBackedPicker.PickerOptions getOptions();
    }

    public static class ColorMapBackedPickerProviderImpl implements ColorMapBackedPickerProvider
    {
        private final ColorMapBackedPicker.PickerOptions pickerOptions;

        public ColorMapBackedPickerProviderImpl(final ColorMapBackedPicker.PickerOptions pickerOptions)
        {
            this.pickerOptions = pickerOptions;
        }

        @Override
        public ColorMapBackedPicker get(final WiresLayer layer)
        {
            return new ColorMapBackedPicker(layer, layer.getChildShapes(), layer.getLayer().getScratchPad(), pickerOptions);
        }

        @Override
        public ColorMapBackedPicker.PickerOptions getOptions()
        {
            return pickerOptions;
        }
    }
}
