package com.ait.lienzo.client.core.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.event.AttributesChangedEvent;
import com.ait.lienzo.client.core.event.AttributesChangedHandler;
import com.ait.lienzo.client.core.event.HandlerRegistrationManager;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.NFastStringMap;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.widget.DragConstraintEnforcer;
import com.ait.lienzo.client.widget.DragContext;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * This class indexes the top, vertical center, bottom, left, horizontal center and right parts of shape.
 *
 * All indexing is done by rounding the double value - using Math.round.
 *
 * It then uses this information to optional show guidelines or snapping. These can be turned on and off using the setter methods of this class
 *
 * It's possible to control the style of the guideline when drawn. By using hte setter methods of this class.
 *
 * The circa property controls the number of pixes to search from the current position. For instance a circle of 4, will search 4 pixels
 * above and 4 pixels below the current y position.
 *
 * The implementation is fairly generic and uses shape.getBoundingBox to do it's work. There is only one bit that is shape specific,
 * which is the attribute listener, so the engine can determine if a shape is moved or resized. for instance in the case of a rectangle
 * this is the x, y, w and h attributes. But this would be different for other shapes. For this reason each shape that is to be indexed
 * must have handler class that extends EdgeAndCenterIndexHandler. Currently only Rectangle has this. To make this invisible tot he engine each shape
 * has a method "public EdgeAndCenterIndexHandler getEdgeAndCenterIndexHandler(EdgeAndCenterIndex edgeAndCenterIndex, AlignmentCallback alignmentCallback)"
 * which encapsulates the shape specific part handler.
 *
 * The initial design actually allow for any generic callback when alignment is found - so users could provide their all listeners, if they wanted. However
 * until a use case is found for this, it has not been exposed yet.
 */
public class EdgeAndCenterIndex
{
    private Map<Double, LinkedList<Shape>> m_leftIndex;

    private Map<Double, LinkedList<Shape>> m_hCenterIndex;

    private Map<Double, LinkedList<Shape>> m_rightIndex;

    private Map<Double, LinkedList<Shape>> m_topIndex;

    private Map<Double, LinkedList<Shape>> m_vCenterIndex;

    private Map<Double, LinkedList<Shape>> m_bottomIndex;

    private DefaultAlignmentCallback       m_alignmentCallback;

    private NFastStringMap<EdgeAndCenterIndexHandler> m_map   = new NFastStringMap<EdgeAndCenterIndexHandler>();

    private int                                       m_circa = 4;

    protected boolean m_snap           = true;

    protected boolean m_drawGuideLines = true;

    public EdgeAndCenterIndex(Layer layer)
    {
        m_leftIndex = new HashMap<Double, LinkedList<Shape>>();
        m_hCenterIndex = new HashMap<Double, LinkedList<Shape>>();
        m_rightIndex = new HashMap<Double, LinkedList<Shape>>();

        m_topIndex = new HashMap<Double, LinkedList<Shape>>();
        m_vCenterIndex = new HashMap<Double, LinkedList<Shape>>();
        m_bottomIndex = new HashMap<Double, LinkedList<Shape>>();

        m_alignmentCallback = new DefaultAlignmentCallback(layer);
    }

    public int getStrokeWidth()
    {
        return m_alignmentCallback.getStrokeWidth();
    }

    public void setStrokeWidth(int strokeWidth)
    {
        m_alignmentCallback.setStrokeWidth(strokeWidth);
    }

    public String getStrokeColor()
    {
        return m_alignmentCallback.getStrokeColor();
    }

    public void setStrokeColor(String strokeColor)
    {
        m_alignmentCallback.setStrokeColor(strokeColor);
    }

    public DashArray getDashArray()
    {
        return m_alignmentCallback.getDashArray();
    }

    public void setDashArray(DashArray dashArray)
    {
        m_alignmentCallback.setDashArray(dashArray);
    }

    public int getSnapCirca()
    {
        return m_circa;
    }

    public void setSnapCirca(int circa)
    {
        m_circa = circa;
    }

    public boolean isSnap()
    {
        return m_snap;
    }

    public void setSnap(boolean snap)
    {
        m_snap = snap;
    }

    public boolean isDrawGuideLines()
    {
        return m_drawGuideLines;
    }

    public void setDrawGuideLines(boolean drawGuideLines)
    {
        m_drawGuideLines = drawGuideLines;
    }

    public void addShapeToIndex(Shape shape)
    {

        EdgeAndCenterIndexHandler handler = shape.getEdgeAndCenterIndexHandler(this, m_alignmentCallback);

        m_map.put(shape.uuid(), handler);
    }

    public void removeShapeFromIndex(Shape shape)
    {

        EdgeAndCenterIndexHandler handler = m_map.get(shape.uuid());
        ;
        m_map.remove(shape.uuid());

        handler.removeHandlerRegistrations();
    }

    public void addIndexEntry(Map<Double, LinkedList<Shape>> index, Shape shape, double pos)
    {
        double rounded = Math.round(pos);
        LinkedList<Shape> bucket = index.get(rounded);
        if ( bucket == null ) {
            bucket = new LinkedList<Shape>();
            index.put(rounded, bucket);
        }
        bucket.add(shape);
    }

    public void removeIndexEntry(Map<Double, LinkedList<Shape>> index, Shape shape, double pos) {
        double rounded = Math.round(pos);
        LinkedList<Shape> bucket = index.get(rounded);
        bucket.remove(shape);
        if ( bucket.isEmpty() ) {
            index.remove(rounded);
        }
    }

    public AlignedMatches findNearestAlignedMatches(EdgeAndCenterIndexHandler handler, double left, double hCenter, double right, double top, double vCenter, double bottom) {
        LinkedList<Shape> leftList = null;
        LinkedList<Shape> hCenterList = null;
        LinkedList<Shape> rightList = null;

        LinkedList<Shape> topList = null;
        LinkedList<Shape> vCenterList = null;
        LinkedList<Shape> bottomList = null;

        int hOffset = 0;
        while ( hOffset <= m_circa )
        {
            leftList = findNearestIndexEntry(m_leftIndex, left + hOffset);
            hCenterList = findNearestIndexEntry(m_hCenterIndex, hCenter + hOffset);
            rightList = findNearestIndexEntry(m_rightIndex, right + hOffset);


            if (leftList == null && hCenterList == null && rightList == null)
            {
                leftList = findNearestIndexEntry(m_leftIndex, left - hOffset);
                hCenterList = findNearestIndexEntry(m_hCenterIndex, hCenter - hOffset);
                rightList = findNearestIndexEntry(m_rightIndex, right - hOffset);

                if (leftList != null || hCenterList != null || rightList != null )
                {
                    hOffset = -hOffset;
                    break;
                }
            } else  {
                break;
            }
            hOffset++;
        }

        int vOffset = 0;
        while ( vOffset <= m_circa )
        {
            topList = findNearestIndexEntry(m_topIndex, top + vOffset);
            vCenterList = findNearestIndexEntry(m_vCenterIndex, vCenter + vOffset);
            bottomList = findNearestIndexEntry(m_bottomIndex, bottom + vOffset);

            if (topList == null && vCenterList == null && bottomList == null)
            {
                topList = findNearestIndexEntry(m_topIndex, top - vOffset);
                vCenterList = findNearestIndexEntry(m_vCenterIndex, vCenter - vOffset);
                bottomList = findNearestIndexEntry(m_bottomIndex, bottom - vOffset);
                if (topList != null || vCenterList != null || bottomList != null )
                {
                    vOffset = -vOffset;
                    break;
                }
            } else {
                break;
            }
            vOffset++;
        }

        AlignedMatches matches;
        if (leftList != null || hCenterList != null || rightList != null || topList != null || vCenterList != null || bottomList != null )
        {
            matches = new AlignedMatches(handler,
                                         left + hOffset, leftList, hCenter + hOffset, hCenterList, right + hOffset, rightList,
                                         top + vOffset, topList, vCenter + vOffset, vCenterList, bottom + vOffset, bottomList);
        }
        else {
            matches = emptyAlignedMatches;
        }

        return matches;
    }

    private static LinkedList<Shape> findNearestIndexEntry(Map<Double, LinkedList<Shape>> map, double pos)
    {
        double rounded = Math.round( pos );
        LinkedList<Shape> indexEntries = map.get( rounded);
        return indexEntries;
    }

    private static final EmptyAlignedMatches emptyAlignedMatches = new EmptyAlignedMatches();
    public static class EmptyAlignedMatches extends AlignedMatches  {
        public EmptyAlignedMatches() {
            m_hasMatch = false;
        }
    }

    public void addIndex(Shape shape, double left, double hCenter, double right, double top, double vCenter, double bottom)
    {

        addIndexEntry(m_leftIndex, shape, left);
        addIndexEntry(m_hCenterIndex, shape, hCenter);
        addIndexEntry(m_rightIndex, shape, right);

        addIndexEntry(m_topIndex, shape, top);
        addIndexEntry(m_vCenterIndex, shape, vCenter);
        addIndexEntry(m_bottomIndex, shape, bottom);
    }

    public void removeIndex(Shape shape, double left, double hCenter, double right, double top, double vCenter, double bottom)
    {
        removeIndexEntry(m_leftIndex, shape, left);
        removeIndexEntry(m_hCenterIndex, shape, hCenter);
        removeIndexEntry(m_rightIndex, shape, right);

        removeIndexEntry(m_topIndex, shape, top);
        removeIndexEntry(m_vCenterIndex, shape, vCenter);
        removeIndexEntry(m_bottomIndex, shape, bottom);
    }

    public void addLeftIndexEntry(Shape shape, double left) {
        addIndexEntry(m_leftIndex, shape, left);
    }

    public void addHCenterIndexEntry(Shape shape, double hCenter) {
        addIndexEntry(m_hCenterIndex, shape, hCenter);
    }

    public void addRightIndexEntry(Shape shape, double right) {
        addIndexEntry(m_rightIndex, shape, right);
    }

    public void addTopIndexEntry(Shape shape, double top) {
        addIndexEntry(m_topIndex, shape, top);
    }

    public void addVCenterIndexEntry(Shape shape, double vCenter) {
        addIndexEntry(m_vCenterIndex, shape, vCenter);
    }

    public void addBottomIndexEntry(Shape shape, double bottom) {
        addIndexEntry(m_bottomIndex, shape, bottom);
    }

    public void removeLeftIndexEntry(Shape shape, double left) {
        addIndexEntry(m_leftIndex, shape, left);
    }

    public void removeHCenterIndexEntry(Shape shape, double hCenter) {
        removeIndexEntry(m_hCenterIndex, shape, hCenter);
    }

    public void removeRightIndexEntry(Shape shape, double right) {
        removeIndexEntry(m_rightIndex, shape, right);
    }

    public void removeTopIndexEntry(Shape shape, double top) {
        removeIndexEntry(m_topIndex, shape, top);
    }

    public void removeVCenterIndexEntry(Shape shape, double vCenter) {
        removeIndexEntry(m_vCenterIndex, shape, vCenter);
    }

    public void removeBottomIndexEntry(Shape shape, double bottom) {
        removeIndexEntry(m_bottomIndex, shape, bottom);
    }

    public static class AlignedMatches {
        private EdgeAndCenterIndexHandler m_handler;

        private double            m_leftPos;
        private LinkedList<Shape> m_left;

        private double            m_hCenterPos;
        private LinkedList<Shape> m_hCenter;

        private double            m_rightPos;
        private LinkedList<Shape> m_right;

        private double            m_topPos;
        private LinkedList<Shape> m_top;

        private double            m_vCenterPos;
        private LinkedList<Shape> m_vCenter;

        private double            m_bottomPos;
        private LinkedList<Shape> m_bottom;

        protected boolean         m_hasMatch;

        public AlignedMatches() {

        }

        public AlignedMatches(EdgeAndCenterIndexHandler handler,
                              double leftPos, LinkedList<Shape> left, double hCenterPos, LinkedList<Shape> hCenter, double rightPos, LinkedList<Shape> right,
                              double topPos, LinkedList<Shape> top, double vCenterPos, LinkedList<Shape> vCenter, double bottomPos, LinkedList<Shape> bottom)
        {
            m_handler = handler;
            m_leftPos = leftPos;
            m_left = left;
            m_hCenterPos = hCenterPos;
            m_hCenter = hCenter;
            m_rightPos = rightPos;
            m_right = right;
            m_topPos = topPos;
            m_top = top;
            m_vCenterPos = vCenterPos;
            m_vCenter = vCenter;
            m_bottomPos = bottomPos;
            m_bottom = bottom;
            m_hasMatch = true;
        }

        public EdgeAndCenterIndexHandler getHandler() {
            return m_handler;
        }

        public boolean hashMatch() {
            return m_hasMatch;
        }

        public LinkedList<Shape> getLeft()
        {
            return m_left;
        }

        public void setLeft(LinkedList<Shape> left)
        {
            m_left = left;
        }


        public LinkedList<Shape> getHorizontalCenter()
        {
            return m_hCenter;
        }

        public void setHorizontalCenter(LinkedList<Shape> hCenter)
        {
            m_hCenter = hCenter;
        }

        public LinkedList<Shape> getRight()
        {
            return m_right;
        }

        public void setRight(LinkedList<Shape> right)
        {
            m_right = right;
        }


        public LinkedList<Shape> getTop()
        {
            return m_top;
        }

        public void setTop(LinkedList<Shape> top)
        {
            m_top = top;
        }

        public LinkedList<Shape> getVerticalCenter()
        {
            return m_vCenter;
        }

        public void setVerticalCenter(LinkedList<Shape> vCenter)
        {
            m_vCenter = vCenter;
        }

        public LinkedList<Shape> getBottom()
        {
            return m_bottom;
        }

        public void setBottom(LinkedList<Shape> bottom)
        {
            m_bottom = bottom;
        }

        public double getLeftPos()
        {
            return m_leftPos;
        }

        public double getHorizontalCenterPos()
        {
            return m_hCenterPos;
        }

        public double getRightPos()
        {
            return m_rightPos;
        }

        public double getTopPos()
        {
            return m_topPos;
        }

        public double getVerticalCenterPos()
        {
            return m_vCenterPos;
        }

        public double getBottomPos()
        {
            return m_bottomPos;
        }
    }


    public static abstract class EdgeAndCenterIndexHandler implements AttributesChangedHandler, DragConstraintEnforcer, NodeDragEndHandler
    {
        protected EdgeAndCenterIndex         m_edgeAndCenterIndex;

        protected Shape                      m_shape;

        protected double                     m_xBoxOffset;

        protected double                     m_yBoxOffset;

        protected boolean                    m_isDragging;

        protected HandlerRegistrationManager m_attrHandlerRegs;

        protected HandlerRegistration        m_dragEndHandlerReg;

        protected AlignmentCallback          m_alignmentCallback;

        protected double                     m_startX;

        protected double                     m_startY;

        protected double                     m_x;

        protected double                     m_y;

        protected double                     m_left;

        protected double                     m_hCenter;

        protected double                     m_right;

        protected double                     m_top;

        protected double                     m_vCenter;

        protected double                     m_bottom;

        protected DragConstraintEnforcer     m_enforcerDelegate;

        public EdgeAndCenterIndexHandler(Shape shape, EdgeAndCenterIndex edgeAndCenterIndex, AlignmentCallback alignmentCallback, Attribute... attributes)
        {
            m_yBoxOffset = 0;

            m_shape = shape;
            m_xBoxOffset = 0;
            m_edgeAndCenterIndex = edgeAndCenterIndex;

            m_alignmentCallback = alignmentCallback;

            capturePositions(shape.getX(), shape.getY());
            m_edgeAndCenterIndex.addIndex(m_shape, m_left, m_hCenter, m_right, m_top, m_vCenter, m_bottom);

            if (m_shape.isDraggable())
            {
                dragOn();
            }

            int length = attributes.length;
            m_attrHandlerRegs = new HandlerRegistrationManager();
            for (int i = 0; i < length; i++)
            {
                m_attrHandlerRegs.add(m_shape.addAttributesChangedHandler(attributes[i], this));
            }
        }

        public double getLeft()
        {
            return m_left;
        }

        public double getHorizontalCenter()
        {
            return m_hCenter;
        }

        public double getRight()
        {
            return m_right;
        }

        public double getTop()
        {
            return m_top;
        }

        public double getVerticalCenter()
        {
            return m_vCenter;
        }

        public double getBottom()
        {
            return m_bottom;
        }

        public void captureVerticalPositions(BoundingBox box, double y)
        {
            double height = box.getHeight();
            m_top = y + m_yBoxOffset;
            m_vCenter = (m_top + (height / 2));
            m_bottom = (m_top + height);
        }

        public void captureHorizontalPositions(BoundingBox box, double x)
        {
            double width = box.getWidth();
            m_left = x + m_xBoxOffset;
            m_hCenter = m_left + (width / 2);
            m_right = m_left + width;
        }

        public void capturePositions(double x, double y)
        {
            BoundingBox box = null;
            if (x != m_x)
            {
                box = m_shape.getBoundingBox();
                captureHorizontalPositions(box, x);
            }

            if (y != m_y)
            {
                if (box == null)
                {
                    box = m_shape.getBoundingBox();
                }
                captureVerticalPositions(box, y);
            }

            m_x = x;
            m_y = y;
        }

        public void updateIndex(boolean leftChanged, boolean hCenterChanged, boolean rightChanged,
                                boolean topChanged, boolean vCenterChanged, boolean bottomChanged)
        {
            // This method attempts to avoid uneeded work, by only updating based on which edge or center changed
            BoundingBox box = m_shape.getBoundingBox();

            if (leftChanged || hCenterChanged || rightChanged)
            {
                if (leftChanged)
                {
                    m_edgeAndCenterIndex.removeLeftIndexEntry(m_shape, m_left);
                }

                if (hCenterChanged)
                {
                    m_edgeAndCenterIndex.removeHCenterIndexEntry(m_shape, m_hCenter);
                }

                if (rightChanged)
                {
                    m_edgeAndCenterIndex.removeRightIndexEntry(m_shape, m_right);
                }

                m_x = m_shape.getX();
                captureHorizontalPositions(box, m_x);
                if (leftChanged)
                {
                    m_edgeAndCenterIndex.addLeftIndexEntry(m_shape, m_left);
                }

                if (hCenterChanged)
                {
                    m_edgeAndCenterIndex.addHCenterIndexEntry(m_shape, m_hCenter);
                }

                if (rightChanged)
                {
                    m_edgeAndCenterIndex.addRightIndexEntry(m_shape, m_right);
                }
            }

            if (topChanged || vCenterChanged || bottomChanged)
            {
                if (topChanged)
                {
                    m_edgeAndCenterIndex.removeTopIndexEntry(m_shape, m_top);
                }

                if (vCenterChanged)
                {
                    m_edgeAndCenterIndex.removeVCenterIndexEntry(m_shape, m_vCenter);
                }

                if (bottomChanged)
                {
                    m_edgeAndCenterIndex.removeRightIndexEntry(m_shape, m_right);
                }

                m_y = m_shape.getY();
                captureHorizontalPositions(box, m_y);
                if (topChanged)
                {
                    m_edgeAndCenterIndex.addTopIndexEntry(m_shape, m_top);
                }

                if (vCenterChanged)
                {
                    m_edgeAndCenterIndex.addVCenterIndexEntry(m_shape, m_vCenter);
                }

                if (bottomChanged)
                {
                    m_edgeAndCenterIndex.addRightIndexEntry(m_shape, m_right);
                }
            }
        }

        public void dragOn()
        {
            m_enforcerDelegate = m_shape.getDragConstraints();
            m_shape.setDragConstraints(this);
            m_dragEndHandlerReg = m_shape.addNodeDragEndHandler(this);
        }

        public void draggOff()
        {
            m_shape.setDragConstraints(m_enforcerDelegate);
            removeDragHandlerRegistrations();
        }

        public void onAttributesChanged(AttributesChangedEvent event)
        {
            if (m_isDragging)
            {
                return;
            }

            if (event.has(Attribute.DRAGGABLE))
            {
                boolean isDraggable = m_shape.isDraggable();
                if (!m_isDragging && isDraggable)
                {
                    // was off, now on
                    dragOn();
                }
                else if (m_isDragging && !isDraggable)
                {
                    // was on, now on off
                    draggOff();
                }
                m_isDragging = m_shape.isDraggable();
            }

            doOnAttributesChanged(event);
        }

        public abstract void doOnAttributesChanged(AttributesChangedEvent event);

        @Override public void startDrag(DragContext dragContext)
        {
            // shapes being dragged must be removed from the index, so that they don't snap to themselves
            m_startX = dragContext.getNode().getX();
            m_startY = dragContext.getNode().getY();

            m_isDragging = true;
            m_edgeAndCenterIndex.removeIndex(m_shape, m_left, m_hCenter, m_right, m_top, m_vCenter, m_bottom);
        }

        @Override public void adjust(Point2D dxy)
        {
            double x = m_startX + dxy.getX();
            double y = m_startY + dxy.getY();
            capturePositions(x, y);
            AlignedMatches matches = m_edgeAndCenterIndex.findNearestAlignedMatches(this, m_left, m_hCenter, m_right,
                                                                                    m_top, m_vCenter, m_bottom);

            if ( m_edgeAndCenterIndex.isSnap() )
            {
                BoundingBox box = null;
                boolean recapture = false;

                // Adjust Vertical
                if (matches.getTop() != null)
                {
                    dxy.setY(matches.getTopPos() - m_startY);
                    recapture = true;
                }
                else if (matches.getVerticalCenter() != null)
                {
                    box = m_shape.getBoundingBox();
                    dxy.setY((matches.getVerticalCenterPos() - (box.getHeight() / 2)) - m_startY);
                    recapture = true;
                }
                else if (matches.getBottom() != null)
                {
                    box = m_shape.getBoundingBox();
                    dxy.setY((matches.getBottomPos() - box.getHeight()) - m_startY);
                    recapture = true;
                }

                // Adjust horizontal
                if (matches.getLeft() != null)
                {
                    dxy.setX(matches.getLeftPos() - m_startX);
                    recapture = true;
                }
                else if (matches.getHorizontalCenter() != null)
                {
                    if (box == null)
                    {
                        box = m_shape.getBoundingBox();
                    }
                    dxy.setX((matches.getHorizontalCenterPos() - (box.getWidth() / 2)) - m_startX);
                    recapture = true;
                }
                else if (matches.getRight() != null)
                {
                    if (box == null)
                    {
                        box = m_shape.getBoundingBox();
                    }
                    dxy.setX((matches.getRightPos() - box.getWidth()) - m_startX);
                    recapture = true;
                }

                if (m_enforcerDelegate != null)
                {
                    // Try to obey the default or user provided enforcer too.
                    double dx = dxy.getX();
                    double dy = dxy.getY();
                    m_enforcerDelegate.adjust(dxy);
                    if (!recapture && (dx != dxy.getX() || dy != dxy.getY()))
                    {
                        // if the delegate adjusted, we must recapture
                        recapture = true;
                    }
                }

                // it was adjusted, so recapture points
                if (recapture)
                {
                    x = m_startX + dxy.getX();
                    y = m_startY + dxy.getY();
                    capturePositions(x, y);
                }
            }



            if ( m_edgeAndCenterIndex.isDrawGuideLines() )
            {
                m_alignmentCallback.call(matches);
            }
        }

        public void onNodeDragEnd(NodeDragEndEvent event)
        {
            m_isDragging = false;
            capturePositions(m_shape.getX(), m_shape.getY());

            m_alignmentCallback.dragEnd();

            // shape was removed from the index, so add it back in
            m_edgeAndCenterIndex.addIndex(m_shape, m_left, m_hCenter, m_right, m_top, m_vCenter, m_bottom);
        }

        private void removeDragHandlerRegistrations()
        {
            m_dragEndHandlerReg.removeHandler();
            m_dragEndHandlerReg = null;
        }

        public void removeHandlerRegistrations()
        {
            m_attrHandlerRegs.delete();
            m_attrHandlerRegs = null;

            removeDragHandlerRegistrations();
        }
    }

    public static interface AlignmentCallback
    {
        void call(AlignedMatches matches);

        void dragEnd();
    }

    public static class DefaultAlignmentCallback implements AlignmentCallback
    {
        private final Line[] m_lines = new Line[6];

        private Layer m_layer;

        private int       m_strokeWidth = 1;

        private String    m_strokeColor = "#000000";

        private DashArray m_dashArray   = new DashArray(10, 10);

        public DefaultAlignmentCallback(Layer layer)
        {
            m_layer = layer;
        }

        public DefaultAlignmentCallback(Layer layer, int strokeWidth, String strokeColor, DashArray dashArray)
        {
            this(layer);
            m_strokeWidth = strokeWidth;
            m_strokeColor = strokeColor;
            m_dashArray = dashArray;
        }

        public int getStrokeWidth()
        {
            return m_strokeWidth;
        }

        public void setStrokeWidth(int strokeWidth)
        {
            m_strokeWidth = strokeWidth;
        }

        public String getStrokeColor()
        {
            return m_strokeColor;
        }

        public void setStrokeColor(String strokeColor)
        {
            m_strokeColor = strokeColor;
        }

        public DashArray getDashArray()
        {
            return m_dashArray;
        }

        public void setDashArray(DashArray dashArray)
        {
            m_dashArray = dashArray;
        }

        @Override public void dragEnd()
        {
            for ( int i = 0; i <m_lines.length; i++ ) {
                if (m_lines[i] != null)
                {
                    m_layer.remove(m_lines[i]);
                    m_lines[i] = null;
                }
            }

            m_layer.draw();
        }

        @Override public void call(EdgeAndCenterIndex.AlignedMatches matches)
        {
            EdgeAndCenterIndex.EdgeAndCenterIndexHandler handler = matches.getHandler();

            drawLinesIfMatches(handler, matches.getLeft(), matches.getLeftPos(), 0, true);
            drawLinesIfMatches(handler, matches.getHorizontalCenter(), matches.getHorizontalCenterPos(), 1, true);
            drawLinesIfMatches(handler, matches.getRight(), matches.getRightPos(), 2, true);

            drawLinesIfMatches(handler, matches.getTop(), matches.getTopPos(), 3, false);
            drawLinesIfMatches(handler, matches.getVerticalCenter(), matches.getVerticalCenterPos(), 4, false);
            drawLinesIfMatches(handler, matches.getBottom(), matches.getBottomPos(), 5, false);
        }

        private void drawLinesIfMatches(EdgeAndCenterIndex.EdgeAndCenterIndexHandler handler, LinkedList<Shape> shapes, double pos, int index, boolean vertical)
        {

            if (shapes != null)
            {

                if (vertical)
                {
                    drawVerticalLine(handler, pos, shapes, index);
                }
                else
                {
                    drawHorizontalLine(handler, pos, shapes, index);
                }
                m_layer.draw();  // @dean can we avoid calling draw here REVIEW
            }
            else if (m_lines[index] != null)
            {
                removeLine(index, m_lines[index]);
            }
        }

        private void removeLine(int index, Line line)
        {
            m_layer.remove(line);
            m_lines[index] = null;
            m_layer.draw();
        }

        private void drawHorizontalLine(EdgeAndCenterIndex.EdgeAndCenterIndexHandler handler, double pos, LinkedList<Shape> shapes, int index)
        {
            double left = handler.getLeft();
            double right = handler.getRight();

            for (Shape shape : shapes)
            {
                double newLeft = shape.getX();
                double newRight = newLeft + shape.getBoundingBox().getWidth();

                if (newLeft < left)
                {
                    left = newLeft;
                }

                if (newRight > right)
                {
                    right = newRight;
                }
            }

            drawHorizontalLine(pos, left, right, index);
        }

        private void drawVerticalLine(EdgeAndCenterIndex.EdgeAndCenterIndexHandler handler, double pos, LinkedList<Shape> shapes, int index)
        {
            double top = handler.getTop();
            double bottom = handler.getBottom();

            for (Shape shape : shapes)
            {
                double newTop = shape.getY();
                double newBottom = newTop + shape.getBoundingBox().getHeight();

                if (newTop < top)
                {
                    top = newTop;
                }

                if (newBottom > bottom)
                {
                    bottom = newBottom;
                }
            }

            drawVerticalLine(pos, top, bottom, index);
        }

        private void drawHorizontalLine(double pos, double left, double right, int index)
        {
            Line line = m_lines[index];
            if (line == null)
            {
                line = new Line(left, pos, right, pos);
                line.setStrokeWidth(m_strokeWidth);
                line.setStrokeColor(m_strokeColor);
                line.setDashArray(m_dashArray);
                m_layer.add(line);
                m_lines[index] = line;
            }
            else
            {
                line.setPoints(new Point2DArray(new Point2D(left, pos), new Point2D(right, pos)));
            }
        }

        private void drawVerticalLine(double pos, double top, double bottom, int index)
        {
            Line line = m_lines[index];
            if (line == null)
            {
                line = new Line(pos, top, pos, bottom);
                line.setStrokeWidth(m_strokeWidth);
                line.setStrokeColor(m_strokeColor);
                line.setDashArray(m_dashArray);
                m_layer.add(line);
                m_lines[index] = line;
            }
            else
            {
                line.setPoints(new Point2DArray(new Point2D(pos, top), new Point2D(pos, bottom)));
            }
        }
    }
}
