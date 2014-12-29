package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.config.LienzoCore;
import com.ait.lienzo.client.core.shape.json.IFactory;
import com.ait.lienzo.client.core.shape.json.validators.ValidationContext;
import com.ait.lienzo.client.core.shape.json.validators.ValidationException;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ShapeType;
import com.google.gwt.json.client.JSONObject;

public class StraightLine extends Shape<StraightLine> {
    private PathPartList paths;

    public StraightLine(Point2D start, Point2D... points) {
        super(ShapeType.STRAIGHT_LINE);

        Point2DArray array = new Point2DArray();
        array.push(start, points);
        init( array);
    }

    public StraightLine(Point2DArray points) {
        super(ShapeType.STRAIGHT_LINE);
        init( points);
    }

    public void init(Point2DArray points) {
        getAttributes().setControlPoints(points);
    }

    public StraightLine(JSONObject node, ValidationContext ctx)  throws ValidationException {
        super(ShapeType.STRAIGHT_LINE, node, ctx);
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }

    @Override
    protected boolean prepare(Context2D context, Attributes attr, double alpha) {
        if ( paths == null ) {
            buildPath(attr);
            context.path( paths );
            return true;
        } else {
            return false;
        }
    }

    private void buildPath(Attributes attr) {
        paths = new PathPartList();
        if (false == LienzoCore.get().isNativeLineDashSupported())
        {
            setDashArray( null ); // fall back to solid line
        }

        Point2DArray points = getAttributes().getControlPoints();

        Point2D start = points.get(0);
        Point2D next = points.get(1);

        System.out.print( start + " : " + next + " : " + points );
        System.out.println(" ");

        paths.M(start.getX(), start.getY());

        paths.L(next.getX(), next.getY());
        if (points.size() > 2) {
            for (int i = 2, length = points.size(); i < length; i++) {
                Point2D point = points.get(i);
                paths.L(point.getX(), point.getY());
            }
        }
    }


    @Override
    public IFactory<StraightLine> getFactory()
    {
        return new LineFactory();
    }

    public static class LineFactory extends ShapeFactory<StraightLine>
    {
        public LineFactory()
        {
            super(ShapeType.STRAIGHT_LINE);
            addAttribute(Attribute.CONTROL_POINTS, true);
        }

        @Override
        public StraightLine create(JSONObject node, ValidationContext ctx) throws ValidationException
        {
            return new StraightLine(node, ctx);
        }
    }


}
