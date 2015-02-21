package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ShapeType;

public class SimpleArrow extends Shape<SimpleArrow>
{
    private final PathPartList m_list = new PathPartList();


    public SimpleArrow()
    {
        super(ShapeType.ARROW);
    }

    public SimpleArrow(Point2D start, Point2D end)
    {
        super(ShapeType.ARROW);
        setPoints(new Point2DArray(start, end));
    }

    @Override public BoundingBox getBoundingBox()
    {
        return null;
    }

    @Override protected boolean prepare(Context2D context, Attributes attr, double alpha)
    {
        if (m_list.size() < 1)
        {
            Point2DArray points = attr.getPoints();
            buildArrow0(points.get(0), points.get(1), m_list);
        }
        if (m_list.size() < 1)
        {
            return false;
        }
        context.path(m_list);

        return true;
    }

    private void buildArrow0(Point2D tip, Point2D base, PathPartList list)
    {
        Point2D dv = base.sub(tip);
        double length = dv.getLength();
        Point2D dx = dv.unit(); // unit vector in the direction of SE
        Point2D dy = dx.perpendicular();

        Point2D p0 = tip;
        double  halfWidth = (length*0.75)/2; // TODO The 0.75 could be an attribute (mdp)
        Point2D p1 = base.add(dy.mul(halfWidth));
        Point2D p2 = base.sub(dy.mul(halfWidth));

        m_list.M(p0);
        m_list.L(p1);
        m_list.L(p2);
        m_list.L(p0);
        m_list.close();
    }

    private void block0(Point2D tip, Point2D base, PathPartList list)
    {
        Point2D dv = base.sub(tip);
        double length = dv.getLength();

        Point2D dx = dv.unit(); // unit vector in the direction of SE
        Point2D dy = dx.perpendicular();

        Point2D p0 = tip;
        double  halfWidth = length/2;
        Point2D p1 = tip.add(dy.mul(halfWidth));
        Point2D p2 = tip.sub(dy.mul(halfWidth));
        Point2D p3 = base.add(dy.mul(halfWidth));
        Point2D p4 = base.sub(dy.mul(halfWidth));

        m_list.M(p1);
        m_list.L(p2);
        m_list.L(p4);
        m_list.L(p3);
        m_list.L(p1);
        m_list.close();
    }

    private void circle0(Point2D tip, Point2D base, PathPartList list)
    {
        Point2D dv = base.sub(tip);
        double length = dv.getLength();

        double  r = length/2;
        m_list.M(tip);
        m_list.A(r, r, 0, 1, 0, base.getX(), base.getY());
        m_list.A(r, r, 0, 1, 0, tip.getX(), tip.getY());
        m_list.close();
    }


    @Override public IFactory<SimpleArrow> getFactory()
    {
        return null;
    }


    public Point2DArray getPoints()
    {
        return getAttributes().getPoints();
    }

    public SimpleArrow setPoints(Point2DArray points)
    {
        getAttributes().setPoints(points);

        return this;
    }
}
