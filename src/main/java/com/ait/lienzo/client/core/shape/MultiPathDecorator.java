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

package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;

public class MultiPathDecorator
{
    private final MultiPath   m_path;

    private final BoundingBox m_bbox;

    private final double      m_wide;

    private final double      m_high;

    public MultiPathDecorator(final MultiPath path)
    {
        m_path = path;

        m_bbox = m_path.getBoundingBox();

        m_wide = m_bbox.getWidth();

        m_high = m_bbox.getHeight();
    }

    public MultiPath getPath()
    {
        return m_path;
    }

    public double getWidth()
    {
        return m_wide;
    }

    public double getHeight()
    {
        return m_high;
    }

    public BoundingBox getBoundingBox()
    {
        return m_bbox;
    }

    public void draw(final Point2DArray points)
    {
        final Point2D p1 = points.get(1);

        final double px = p1.getX();

        final double py = p1.getY();

        final double w2 = getWidth() / 2;

        final double dt = Geometry.getClockwiseAngleBetweenThreePoints(new Point2D(px, py + getHeight()), p1, points.get(0));

        m_path.setX(px - w2).setY(py).setOffset(w2, 0).setRotation(dt);
    }

    public MultiPathDecorator copy()
    {
        return new MultiPathDecorator(m_path.copy());
    }
}
