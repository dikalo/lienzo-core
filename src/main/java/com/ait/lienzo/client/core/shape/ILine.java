package com.ait.lienzo.client.core.shape;

import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.Direction;

public interface ILine<T>
{
    Point2DArray getControlPoints();

    T setControlPoints(Point2DArray points);

    void setTailDirection(Direction tailDirection);

    Direction getTailDirection();

    double getTailOffset();


    void setTailOffset(double tailOffset);

    Point2D getTailOffsetStart();


    void setHeadDirection(Direction headDirection);

    Direction getHeadDirection();

    double getHeadOffset();

    void setHeadOffset(double headOffset);

    Point2D getHeadOffsetEnd();
}
