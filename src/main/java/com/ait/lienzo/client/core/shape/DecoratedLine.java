package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.types.NFastArrayList;
import com.ait.lienzo.client.core.types.PathPartList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;

public class DecoratedLine extends Group
{
    private AbstractOffsetMultiPointShape line;

    public DecoratedLine(AbstractOffsetMultiPointShape line, SimpleArrow tailArrow, SimpleArrow headArrow) {
        add(line);
        add(tailArrow);
        add(headArrow);
    }

    protected void drawWithoutTransforms(final Context2D context, double alpha)
    {
        if ((context.isSelection()) && (false == isListening()))
        {
            return;
        }
        alpha = alpha * getAttributes().getAlpha();

        if (alpha <= 0)
        {
            return;
        }

        NFastArrayList shapes = getChildNodes();

        AbstractOffsetMultiPointShape line = (AbstractOffsetMultiPointShape) shapes.get(0);
        line.drawWithoutTransforms(context, alpha);
        Point2DArray points = line.getPoint2DArray();
        setX( 0 );
        setY( 0 );

        SimpleArrow tailArrow = (SimpleArrow) shapes.get(1);
        tailArrow.setPoints(new Point2DArray(points.get(0), line.getTailOffsetPoint()));
        tailArrow.drawWithoutTransforms(context, alpha);

        SimpleArrow headArrow = (SimpleArrow) shapes.get(2);
        headArrow.setPoints(new Point2DArray(points.get(points.size() -1), line.getHeadOffsetPoint()));
        headArrow.drawWithoutTransforms(context, alpha);
    }
}
