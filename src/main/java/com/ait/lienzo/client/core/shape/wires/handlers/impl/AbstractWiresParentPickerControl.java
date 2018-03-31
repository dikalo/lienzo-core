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
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.types.Point2D;

public abstract class AbstractWiresParentPickerControl implements WiresParentPickerControl
{
    private final WiresParentPickerControlImpl parentPickerControl;

    private boolean                            enabled;

    protected AbstractWiresParentPickerControl(final WiresShape shape, final ColorMapBackedPicker.PickerOptions pickerOptions)
    {
        this(new WiresParentPickerControlImpl(shape, pickerOptions));
    }

    protected AbstractWiresParentPickerControl(final WiresParentPickerControlImpl parentPickerControl)
    {
        this.parentPickerControl = parentPickerControl;

        enable();
    }

    protected void enable()
    {
        this.enabled = true;
    }

    protected void disable()
    {
        this.enabled = false;
    }

    @Override
    public void onMoveStart(final double x, final double y)
    {
        if (!enabled)
        {
            return;
        }
        beforeMoveStart(x, y);

        parentPickerControl.onMoveStart(x, y);

        afterMoveStart(x, y);
    }

    @Override
    public boolean onMove(final double dx, final double dy)
    {
        if (!enabled)
        {
            return false;
        }
        parentPickerControl.onMove(dx, dy);

        return afterMove(dx, dy);
    }

    @Override
    public void onMoveAdjusted(final Point2D dxy)
    {
        parentPickerControl.onMoveAdjusted(dxy);
    }

    @Override
    public boolean onMoveComplete()
    {
        if (!enabled)
        {
            return true;
        }
        parentPickerControl.onMoveComplete();

        return afterMoveComplete();
    }

    @Override
    public Point2D getShapeLocation()
    {
        return parentPickerControl.getShapeLocation();
    }

    @Override
    public void setShapeLocation(final Point2D location)
    {
        parentPickerControl.getShapeLocationControl().setShapeLocation(location);
    }

    protected void beforeMoveStart(final double x, final double y)
    {
    }

    protected void afterMoveStart(final double x, final double y)
    {
    }

    protected boolean afterMove(final double dx, final double dy)
    {
        return false;
    }

    protected boolean afterMoveComplete()
    {
        return true;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public Point2D getCurrentLocation()
    {
        return parentPickerControl.getCurrentLocation();
    }

    @Override
    public WiresContainer getParent()
    {
        return parentPickerControl.getParent();
    }

    @Override
    public WiresShape getShape()
    {
        return parentPickerControl.getShape();
    }

    @Override
    public PickerPart.ShapePart getParentShapePart()
    {
        return parentPickerControl.getParentShapePart();
    }

    public WiresParentPickerControlImpl getParentPickerControl()
    {
        return parentPickerControl;
    }
}
