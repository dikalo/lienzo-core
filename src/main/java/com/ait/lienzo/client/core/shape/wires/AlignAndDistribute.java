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

package com.ait.lienzo.client.core.shape.wires;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.ait.lienzo.client.core.shape.Attributes;
import com.ait.lienzo.client.core.shape.IDrawable;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.handlers.AlignAndDistributeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.AlignAndDistributeControlImpl;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.DashArray;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;

/**
 * This class indexes related classes for alignment and distribution.
 *
 * an index is maintained for each edge and center for alignment and distribution.
 *
 * All indexing is done by rounding the double value to int - using Math.round.
 *
 * It then uses this information to optional show guidelines or perform snapping. These can be turned on and off using the setter methods of this class
 *
 * It's possible to control the style of the guideline when drawn. By using the style setter methods of this class.
 *
 * The circa property controls the number of pixes to search from the current position. For instance a circle of 4, will search 4 pixels
 * above and 4 pixels below the current y position, as well as 4 pixels to the left and 4 pixels to the right. As soon as the first index has a match, the search stops and snapping is done to that offset.
 *
 * The implementation is fairly generic and uses shape.getBoundingPoints().getBoundingBox() to do it's work.
 * The reason for getBoundPoints, is that the x/y of a Circle is at the center, where as other shapes are top left - getBoundingPoints normalises this to top left.
 * There is only one bit that is shape specific, which is the attribute listener, so the engine can determine if a shape has been moved or resized. For example in the case of a rectangle
 * this is the x, y, w and h attributes - this would be different for other shapes. This information is provided by getBoundingBoxAttributes().
 *
 * Be aware that nested indexed shapes are removed on drag, so that if they extend beyond the parent shape, they do not impact it's bounding box used for indexing. One the new boundingbox is assigned.
 * the children are added back.
 */
public class AlignAndDistribute
{
    private final Map<Double, LinkedList<AlignAndDistributeControl>> m_leftIndex;

    private final Map<Double, LinkedList<AlignAndDistributeControl>> m_hCenterIndex;

    private final Map<Double, LinkedList<AlignAndDistributeControl>> m_rightIndex;

    private final Map<Double, LinkedList<AlignAndDistributeControl>> m_topIndex;

    private final Map<Double, LinkedList<AlignAndDistributeControl>> m_vCenterIndex;

    private final Map<Double, LinkedList<AlignAndDistributeControl>> m_bottomIndex;

    private final Map<Double, LinkedList<DistributionEntry>>         m_leftDistIndex;

    private final Map<Double, LinkedList<DistributionEntry>>         m_hCenterDistIndex;

    private final Map<Double, LinkedList<DistributionEntry>>         m_rightDistIndex;

    private final Map<Double, LinkedList<DistributionEntry>>         m_topDistIndex;

    private final Map<Double, LinkedList<DistributionEntry>>         m_vCenterDistIndex;

    private final Map<Double, LinkedList<DistributionEntry>>         m_bottomDistIndex;

    private final DefaultAlignAndDistributeMatchesCallback           m_alignmentCallback;

    private final Map<String, AlignAndDistributeControl>             m_shapes         = new HashMap<>();

    private int                                                      m_circa          = 4;

    protected boolean                                                m_snap           = true;

    protected boolean                                                m_drawGuideLines = true;

    public AlignAndDistribute(final Layer layer)
    {
        m_leftIndex = new HashMap<>();

        m_hCenterIndex = new HashMap<>();

        m_rightIndex = new HashMap<>();

        m_topIndex = new HashMap<>();

        m_vCenterIndex = new HashMap<>();

        m_bottomIndex = new HashMap<>();

        m_alignmentCallback = new DefaultAlignAndDistributeMatchesCallback(layer);

        m_leftDistIndex = new HashMap<>();

        m_hCenterDistIndex = new HashMap<>();

        m_rightDistIndex = new HashMap<>();

        m_topDistIndex = new HashMap<>();

        m_vCenterDistIndex = new HashMap<>();

        m_bottomDistIndex = new HashMap<>();
    }

    public static BoundingBox getBoundingBox(final IDrawable<?> prim)
    {
        return prim.getComputedBoundingPoints().getBoundingBox();
    }

    public static Attributes getAttributes(final IPrimitive<?> prim)
    {
        return prim.getAttributes();
    }

    public double getStrokeWidth()
    {
        return m_alignmentCallback.getStrokeWidth();
    }

    public void setStrokeWidth(final double strokeWidth)
    {
        m_alignmentCallback.setStrokeWidth(strokeWidth);
    }

    public String getStrokeColor()
    {
        return m_alignmentCallback.getStrokeColor();
    }

    public void setStrokeColor(final String strokeColor)
    {
        m_alignmentCallback.setStrokeColor(strokeColor);
    }

    public DashArray getDashArray()
    {
        return m_alignmentCallback.getDashArray();
    }

    public void setDashArray(final DashArray dashArray)
    {
        m_alignmentCallback.setDashArray(dashArray);
    }

    public int getSnapCirca()
    {
        return m_circa;
    }

    public void setSnapCirca(final int circa)
    {
        m_circa = circa;
    }

    public boolean isSnap()
    {
        return m_snap;
    }

    public void setSnap(final boolean snap)
    {
        m_snap = snap;
    }

    public boolean isDrawGuideLines()
    {
        return m_drawGuideLines;
    }

    public void setDrawGuideLines(final boolean drawGuideLines)
    {
        m_drawGuideLines = drawGuideLines;
    }

    public AlignAndDistributeControl getShapeControl(final IPrimitive<?> prim)
    {
        return m_shapes.get(prim.uuid());
    }

    public AlignAndDistributeControl addShape(final IDrawable<?> group)
    {
        final String uuid = group.uuid();

        AlignAndDistributeControl handler = m_shapes.get(uuid);

        // only add if the group has not already been added
        if (null == handler)
        {
            handler = new AlignAndDistributeControlImpl((IPrimitive<?>) group, this, m_alignmentCallback, group.getBoundingBoxAttributes());

            m_shapes.put(uuid, handler);
        }
        return handler;
    }

    public void removeShape(final IDrawable<?> shape)
    {
        final AlignAndDistributeControl handler = m_shapes.get(shape.uuid());

        if (null != handler)
        {
            indexOff(handler);

            m_shapes.remove(shape.uuid());

            handler.remove();
        }
    }

    public boolean isShapeIndexed(final String uuid)
    {
        return m_shapes.containsKey(uuid);
    }

    public AlignAndDistributeControl getControlForShape(final String uuid)
    {
        return m_shapes.get(uuid);
    }

    public void addAlignIndexEntry(final Map<Double, LinkedList<AlignAndDistributeControl>> index, final AlignAndDistributeControl handler, final double pos)
    {
        final double rounded = round(pos);

        LinkedList<AlignAndDistributeControl> bucket = index.get(rounded);

        if (bucket == null)
        {
            bucket = new LinkedList<>();

            index.put(rounded, bucket);
        }
        bucket.add(handler);
    }

    public void removeAlignIndexEntry(final Map<Double, LinkedList<AlignAndDistributeControl>> index, final AlignAndDistributeControl handler, final double pos)
    {
        final double rounded = round(pos);

        final LinkedList<AlignAndDistributeControl> bucket = index.get(rounded);

        bucket.remove(handler);

        if (bucket.isEmpty())
        {
            index.remove(rounded);
        }
    }

    public void addDistIndexEntry(final Map<Double, LinkedList<DistributionEntry>> index, final DistributionEntry dist)
    {
        LinkedList<DistributionEntry> bucket = index.get(dist.getPoint());

        if (bucket == null)
        {
            bucket = new LinkedList<>();

            index.put(dist.getPoint(), bucket);
        }
        bucket.add(dist);
    }

    public void removeDistIndexEntry(final Map<Double, LinkedList<DistributionEntry>> index, final DistributionEntry dist)
    {
        final LinkedList<DistributionEntry> bucket = index.get(dist.getPoint());

        bucket.remove(dist);

        if (bucket.isEmpty())
        {
            index.remove(dist.getPoint());
        }
    }

    public void removeDistIndex(final AlignAndDistributeControl handler)
    {
        removeHorizontalDistIndex(handler);

        removeVerticalDistIndex(handler);
    }

    public void removeHorizontalDistIndex(final AlignAndDistributeControl handler)
    {
        for (final DistributionEntry dist : handler.getHorizontalDistributionEntries())
        {
            final AlignAndDistributeControl h1 = dist.getShape1();

            final AlignAndDistributeControl h2 = dist.getShape2();

            // make sure we don't remove from handler, or it will remove from the collection currently being iterated.
            if (handler == h1)
            {
                h2.getHorizontalDistributionEntries().remove(dist);
            }
            else
            {
                h1.getHorizontalDistributionEntries().remove(dist);
            }
            switch (dist.getDistributionType())
            {
                case DistributionEntry.LEFT_DIST:
                    removeDistIndexEntry(m_leftDistIndex, dist);
                    break;
                case DistributionEntry.H_CENTER_DIST:
                    removeDistIndexEntry(m_hCenterDistIndex, dist);
                    break;
                case DistributionEntry.RIGHT_DIST:
                    removeDistIndexEntry(m_rightDistIndex, dist);
                    break;
            }
        }
        handler.getHorizontalDistributionEntries().clear();
    }

    public void removeVerticalDistIndex(final AlignAndDistributeControl handler)
    {
        for (final DistributionEntry dist : handler.getVerticalDistributionEntries())
        {
            final AlignAndDistributeControl h1 = dist.getShape1();

            final AlignAndDistributeControl h2 = dist.getShape2();

            // make sure we don't remove from handler, or it will remove from the collection currently being iterated.
            if (handler == h1)
            {
                h2.getVerticalDistributionEntries().remove(dist);
            }
            else
            {
                h1.getVerticalDistributionEntries().remove(dist);
            }
            switch (dist.getDistributionType())
            {
                case DistributionEntry.TOP_DIST:
                    removeDistIndexEntry(m_topDistIndex, dist);
                    break;
                case DistributionEntry.V_CENTER_DIST:
                    removeDistIndexEntry(m_vCenterDistIndex, dist);
                    break;
                case DistributionEntry.BOTTOM_DIST:
                    removeDistIndexEntry(m_bottomDistIndex, dist);
                    break;
            }
        }
        handler.getVerticalDistributionEntries().clear();
    }

    public void buildDistIndex(final AlignAndDistributeControl handler)
    {
        buildHorizontalDistIndex(handler);

        buildVerticalDistIndex(handler);
    }

    public void buildHorizontalDistIndex(final AlignAndDistributeControl handler)
    {
        final double left = round(handler.getLeft());

        final double right = round(handler.getRight());

        for (final AlignAndDistributeControl otherH : m_shapes.values())
        {
            if (skipShape(handler, otherH))
            {
                continue;
            }
            final double otherLeft = round(otherH.getLeft());

            final double otherRight = round(otherH.getRight());

            DistributionEntry leftDist = null;

            DistributionEntry hCenterDist = null;

            DistributionEntry rightDist = null;

            if (otherRight < left)
            {
                final double dx = left - otherRight;

                final double leftPoint = otherLeft - dx;

                final double rightPoint = right + dx;

                final double centerPoint = round(otherRight + ((left - otherRight) / 2));

                leftDist = new DistributionEntry(otherH, handler, leftPoint, DistributionEntry.LEFT_DIST);

                hCenterDist = new DistributionEntry(otherH, handler, centerPoint, DistributionEntry.H_CENTER_DIST);

                rightDist = new DistributionEntry(otherH, handler, rightPoint, DistributionEntry.RIGHT_DIST);
            }
            else if (otherLeft > right)
            {
                final double dx = otherLeft - right;

                final double leftPoint = left - dx;

                final double rightPoint = otherRight + dx;

                final double centerPoint = round(otherLeft + ((right - otherLeft) / 2));

                leftDist = new DistributionEntry(handler, otherH, leftPoint, DistributionEntry.LEFT_DIST);

                hCenterDist = new DistributionEntry(handler, otherH, centerPoint, DistributionEntry.H_CENTER_DIST);

                rightDist = new DistributionEntry(handler, otherH, rightPoint, DistributionEntry.RIGHT_DIST);
            }
            if (leftDist != null)
            {
                addDistIndexEntry(m_leftDistIndex, leftDist);

                addDistIndexEntry(m_hCenterDistIndex, hCenterDist);

                addDistIndexEntry(m_rightDistIndex, rightDist);
            }
        }
    }

    private boolean skipShape(final AlignAndDistributeControl handler, final AlignAndDistributeControl otherH)
    {
        if ((otherH == handler) || !otherH.isIndexed())
        {
            // don't index against yourself or shapes not indexed
            return true;
        }
        return false;
    }

    public void buildVerticalDistIndex(final AlignAndDistributeControl handler)
    {
        final double top = round(handler.getTop());

        final double bottom = round(handler.getBottom());

        for (final AlignAndDistributeControl otherH : m_shapes.values())
        {
            if (skipShape(handler, otherH))
            {
                continue;
            }
            final double otherTop = round(otherH.getTop());

            final double otherBottom = round(otherH.getBottom());

            DistributionEntry topDist = null;

            DistributionEntry vCenterDist = null;

            DistributionEntry bottomDist = null;

            if (otherBottom < top)
            {
                final double dx = top - otherBottom;

                final double topPoint = otherTop - dx;

                final double bottomPoint = bottom + dx;

                final double centerPoint = round(otherBottom + ((top - otherBottom) / 2));

                topDist = new DistributionEntry(otherH, handler, topPoint, DistributionEntry.TOP_DIST);

                vCenterDist = new DistributionEntry(otherH, handler, centerPoint, DistributionEntry.V_CENTER_DIST);

                bottomDist = new DistributionEntry(otherH, handler, bottomPoint, DistributionEntry.BOTTOM_DIST);
            }
            else if (otherTop > bottom)
            {
                final double dx = otherTop - bottom;

                final double topPoint = top - dx;

                final double bottomPoint = otherBottom + dx;

                final double centerPoint = round(bottom + ((otherTop - bottom) / 2));

                topDist = new DistributionEntry(handler, otherH, topPoint, DistributionEntry.TOP_DIST);

                vCenterDist = new DistributionEntry(handler, otherH, centerPoint, DistributionEntry.V_CENTER_DIST);

                bottomDist = new DistributionEntry(handler, otherH, bottomPoint, DistributionEntry.BOTTOM_DIST);
            }
            if (topDist != null)
            {
                addDistIndexEntry(m_topDistIndex, topDist);

                addDistIndexEntry(m_vCenterDistIndex, vCenterDist);

                addDistIndexEntry(m_bottomDistIndex, bottomDist);
            }
        }
    }

    public static class DistributionEntry
    {
        private static final int                LEFT_DIST     = 0;

        private static final int                H_CENTER_DIST = 1;

        private static final int                RIGHT_DIST    = 2;

        private static final int                TOP_DIST      = 3;

        private static final int                V_CENTER_DIST = 4;

        private static final int                BOTTOM_DIST   = 5;

        private final AlignAndDistributeControl m_shape1;

        private final AlignAndDistributeControl m_shape2;

        private final double                    m_point;

        private final int                       m_distType;

        public DistributionEntry(final AlignAndDistributeControl shape1, final AlignAndDistributeControl shape2, final double point, final int distType)
        {
            m_shape1 = shape1;
            m_shape2 = shape2;
            m_point = point;
            m_distType = distType;
            if (distType <= 2)
            {
                shape1.getHorizontalDistributionEntries().add(this);
                shape2.getHorizontalDistributionEntries().add(this);
            }
            else
            {
                shape1.getVerticalDistributionEntries().add(this);
                shape2.getVerticalDistributionEntries().add(this);
            }
        }

        public AlignAndDistributeControl getShape1()
        {
            return m_shape1;
        }

        public AlignAndDistributeControl getShape2()
        {
            return m_shape2;
        }

        public double getPoint()
        {
            return m_point;
        }

        public int getDistributionType()
        {
            return m_distType;
        }
    }

    public AlignAndDistributeMatches findNearestMatches(final AlignAndDistributeControl handler, final double left, final double hCenter, final double right, final double top, final double vCenter, final double bottom)
    {
        LinkedList<AlignAndDistributeControl> leftList = null;
        LinkedList<AlignAndDistributeControl> hCenterList = null;
        LinkedList<AlignAndDistributeControl> rightList = null;

        LinkedList<AlignAndDistributeControl> topList = null;
        LinkedList<AlignAndDistributeControl> vCenterList = null;
        LinkedList<AlignAndDistributeControl> bottomList = null;

        LinkedList<DistributionEntry> leftDistList = null;
        LinkedList<DistributionEntry> hCenterDistList = null;
        LinkedList<DistributionEntry> rightDistList = null;

        LinkedList<DistributionEntry> topDistList = null;
        LinkedList<DistributionEntry> vCenterDistList = null;
        LinkedList<DistributionEntry> bottomDistList = null;

        int hOffset = 0;
        while (hOffset <= m_circa)
        {
            leftList = findNearestAlignIndexEntry(m_leftIndex, left + hOffset);
            hCenterList = findNearestAlignIndexEntry(m_hCenterIndex, hCenter + hOffset);
            rightList = findNearestAlignIndexEntry(m_rightIndex, right + hOffset);

            leftDistList = findNearestDistIndexEntry(m_leftDistIndex, right + hOffset);
            hCenterDistList = findNearestDistIndexEntry(m_hCenterDistIndex, hCenter + hOffset);
            rightDistList = findNearestDistIndexEntry(m_rightDistIndex, left + hOffset);

            if (matchFound(leftList, hCenterList, rightList, leftDistList, hCenterDistList, rightDistList))
            {
                break;
            }

            leftList = findNearestAlignIndexEntry(m_leftIndex, left - hOffset);
            hCenterList = findNearestAlignIndexEntry(m_hCenterIndex, hCenter - hOffset);
            rightList = findNearestAlignIndexEntry(m_rightIndex, right - hOffset);

            leftDistList = findNearestDistIndexEntry(m_leftDistIndex, right - hOffset);
            hCenterDistList = findNearestDistIndexEntry(m_hCenterDistIndex, hCenter - hOffset);
            rightDistList = findNearestDistIndexEntry(m_rightDistIndex, left - hOffset);
            if (matchFound(leftList, hCenterList, rightList, leftDistList, hCenterDistList, rightDistList))
            {
                hOffset = -hOffset;
                break;
            }
            hOffset++;
        }

        int vOffset = 0;
        while (vOffset <= m_circa)
        {
            topList = findNearestAlignIndexEntry(m_topIndex, top + vOffset);
            vCenterList = findNearestAlignIndexEntry(m_vCenterIndex, vCenter + vOffset);
            bottomList = findNearestAlignIndexEntry(m_bottomIndex, bottom + vOffset);

            topDistList = findNearestDistIndexEntry(m_topDistIndex, bottom + vOffset);
            vCenterDistList = findNearestDistIndexEntry(m_vCenterDistIndex, vCenter + vOffset);
            bottomDistList = findNearestDistIndexEntry(m_bottomDistIndex, top + vOffset);

            if (matchFound(topList, vCenterList, bottomList, topDistList, vCenterDistList, bottomDistList))
            {
                break;
            }

            topList = findNearestAlignIndexEntry(m_topIndex, top - vOffset);
            vCenterList = findNearestAlignIndexEntry(m_vCenterIndex, vCenter - vOffset);
            bottomList = findNearestAlignIndexEntry(m_bottomIndex, bottom - vOffset);

            topDistList = findNearestDistIndexEntry(m_topDistIndex, bottom - vOffset);
            vCenterDistList = findNearestDistIndexEntry(m_vCenterDistIndex, vCenter - vOffset);
            bottomDistList = findNearestDistIndexEntry(m_bottomDistIndex, top - vOffset);

            if (matchFound(topList, vCenterList, bottomList, topDistList, vCenterDistList, bottomDistList))
            {
                vOffset = -vOffset;
                break;
            }
            vOffset++;
        }

        AlignAndDistributeMatches matches;
        if (matchFound(leftList, hCenterList, rightList, leftDistList, hCenterDistList, rightDistList) || matchFound(topList, vCenterList, bottomList, topDistList, vCenterDistList, bottomDistList))
        {
            matches = new AlignAndDistributeMatches(handler, left + hOffset, leftList, hCenter + hOffset, hCenterList, right + hOffset, rightList, top + vOffset, topList, vCenter + vOffset, vCenterList, bottom + vOffset, bottomList, leftDistList, hCenterDistList, rightDistList, topDistList, vCenterDistList, bottomDistList);
        }
        else
        {
            matches = emptyAlignedMatches;
        }
        return matches;
    }

    private boolean matchFound(final LinkedList<AlignAndDistributeControl> l1, final LinkedList<AlignAndDistributeControl> l2, final LinkedList<AlignAndDistributeControl> l3, final LinkedList<DistributionEntry> l4, final LinkedList<DistributionEntry> l5, final LinkedList<DistributionEntry> l6)
    {
        if ((l1 != null) || (l2 != null) || (l3 != null) || (l4 != null) || (l5 != null) || (l6 != null))
        {
            return true;
        }
        return false;
    }

    private static LinkedList<AlignAndDistributeControl> findNearestAlignIndexEntry(final Map<Double, LinkedList<AlignAndDistributeControl>> map, final double pos)
    {
        return map.get(round(pos));
    }

    private static LinkedList<DistributionEntry> findNearestDistIndexEntry(final Map<Double, LinkedList<DistributionEntry>> map, final double pos)
    {
        return map.get(round(pos));
    }

    private static final EmptyAlignAndDistributeMatches emptyAlignedMatches = new EmptyAlignAndDistributeMatches();

    private static class EmptyAlignAndDistributeMatches extends AlignAndDistributeMatches
    {
        public EmptyAlignAndDistributeMatches()
        {
            m_hasMatch = false;
        }
    }

    public void indexOff(final AlignAndDistributeControl handler)
    {
        indexOffWithoutChangingStatus(handler);
        handler.setIndexed(false);
    }

    public void indexOffWithoutChangingStatus(final AlignAndDistributeControl handler)
    {
        removeAlignIndex(handler, handler.getLeft(), handler.getHorizontalCenter(), handler.getRight(), handler.getTop(), handler.getVerticalCenter(), handler.getBottom());
        removeDistIndex(handler);
    }

    public void indexOn(final AlignAndDistributeControl handler)
    {
        indexOnWithoutChangingStatus(handler);
        handler.setIndexed(true);
    }

    public void indexOnWithoutChangingStatus(final AlignAndDistributeControl handler)
    {
        buildAlignIndex(handler, handler.getLeft(), handler.getHorizontalCenter(), handler.getRight(), handler.getTop(), handler.getVerticalCenter(), handler.getBottom());
        buildDistIndex(handler);
    }

    private void buildAlignIndex(final AlignAndDistributeControl handler, final double left, final double hCenter, final double right, final double top, final double vCenter, final double bottom)
    {
        addAlignIndexEntry(m_leftIndex, handler, left);
        addAlignIndexEntry(m_hCenterIndex, handler, hCenter);
        addAlignIndexEntry(m_rightIndex, handler, right);

        addAlignIndexEntry(m_topIndex, handler, top);
        addAlignIndexEntry(m_vCenterIndex, handler, vCenter);
        addAlignIndexEntry(m_bottomIndex, handler, bottom);
    }

    private void removeAlignIndex(final AlignAndDistributeControl handler, final double left, final double hCenter, final double right, final double top, final double vCenter, final double bottom)
    {
        removeAlignIndexEntry(m_leftIndex, handler, left);
        removeAlignIndexEntry(m_hCenterIndex, handler, hCenter);
        removeAlignIndexEntry(m_rightIndex, handler, right);

        removeAlignIndexEntry(m_topIndex, handler, top);
        removeAlignIndexEntry(m_vCenterIndex, handler, vCenter);
        removeAlignIndexEntry(m_bottomIndex, handler, bottom);
    }

    public void addLeftAlignIndexEntry(final AlignAndDistributeControl shape, final double left)
    {
        addAlignIndexEntry(m_leftIndex, shape, left);
    }

    public void addHCenterAlignIndexEntry(final AlignAndDistributeControl shape, final double hCenter)
    {
        addAlignIndexEntry(m_hCenterIndex, shape, hCenter);
    }

    public void addRightAlignIndexEntry(final AlignAndDistributeControl shape, final double right)
    {
        addAlignIndexEntry(m_rightIndex, shape, right);
    }

    public void addTopAlignIndexEntry(final AlignAndDistributeControl shape, final double top)
    {
        addAlignIndexEntry(m_topIndex, shape, top);
    }

    public void addVCenterAlignIndexEntry(final AlignAndDistributeControl shape, final double vCenter)
    {
        addAlignIndexEntry(m_vCenterIndex, shape, vCenter);
    }

    public void addBottomAlignIndexEntry(final AlignAndDistributeControl shape, final double bottom)
    {
        addAlignIndexEntry(m_bottomIndex, shape, bottom);
    }

    public void removeLeftAlignIndexEntry(final AlignAndDistributeControl shape, final double left)
    {
        removeAlignIndexEntry(m_leftIndex, shape, left);
    }

    public void removeHCenterAlignIndexEntry(final AlignAndDistributeControl shape, final double hCenter)
    {
        removeAlignIndexEntry(m_hCenterIndex, shape, hCenter);
    }

    public void removeRightAlignIndexEntry(final AlignAndDistributeControl shape, final double right)
    {
        removeAlignIndexEntry(m_rightIndex, shape, right);
    }

    public void removeTopAlignIndexEntry(final AlignAndDistributeControl shape, final double top)
    {
        removeAlignIndexEntry(m_topIndex, shape, top);
    }

    public void removeVCenterAlignIndexEntry(final AlignAndDistributeControl shape, final double vCenter)
    {
        removeAlignIndexEntry(m_vCenterIndex, shape, vCenter);
    }

    public void removeBottomAlignIndexEntry(final AlignAndDistributeControl shape, final double bottom)
    {
        removeAlignIndexEntry(m_bottomIndex, shape, bottom);
    }

    public static class AlignAndDistributeMatches
    {
        private AlignAndDistributeControl             m_handler;

        private double                                m_leftPos;

        private LinkedList<AlignAndDistributeControl> m_leftList;

        private double                                m_hCenterPos;

        private LinkedList<AlignAndDistributeControl> m_hCenterList;

        private double                                m_rightPos;

        private LinkedList<AlignAndDistributeControl> m_rightList;

        private double                                m_topPos;

        private LinkedList<AlignAndDistributeControl> m_topList;

        private double                                m_vCenterPos;

        private LinkedList<AlignAndDistributeControl> m_vCenterList;

        private double                                m_bottomPos;

        private LinkedList<AlignAndDistributeControl> m_bottomList;

        private LinkedList<DistributionEntry>         m_leftDistList;

        private LinkedList<DistributionEntry>         m_hCenterDistList;

        private LinkedList<DistributionEntry>         m_rightDistList;

        private LinkedList<DistributionEntry>         m_topDistList;

        private LinkedList<DistributionEntry>         m_vCenterDistList;

        private LinkedList<DistributionEntry>         m_bottomDistList;

        protected boolean                             m_hasMatch;

        public AlignAndDistributeMatches()
        {
        }

        public AlignAndDistributeMatches(final AlignAndDistributeControl handler, final double leftPos, final LinkedList<AlignAndDistributeControl> leftList, final double hCenterPos, final LinkedList<AlignAndDistributeControl> hCenterList, final double rightPos, final LinkedList<AlignAndDistributeControl> rightList, final double topPos, final LinkedList<AlignAndDistributeControl> topList, final double vCenterPos, final LinkedList<AlignAndDistributeControl> vCenterList, final double bottomPos, final LinkedList<AlignAndDistributeControl> bottomList, final LinkedList<DistributionEntry> leftDistList, final LinkedList<DistributionEntry> hCenterDistList, final LinkedList<DistributionEntry> rightDistList, final LinkedList<DistributionEntry> topDistList, final LinkedList<DistributionEntry> vCenterDistList, final LinkedList<DistributionEntry> bottomDistList)
        {
            m_handler = handler;
            m_leftPos = leftPos;
            m_leftList = leftList;
            m_hCenterPos = hCenterPos;
            m_hCenterList = hCenterList;
            m_rightPos = rightPos;
            m_rightList = rightList;
            m_topPos = topPos;
            m_topList = topList;
            m_vCenterPos = vCenterPos;
            m_vCenterList = vCenterList;
            m_bottomPos = bottomPos;
            m_bottomList = bottomList;

            m_leftDistList = leftDistList;
            m_hCenterDistList = hCenterDistList;
            m_rightDistList = rightDistList;

            m_topDistList = topDistList;
            m_vCenterDistList = vCenterDistList;
            m_bottomDistList = bottomDistList;

            m_hasMatch = true;
        }

        public AlignAndDistributeControl getHandler()
        {
            return m_handler;
        }

        public boolean hashMatch()
        {
            return m_hasMatch;
        }

        public LinkedList<AlignAndDistributeControl> getLeftList()
        {
            return m_leftList;
        }

        public LinkedList<AlignAndDistributeControl> getHorizontalCenterList()
        {
            return m_hCenterList;
        }

        public LinkedList<AlignAndDistributeControl> getRightList()
        {
            return m_rightList;
        }

        public LinkedList<AlignAndDistributeControl> getTopList()
        {
            return m_topList;
        }

        public LinkedList<AlignAndDistributeControl> getVerticalCenterList()
        {
            return m_vCenterList;
        }

        public LinkedList<AlignAndDistributeControl> getBottomList()
        {
            return m_bottomList;
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

        public LinkedList<DistributionEntry> getLeftDistList()
        {
            return m_leftDistList;
        }

        public LinkedList<DistributionEntry> getHorizontalCenterDistList()
        {
            return m_hCenterDistList;
        }

        public LinkedList<DistributionEntry> getRightDistList()
        {
            return m_rightDistList;
        }

        public LinkedList<DistributionEntry> getTopDistList()
        {
            return m_topDistList;
        }

        public LinkedList<DistributionEntry> getVerticalCenterDistList()
        {
            return m_vCenterDistList;
        }

        public LinkedList<DistributionEntry> getBottomDistList()
        {
            return m_bottomDistList;
        }
    }

    private static double round(final double value)
    {
        return Math.round(value);
    }

    public interface AlignAndDistributeMatchesCallback
    {
        void call(AlignAndDistributeMatches matches);

        void dragEnd();
    }

    static class DefaultAlignAndDistributeMatchesCallback implements AlignAndDistributeMatchesCallback
    {
        private final Shape<?>[] m_lines       = new Shape<?>[18];

        private final Layer      m_layer;

        private Layer            m_overs;

        private double           m_strokeWidth = 0.5;

        private String           m_strokeColor = "#000000";

        private DashArray        m_dashArray   = new DashArray(10, 10);

        public DefaultAlignAndDistributeMatchesCallback(final Layer layer)
        {
            m_layer = layer;
        }

        public DefaultAlignAndDistributeMatchesCallback(final Layer layer, final double strokeWidth, final String strokeColor, final DashArray dashArray)
        {
            this(layer);
            m_strokeWidth = strokeWidth;
            m_strokeColor = strokeColor;
            m_dashArray = dashArray;
        }

        public double getStrokeWidth()
        {
            return m_strokeWidth;
        }

        public void setStrokeWidth(final double strokeWidth)
        {
            m_strokeWidth = strokeWidth;
        }

        public String getStrokeColor()
        {
            return m_strokeColor;
        }

        public void setStrokeColor(final String strokeColor)
        {
            m_strokeColor = strokeColor;
        }

        public DashArray getDashArray()
        {
            return m_dashArray;
        }

        public void setDashArray(final DashArray dashArray)
        {
            m_dashArray = dashArray;
        }

        private final Layer getOverLayer()
        {
            if (null == m_overs)
            {
                m_overs = m_layer.getViewport().getOverLayer();
            }
            return m_overs;
        }

        @Override
        public void dragEnd()
        {
            final Layer layer = getOverLayer();

            for (int i = 0; i < m_lines.length; i++)
            {
                if (m_lines[i] != null)
                {
                    layer.remove(m_lines[i]);

                    m_lines[i] = null;
                }
            }
            layer.draw();
        }

        @Override
        public void call(final AlignAndDistributeMatches matches)
        {
            final AlignAndDistributeControl handler = matches.getHandler();

            drawAlignIfMatches(handler, matches.getLeftList(), matches.getLeftPos(), 0, true);
            drawAlignIfMatches(handler, matches.getHorizontalCenterList(), matches.getHorizontalCenterPos(), 1, true);
            drawAlignIfMatches(handler, matches.getRightList(), matches.getRightPos(), 2, true);

            drawAlignIfMatches(handler, matches.getTopList(), matches.getTopPos(), 3, false);
            drawAlignIfMatches(handler, matches.getVerticalCenterList(), matches.getVerticalCenterPos(), 4, false);
            drawAlignIfMatches(handler, matches.getBottomList(), matches.getBottomPos(), 5, false);

            drawDistIfMatches(handler, matches.getLeftDistList(), 6, false);
            drawDistIfMatches(handler, matches.getHorizontalCenterDistList(), 8, false);
            drawDistIfMatches(handler, matches.getRightDistList(), 10, false);

            drawDistIfMatches(handler, matches.getTopDistList(), 12, true);
            drawDistIfMatches(handler, matches.getVerticalCenterDistList(), 14, true);
            drawDistIfMatches(handler, matches.getBottomDistList(), 16, true);
        }

        private void drawAlignIfMatches(final AlignAndDistributeControl handler, final LinkedList<AlignAndDistributeControl> shapes, final double pos, final int index, final boolean vertical)
        {
            final Layer layer = getOverLayer();

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
                layer.draw();
            }
            else if (m_lines[index] != null)
            {
                removeLine(index, m_lines[index]);

                layer.draw();
            }
        }

        private void drawDistIfMatches(final AlignAndDistributeControl h, final LinkedList<DistributionEntry> shapes, final int index, final boolean vertical)
        {
            final Layer layer = getOverLayer();

            if (shapes != null)
            {
                for (final DistributionEntry dist : shapes)
                {
                    final AlignAndDistributeControl h1 = dist.getShape1();

                    final AlignAndDistributeControl h2 = dist.getShape2();

                    if (!vertical)
                    {
                        double bottom = h.getBottom();

                        if (h1.getBottom() > bottom)
                        {
                            bottom = h1.getBottom();
                        }
                        if (h2.getBottom() > bottom)
                        {
                            bottom = h2.getBottom();
                        }
                        bottom = bottom + 20;

                        double x0 = 0, y0 = 0, x1 = 0, y1 = 0;
                        double x2 = 0, y2 = 0, x3 = 0, y3 = 0;

                        switch (dist.getDistributionType())
                        {
                            case DistributionEntry.LEFT_DIST:
                                x0 = h.getRight();
                                y0 = h.getBottom() + 5;
                                x1 = h1.getLeft();
                                y1 = h1.getBottom() + 5;

                                x2 = h1.getRight();
                                y2 = h1.getBottom() + 5;
                                x3 = h2.getLeft();
                                y3 = h2.getBottom() + 5;
                                break;
                            case DistributionEntry.H_CENTER_DIST:
                                x0 = h1.getRight();
                                y0 = h1.getBottom() + 5;
                                x1 = h.getLeft();
                                y1 = h.getBottom() + 5;

                                x2 = h.getRight();
                                y2 = h.getBottom() + 5;
                                x3 = h2.getLeft();
                                y3 = h2.getBottom() + 5;
                                break;
                            case DistributionEntry.RIGHT_DIST:
                                x0 = h1.getRight();
                                y0 = h1.getBottom() + 5;
                                x1 = h2.getLeft();
                                y1 = h2.getBottom() + 5;

                                x2 = h2.getRight();
                                y2 = h2.getBottom() + 5;
                                x3 = h.getLeft();
                                y3 = h.getBottom() + 5;
                                break;
                        }
                        drawPolyLine(index, bottom, x0, y0, x1, y1, false);
                        drawPolyLine(index + 1, bottom, x2, y2, x3, y3, false);
                    }
                    else
                    {
                        double right = h.getRight();

                        if (h1.getRight() > right)
                        {
                            right = h1.getRight();
                        }
                        if (h2.getRight() > right)
                        {
                            right = h2.getRight();
                        }
                        right = right + 20;

                        double x0 = 0, y0 = 0, x1 = 0, y1 = 0;
                        double x2 = 0, y2 = 0, x3 = 0, y3 = 0;

                        switch (dist.getDistributionType())
                        {
                            case DistributionEntry.TOP_DIST:
                                x0 = h.getRight() + 5;
                                y0 = h.getBottom();
                                x1 = h1.getRight() + 5;
                                y1 = h1.getTop();

                                x2 = h1.getRight() + 5;
                                y2 = h1.getBottom();
                                x3 = h2.getRight() + 5;
                                y3 = h2.getTop();
                                break;
                            case DistributionEntry.V_CENTER_DIST:
                                x0 = h1.getRight() + 5;
                                y0 = h1.getBottom();
                                x1 = h.getRight() + 5;
                                y1 = h.getTop();

                                x2 = h.getRight() + 5;
                                y2 = h.getBottom();
                                x3 = h2.getRight() + 5;
                                y3 = h2.getTop();
                                break;
                            case DistributionEntry.BOTTOM_DIST:
                                x0 = h1.getRight() + 5;
                                y0 = h1.getBottom();
                                x1 = h2.getRight();
                                y1 = h2.getTop();

                                x2 = h2.getRight() + 5;
                                y2 = h2.getBottom();
                                x3 = h.getRight() + 5;
                                y3 = h.getTop();
                                break;
                        }
                        drawPolyLine(index, right, x0, y0, x1, y1, true);
                        drawPolyLine(index + 1, right, x2, y2, x3, y3, true);
                    }
                }
                layer.draw();
            }
            else if (m_lines[index] != null)
            {
                removeLine(index, m_lines[index]);
                removeLine(index + 1, m_lines[index + 1]);
                layer.draw();
            }
        }

        private void removeLine(final int index, final Shape<?> line)
        {
            getOverLayer().remove(line);

            m_lines[index] = null;
        }

        private void drawPolyLine(final int index, final double edge, final double x0, final double y0, final double x1, final double y1, final boolean vertical)
        {
            Point2DArray points;

            if (vertical)
            {
                points = new Point2DArray(new Point2D(x0, y0), new Point2D(edge, y0), new Point2D(edge, y1), new Point2D(x1, y1));
            }
            else
            {
                points = new Point2DArray(new Point2D(x0, y0), new Point2D(x0, edge), new Point2D(x1, edge), new Point2D(x1, y1));
            }
            PolyLine pline = (PolyLine) m_lines[index];

            if (pline == null)
            {
                pline = new PolyLine(points);
                pline.setStrokeWidth(m_strokeWidth);
                pline.setStrokeColor(m_strokeColor);
                pline.setDashArray(m_dashArray);
                m_lines[index] = pline;
                getOverLayer().add(pline);
            }
            else
            {
                pline.setPoints(points);
            }
        }

        private void drawHorizontalLine(final AlignAndDistributeControl handler, final double pos, final LinkedList<AlignAndDistributeControl> shapes, final int index)
        {
            double left = handler.getLeft();
            double right = handler.getRight();

            for (final AlignAndDistributeControl otherHandler : shapes)
            {
                final double newLeft = otherHandler.getLeft();
                final double newRight = otherHandler.getRight();

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

        private void drawHorizontalLine(final double pos, final double left, final double right, final int index)
        {
            Line line = (Line) m_lines[index];
            if (line == null)
            {
                line = new Line(left, pos, right, pos);
                line.setStrokeWidth(m_strokeWidth);
                line.setStrokeColor(m_strokeColor);
                line.setDashArray(m_dashArray);
                getOverLayer().add(line);
                m_lines[index] = line;
            }
            else
            {
                line.setPoints(new Point2DArray(new Point2D(left, pos), new Point2D(right, pos)));
            }
        }

        private void drawVerticalLine(final AlignAndDistributeControl handler, final double pos, final LinkedList<AlignAndDistributeControl> shapes, final int index)
        {
            double top = handler.getTop();
            double bottom = handler.getBottom();

            for (final AlignAndDistributeControl otherHandler : shapes)
            {
                final double newTop = otherHandler.getTop();
                final double newBottom = otherHandler.getBottom();

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

        private void drawVerticalLine(final double pos, final double top, final double bottom, final int index)
        {
            Line line = (Line) m_lines[index];
            if (line == null)
            {
                line = new Line(pos, top, pos, bottom);
                line.setStrokeWidth(m_strokeWidth);
                line.setStrokeColor(m_strokeColor);
                line.setDashArray(m_dashArray);
                getOverLayer().add(line);
                m_lines[index] = line;
            }
            else
            {
                line.setPoints(new Point2DArray(new Point2D(pos, top), new Point2D(pos, bottom)));
            }
        }
    }
}
