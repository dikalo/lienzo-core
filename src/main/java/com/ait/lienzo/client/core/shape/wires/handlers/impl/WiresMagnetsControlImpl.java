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

import static com.ait.lienzo.client.core.shape.wires.MagnetManager.EIGHT_CARDINALS;
import static com.ait.lienzo.client.core.shape.wires.MagnetManager.FOUR_CARDINALS;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.MagnetManager;
import com.ait.lienzo.client.core.shape.wires.WiresMagnet;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresMagnetsControl;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.Direction;

public class WiresMagnetsControlImpl implements WiresMagnetsControl
{
    private final WiresShape shape;

    public WiresMagnetsControlImpl(final WiresShape shape)
    {
        this.shape = shape;
    }

    @Override
    public void onMoveStart(final double x, final double y)
    {
    }

    @Override
    public boolean onMove(final double dx, final double dy)
    {
        shape.shapeMoved();

        return false;
    }

    @Override
    public boolean onMoveComplete()
    {
        shape.shapeMoved();

        return false;
    }

    @Override
    public Point2D getAdjust()
    {
        return new Point2D(0, 0);
    }

    @Override
    public void shapeMoved()
    {
        if (null != getMagnets())
        {
            final IPrimitive<?> prim = getMagnets().getGroup();

            final Point2D absLoc = prim.getComputedLocation();

            final double x = absLoc.getX();

            final double y = absLoc.getY();

            shapeMoved(x, y);
        }
    }

    private void shapeMoved(final double x, final double y)
    {
        if (null != getMagnets())
        {
            final IControlHandleList controlHandles = getMagnets().getMagnets();

            for (int i = 0; i < controlHandles.size(); i++)
            {
                final WiresMagnet m = (WiresMagnet) controlHandles.getHandle(i);

                m.shapeMoved(x, y);
            }
        }
    }

    @Override
    public void shapeChanged()
    {
        final IControlHandleList controlHandles = null != getMagnets() ? getMagnets().getMagnets() : null;

        if ((null == controlHandles) || controlHandles.isEmpty())
        {
            return;
        }
        final Direction[] cardinals = controlHandles.size() == 9 ? EIGHT_CARDINALS : FOUR_CARDINALS;

        final Point2DArray points = MagnetManager.getWiresIntersectionPoints(shape, cardinals);

        final int size = controlHandles.size() <= points.size() ? controlHandles.size() : points.size();

        for (int i = 0; i < size; i++)
        {
            final Point2D p = points.get(i);

            final WiresMagnet m = (WiresMagnet) controlHandles.getHandle(i);

            m.setRx(p.getX()).setRy(p.getY());
        }
        shapeMoved();
    }

    private MagnetManager.Magnets getMagnets()
    {
        return shape.getMagnets();
    }
}
