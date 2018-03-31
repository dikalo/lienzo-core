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

import com.ait.lienzo.client.core.shape.wires.IDockingAcceptor;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresLayer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresDockingControl;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.Geometry;

public class WiresDockingControlImpl extends AbstractWiresParentPickerControl implements WiresDockingControl
{
    private Point2D initialPathLocation;

    private Point2D intersection;

    public WiresDockingControlImpl(final WiresShape shape, final ColorMapBackedPicker.PickerOptions pickerOptions)
    {
        super(shape, pickerOptions);
    }

    public WiresDockingControlImpl(final WiresParentPickerControlImpl parentPickerControl)
    {
        super(parentPickerControl);
    }

    @Override
    protected void beforeMoveStart(final double x, final double y)
    {
        super.beforeMoveStart(x, y);

        initialPathLocation = getShape().getPath().getComputedLocation();
    }

    @Override
    public WiresDockingControl setEnabled(final boolean enabled)
    {
        if (enabled)
        {
            enable();
        }
        else
        {
            disable();
        }
        return this;
    }

    @Override
    protected void afterMoveStart(final double x, final double y)
    {
        super.afterMoveStart(x, y);

        final WiresShape shape = getShape();

        if (null != shape.getDockedTo())
        {
            shape.setDockedTo(null);
        }
    }

    @Override
    protected boolean afterMove(final double dx, final double dy)
    {
        super.afterMove(dx, dy);

        intersection = null;

        if (isAllow())
        {
            final Point2D location = getParentPickerControl().getCurrentLocation();

            final Point2D absLoc = getParent().getComputedLocation();// convert to local xy of the path

            intersection = Geometry.findIntersection((int) (location.getX() - absLoc.getX()), (int) (location.getY() - absLoc.getY()), ((WiresShape) getParent()).getPath());
        }
        return null != intersection;
    }

    @Override
    protected boolean afterMoveComplete()
    {
        super.afterMoveComplete();

        return true;
    }

    @Override
    public void clear()
    {
        initialPathLocation = null;

        intersection = null;
    }

    @Override
    public Point2D getAdjust()
    {
        if (isEnabled() && (intersection != null))
        {
            final WiresShape shape = getShape();

            final BoundingBox box = shape.getPath().getBoundingBox();

            final Point2D absLoc = getParent().getComputedLocation();// convert to local xy of the path

            final double newX = (absLoc.getX() + intersection.getX()) - (box.getWidth() / 2);

            final double newY = (absLoc.getY() + intersection.getY()) - (box.getHeight() / 2);

            return new Point2D(newX - initialPathLocation.getX(), newY - initialPathLocation.getY());
        }
        return new Point2D(0, 0);
    }

    @Override
    public boolean isAllow()
    {
        final WiresLayer layer = getParentPickerControl().getWiresLayer();

        final WiresManager wiresManager = layer.getWiresManager();

        final IDockingAcceptor dockingAcceptor = wiresManager.getDockingAcceptor();

        return !isEnabled() || ((null != getParent()) && (null != getParentShapePart()) && (getParent() instanceof WiresShape) && (getParentShapePart() == PickerPart.ShapePart.BORDER) && (dockingAcceptor.dockingAllowed(getParent(), getShape())));
    }

    @Override
    public boolean accept()
    {
        return !isEnabled() || (isAllow() && _isAccept());
    }

    @Override
    public Point2D getCandidateLocation()
    {
        return calculateCandidateLocation(getShape(), getParent());
    }

    @Override
    public void execute()
    {
        if (isEnabled())
        {
            dock(getShape(), getParent(), getCandidateLocation());
        }
    }

    @Override
    public void reset()
    {
        if (isEnabled())
        {
            if (getParentPickerControl().getShapeLocationControl().isStartDocked() && (getParentPickerControl().getInitialParent() != getShape().getParent()))
            {
                dock(getShape(), getParentPickerControl().getInitialParent(), getParentPickerControl().getShapeLocationControl().getShapeInitialLocation());
            }
        }
    }

    private boolean _isAccept()
    {
        final WiresLayer layer = getParentPickerControl().getWiresLayer();

        final WiresManager wiresManager = layer.getWiresManager();

        final IDockingAcceptor dockingAcceptor = wiresManager.getDockingAcceptor();

        return (null != getParent()) && (null != getParentShapePart()) && (getParentShapePart() == PickerPart.ShapePart.BORDER) && dockingAcceptor.acceptDocking(getParent(), getShape());
    }

    public static Point2D calculateCandidateLocation(final WiresShape shape, final WiresContainer parent)
    {
        final Point2D location = shape.getControl().getParentPickerControl().getShapeLocation();

        final Point2D trgAbsOffset = parent.getContainer().getComputedLocation();

        return new Point2D(location.getX() - trgAbsOffset.getX(), location.getY() - trgAbsOffset.getY());
    }

    private void dock(final WiresShape shape, final WiresContainer parent, final Point2D location)
    {
        shape.removeFromParent();

        shape.setLocation(location);

        parent.add(shape);

        shape.setDockedTo(parent);
    }
}
