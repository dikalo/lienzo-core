package com.ait.lienzo.client.core.util;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.shape.IContainer;
import com.ait.lienzo.client.core.shape.MultiPointLine;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.types.NFastArrayList;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.google.gwt.event.shared.HandlerRegistration;

public class ControlPoints {
    private static final ControlPoints instance = new ControlPoints();

    public static ControlPoints instance() {
        return instance;
    }

    public ControlPointsContext createControlPointsContext(Shape targetShape, Shape protoControlPointShape) {
        Point2DArray points = targetShape.getAttributes().getControlPoints();
        if ( points == null ) {
            throw new IllegalArgumentException("Shape does not have any Control Points" );
        }


        ControlPointsContext cpCtx = new ControlPointsContext(targetShape, protoControlPointShape);
        return cpCtx;
    }

    public static class ControlPointsContext {
        private NFastArrayList<HandlerRegistration> m_onNodeDragStartHandlers;
        private HandlerRegistration                 m_onNodeDragMoveHandler;
        private HandlerRegistration                 m_onNodeDragEndHandler;
        private Shape                               m_targetShape;
        private Shape                               m_protoControlPointShape;
        private ControlPointDragStartHandler        m_controlPointDragStartHandler;
        private NFastArrayList<Shape>               m_controlPoints;

        public ControlPointsContext(Shape targetShape, Shape protoControlPointShape) {
            this.m_targetShape = targetShape;
            this.m_protoControlPointShape = protoControlPointShape;
            m_controlPointDragStartHandler = new ControlPointDragStartHandler(this);
            m_onNodeDragStartHandlers = new NFastArrayList<HandlerRegistration>();
            m_controlPoints = new NFastArrayList<Shape>();
        }

        public void showControlPoints() {
            Point2DArray points = m_targetShape.getAttributes().getControlPoints();
            addControlPoint(points.get(0), m_protoControlPointShape);

            for (int i = 1, size = points.size(); i < size; i++) {
                Shape cp = m_protoControlPointShape.copy();
                addControlPoint(points.get(i), cp);
            }

            m_targetShape.getLayer().draw();
        }

        public void addControlPoint(Point2D point, Shape cp) {
            cp.setDraggable(true);
            cp.setLocation(point);

            HandlerRegistration handler = cp.addNodeDragStartHandler(m_controlPointDragStartHandler);
            m_onNodeDragStartHandlers.add(handler);
            ((IContainer)m_targetShape.getParent()).add(cp);
            m_controlPoints.add(cp);
        }

        public void hideControlPoints() {
            for (int i = 0, size = m_controlPoints.size(); i < size; i++) {
                m_controlPoints.get(i).removeFromParent();
                m_onNodeDragStartHandlers.get(i).removeHandler();
            }
            m_targetShape.getLayer().draw();
        }

        public Shape getTargetShape() {
            return m_targetShape;
        }

        public HandlerRegistration getOnNodeDragMoveHandler() {
            return m_onNodeDragMoveHandler;
        }

        public void setOnNodeDragMoveHandler(HandlerRegistration onNodeDragMoveHandler) {
            this.m_onNodeDragMoveHandler = onNodeDragMoveHandler;
        }

        public HandlerRegistration getOnNodeDragEndHandler() {
            return m_onNodeDragEndHandler;
        }

        public void setOnNodeDragEndHandler(HandlerRegistration onNodeDragEndHandler) {
            this.m_onNodeDragEndHandler = onNodeDragEndHandler;
        }
    }

    public static class ControlPointDragStartHandler implements NodeDragStartHandler  {
        ControlPointsContext m_cpCtx;

        public ControlPointDragStartHandler(ControlPointsContext cpCtx) {
            this.m_cpCtx = cpCtx;
        }

        @Override
        public void onNodeDragStart(NodeDragStartEvent event) {
            Shape cp = (Shape) event.getDragContext().getNode();

            Point2DArray points = m_cpCtx.m_targetShape.getAttributes().getControlPoints();
            Point2DArray clone = new Point2DArray();
            Point2D point = null;
            for (int i = 0, size = points.size(); i < size; i++) {
                Point2D p = new Point2D(points.get(i));
                clone.push(p);
                if (p.getX() == cp.getX() && p.getY() == cp.getY()) {
                    point = p;
                }
            }

            ControlPointDragMoveEndHandler controlPointDragMoveEndHandler = new ControlPointDragMoveEndHandler(m_cpCtx, cp, clone, point);
            HandlerRegistration onNodeDragMoveHandlerHandler = cp.addNodeDragMoveHandler(controlPointDragMoveEndHandler);
            HandlerRegistration onNodeDragEndHandlerHandler = cp.addNodeDragEndHandler(controlPointDragMoveEndHandler);
            m_cpCtx.setOnNodeDragMoveHandler(onNodeDragMoveHandlerHandler);
            m_cpCtx.setOnNodeDragEndHandler(onNodeDragEndHandlerHandler);

        }
    }

    public static class ControlPointDragMoveEndHandler implements NodeDragMoveHandler, NodeDragEndHandler {
        private Point2DArray m_points;
        private Point2D      m_point;
        private double       m_startX;
        private double       m_startY;
        ControlPointsContext m_cpCtx;


        public ControlPointDragMoveEndHandler(ControlPointsContext cpCtx, Shape cp, Point2DArray points, Point2D point) {
            this.m_cpCtx = cpCtx;
            this.m_startX = cp.getX();
            this.m_startY = cp.getY();
            this.m_points = points;
            this.m_point = point;
        }

        @Override
        public void onNodeDragMove(NodeDragMoveEvent event) {
            MultiPointLine line = (MultiPointLine) m_cpCtx.getTargetShape();

            m_point.setX(m_startX + event.getDragContext().getDx());
            m_point.setY(m_startY + event.getDragContext().getDy());

            line.setControlPoints(m_points);
            ((Shape) line).getLayer().draw();
        }

        @Override
        public void onNodeDragEnd(NodeDragEndEvent event) {
            m_cpCtx.getOnNodeDragMoveHandler().removeHandler();
            m_cpCtx.getOnNodeDragEndHandler().removeHandler();
        }
    }
}
