/*
   Copyright (c) 2014,2015 Ahome' Innovation Technologies. All rights reserved.

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

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

/**
 * PolyLine is a continuous line composed of one or more line segments.
 * To create a dashed PolyLine, use one of the setDashArray() methods. 
 */
public class PolyLine extends AbstractOffsetMultiPointShape<PolyLine>
{
    private       double       m_tailOffsetValue = 0;

    private       double       m_headOffsetValue = 0;

    private       Point2D      m_tailOffsetPoint = null;

    private       Point2D      m_headOffsetPoint = null;

    private final PathPartList m_list            = new PathPartList();

    /**
     * Constructor. Creates an instance of a polyline.
     *
     * @param points a {@link Point2DArray} containing 2 or more points.
     */
    public PolyLine(final Point2DArray points)
    {
        super(ShapeType.POLYLINE);

        setPoints(points);
    }

    public PolyLine(final Point2D point, final Point2D... points)
    {
        this(new Point2DArray(point, points));
    }

    protected PolyLine(final JSONObject node, final ValidationContext ctx) throws ValidationException
    {
        super(ShapeType.POLYLINE, node, ctx);

        refresh();
    }

    @Override
    public PolyLine refresh()
    {
        if (m_tailOffsetValue != m_headOffsetValue)
        {
            return this;
        }
        return this;
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return new BoundingBox(getPoints());
    }

    /**
     * Draws this polyline.
     *
     * @param context
     */
    @Override
    protected boolean prepare(final Context2D context, final Attributes attr, final double alpha)
    {
        if (m_list.size() < 1)
        {
            if (false == parse(attr))
            {
                return false;
            }
        }
        if (m_list.size() < 1)
        {
            return false;
        }
        context.path(m_list);

        return true;
    }

    protected boolean parse(final Attributes attr)
    {
        Point2DArray list = attr.getPoints();
        if (null == list)
        {
            return false;
        }

        list = list.noAdjacentPoints();
        final int size = list.size();

        if ( size == 0 )
        {
            return false;
        }

        int startIndex = correctTailWithOffset(list, size);
        int endIndex = correctHeadWithOffset(list, size);

        Point2D p0 = list.get(startIndex);
        m_list.M(p0.getX(), p0.getY());

        Point2D point = p0;
        for (int i = startIndex+1; i <= endIndex; i++)
        {
            point = list.get(i);

            m_list.L(point.getX(), point.getY());
        }

        return true;
    }

    private int correctTailWithOffset(Point2DArray list, int size)
    {
        int i = 1;
        double tailOffset = getTailOffset();
        if ( tailOffset > 0 )
        {
            Point2D p0 = list.get(0);
            for (; i < size; i++)
            {
                Point2D p1 = list.get(i);
                Point2D dx = p1.sub(p0);
                double length = dx.getLength();
                if (length > tailOffset)
                {
                    // offset is within this point, now find the intersect, this is the new p0.
                    p0 = p0.add(dx.unit().mul(tailOffset));
                    break;
                }
            }
            list.set(i-1, p0);
            m_tailOffsetPoint = p0;
        }
        return i-1;
    }

    private int correctHeadWithOffset(Point2DArray list, int size)
    {
        int i = size - 2;
        double headOffset = getHeadOffset();
        if ( headOffset > 0 )
        {
            Point2D pLast = list.get(size - 1);
            for (; i >= 0; i--)
            {
                Point2D p1 = list.get(i);
                Point2D dx = pLast.sub(p1);
                double length = dx.getLength();
                if (length > headOffset)
                {
                    // offset is within this point, now find the intersect, this is the new pLast.
                    pLast = pLast.sub(dx.unit().mul(headOffset));
                    break;
                }
            }
            list.set(i + 1, pLast);
            m_headOffsetPoint = pLast;
        }
        return i+1;
    }

    @Override
    protected void fill(Context2D context, Attributes attr, double alpha)
    {
    }

    /**
     * Returns this PolyLine's points.
     * @return {@link Point2DArray}
     */
    public Point2DArray getPoints()
    {
        return getAttributes().getPoints();
    }

    /**
     * Sets this PolyLine's points.
     * @param points {@link Point2DArray}
     * @return this PolyLine
     */
    public PolyLine setPoints(final Point2DArray points)
    {
        getAttributes().setPoints(points);

        return refresh();
    }

    @Override
    public PolyLine setPoint2DArray(Point2DArray points)
    {
        return setPoints(points);
    }

    @Override
    public Point2DArray getPoint2DArray()
    {
        return getPoints();
    }

    @Override
    public Point2D getTailOffsetPoint()
    {
        return m_tailOffsetPoint;
    }

    @Override
    public Point2D getHeadOffsetPoint()
    {
        return m_headOffsetPoint;
    }

    @Override
    public IFactory<PolyLine> getFactory()
    {
        return new PolyLineFactory();
    }

    public static class PolyLineFactory extends AbstractOffsetMultiPointShapeFactory<PolyLine>
    {
        public PolyLineFactory()
        {
            super(ShapeType.POLYLINE);

            addAttribute(Attribute.POINTS, true);
        }

        @Override
        public PolyLine create(final JSONObject node, final ValidationContext ctx) throws ValidationException
        {
            return new PolyLine(node, ctx);
        }
    }
}
