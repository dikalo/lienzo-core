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

import java.util.Iterator;

import com.ait.lienzo.client.core.event.AbstractNodeDragEvent;
import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleType;
import com.ait.lienzo.client.core.shape.wires.MagnetManager.Magnets;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragEndEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragEndHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragMoveEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragMoveHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragStartEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresDragStartHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresMoveEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresMoveHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepEvent;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;

/**
 * This class handles the Wires Shape controls to provide additional features.
 * As the shape's MultiPath, when resizing using the resize control points, does not updates any attribute, the way
 * the resize is captured when user drags a resize control point is by adding drag handlers to the resize controls. That
 * way it can be calculated the bounding box location and size for the multipath.
 *
 * Future thoughts: if the different parts of the multipath are stored as attributes, another approach based on attributes
 * changed batcher could be used.
 */
public class WiresShapeControlHandleList implements IControlHandleList
{
    private static final int                 POINTS_SIZE = 4;

    private final WiresShape                 m_wires_shape;

    private final ControlHandleList          m_ctrls;

    private final ControlHandleType          m_ctrls_type;

    private final HandlerRegistrationManager m_registrationManager;

    private Group                            m_parent;

    public WiresShapeControlHandleList(final WiresShape wiresShape, final ControlHandleType controlsType, final ControlHandleList controls)
    {
        this(wiresShape, controlsType, controls, new HandlerRegistrationManager());
    }

    public WiresShapeControlHandleList(final WiresShape wiresShape, final ControlHandleType controlsType, final ControlHandleList controls, final HandlerRegistrationManager registrationManager)
    {
        m_wires_shape = wiresShape;

        m_ctrls = controls;

        m_ctrls_type = controlsType;

        m_registrationManager = registrationManager;

        m_parent = null;

        updateParentLocation();

        initControlsListeners();
    }

    @Override
    public void show()
    {
        switchVisibility(true);
    }

    void refresh()
    {
        final BoundingBox bbox = getPath().getBoundingBox();

        resize(bbox.getWidth(), bbox.getHeight(), true);
    }

    @Override
    public void destroy()
    {
        m_ctrls.destroy();

        m_registrationManager.removeHandler();

        if (null != m_parent)
        {
            m_parent.removeFromParent();
        }
    }

    @Override
    public int size()
    {
        return m_ctrls.size();
    }

    @Override
    public boolean isEmpty()
    {
        return m_ctrls.isEmpty();
    }

    @Override
    public IControlHandle getHandle(final int index)
    {
        return m_ctrls.getHandle(index);
    }

    @Override
    public void add(final IControlHandle handle)
    {
        m_ctrls.add(handle);
    }

    @Override
    public void remove(final IControlHandle handle)
    {
        m_ctrls.remove(handle);
    }

    @Override
    public boolean contains(final IControlHandle handle)
    {
        return m_ctrls.contains(handle);
    }

    @Override
    public void hide()
    {
        switchVisibility(false);
    }

    @Override
    public boolean isVisible()
    {
        return m_ctrls.isVisible();
    }

    @Override
    public HandlerRegistrationManager getHandlerRegistrationManager()
    {
        return m_ctrls.getHandlerRegistrationManager();
    }

    @Override
    public boolean isActive()
    {
        return m_ctrls.isActive();
    }

    @Override
    public boolean setActive(final boolean b)
    {
        return m_ctrls.setActive(b);
    }

    @Override
    public Iterator<IControlHandle> iterator()
    {
        return m_ctrls.iterator();
    }

    private void initControlsListeners()
    {
        // Control points - to provide the resize support.

        if (IControlHandle.ControlHandleStandardType.RESIZE.equals(m_ctrls_type))
        {
            for (int i = 0; i < POINTS_SIZE; i++)
            {
                final IPrimitive<?> control = m_ctrls.getHandle(i).getControl();

                m_registrationManager.register(control.addNodeDragStartHandler(new NodeDragStartHandler()
                {
                    @Override
                    public void onNodeDragStart(final NodeDragStartEvent event)
                    {
                        WiresShapeControlHandleList.this.resizeStart(event);
                    }
                }));
                m_registrationManager.register(control.addNodeDragMoveHandler(new NodeDragMoveHandler()
                {
                    @Override
                    public void onNodeDragMove(final NodeDragMoveEvent event)
                    {
                        WiresShapeControlHandleList.this.resizeMove(event);
                    }
                }));
                m_registrationManager.register(control.addNodeDragEndHandler(new NodeDragEndHandler()
                {
                    @Override
                    public void onNodeDragEnd(final NodeDragEndEvent event)
                    {
                        WiresShapeControlHandleList.this.resizeEnd(event);
                    }
                }));
            }
        }
        m_registrationManager.register(m_wires_shape.addWiresDragStartHandler(new WiresDragStartHandler()
        {
            @Override
            public void onShapeDragStart(final WiresDragStartEvent event)
            {
                updateParentLocation();
            }
        }));
        m_registrationManager.register(m_wires_shape.addWiresDragMoveHandler(new WiresDragMoveHandler()
        {
            @Override
            public void onShapeDragMove(final WiresDragMoveEvent event)
            {
                updateParentLocation();
            }
        }));
        m_registrationManager.register(m_wires_shape.addWiresDragEndHandler(new WiresDragEndHandler()
        {
            @Override
            public void onShapeDragEnd(final WiresDragEndEvent event)
            {
                updateParentLocation();
            }
        }));
        m_registrationManager.register(m_wires_shape.addWiresMoveHandler(new WiresMoveHandler()
        {
            @Override
            public void onShapeMoved(final WiresMoveEvent event)
            {
                updateParentLocation();
            }
        }));
    }

    protected void resizeStart(final AbstractNodeDragEvent<?> dragEvent)
    {
        if (m_wires_shape.isResizable())
        {
            final double[] r = resizeWhileDrag(dragEvent);

            m_wires_shape.getHandlerManager().fireEvent(new WiresResizeStartEvent(m_wires_shape, dragEvent, (int) r[0], (int) r[1], r[2], r[3]));
        }
    }

    protected void resizeMove(final AbstractNodeDragEvent<?> dragEvent)
    {
        if (m_wires_shape.isResizable())
        {
            final double[] r = resizeWhileDrag(dragEvent);

            m_wires_shape.getHandlerManager().fireEvent(new WiresResizeStepEvent(m_wires_shape, dragEvent, (int) r[0], (int) r[1], r[2], r[3]));
        }
    }

    protected void resizeEnd(final AbstractNodeDragEvent<?> dragEvent)
    {
        if (m_wires_shape.isResizable())
        {
            final double[] r = resizeWhileDrag(dragEvent);

            m_wires_shape.getHandlerManager().fireEvent(new WiresResizeEndEvent(m_wires_shape, dragEvent, (int) r[0], (int) r[1], r[2], r[3]));
        }
    }

    private double[] resizeWhileDrag(final AbstractNodeDragEvent<?> dragEvent)
    {
        if (m_wires_shape.isResizable())
        {
            // Ensure magnets hidden while resizing.

            final Magnets mags = m_wires_shape.getMagnets();

            if (null != mags)
            {
                mags.hide();
            }
            final double[] attrs = getBBAttributes(getControlPointsArray());

            resize(attrs[0], attrs[1], attrs[2], attrs[3], false);

            return attrs;
        }
        return null;
    }

    protected void resize(final Double x, final Double y, final double width, final double height, final boolean refresh)
    {
        m_wires_shape.getLayoutContainer().setOffset(new Point2D(x, y));
        resize(width, height, refresh);
    }

    protected void resize(final double width, final double height, final boolean refresh)
    {
        m_wires_shape.getLayoutContainer().setSize(width, height);

        if (refresh)
        {
            m_wires_shape.getLayoutContainer().refresh();
        }
        m_wires_shape.getLayoutContainer().execute();

        if (null != m_wires_shape.getControl())
        {
            m_wires_shape.getControl().getMagnetsControl().shapeChanged();
        }
        // Layout content whilst resizing
        m_wires_shape.getLayoutHandler().requestLayout(m_wires_shape);
    }


    private Point2DArray getControlPointsArray()
    {
        final Point2DArray result = new Point2DArray();

        for (int i = 0; i < POINTS_SIZE; i++)
        {
            final IPrimitive<?> control = m_ctrls.getHandle(i).getControl();

            result.push(new Point2D(control.getX(), control.getY()));
        }
        return result;
    }

    protected void updateParentLocation()
    {
        if ((null == m_parent) && (null != getGroup().getLayer()))
        {
            m_parent = new Group();

            getGroup().getLayer().add(m_parent);
        }
        if (null == m_parent)
        {
            return;
        }
        final Point2D p = getGroup().getComputedLocation();

        m_parent.setX(p.getX());

        m_parent.setY(p.getY());

        m_parent.moveToTop();

        for (final WiresShape child : m_wires_shape.getChildShapes())
        {
            final WiresShapeControlHandleList list = child.getControls();

            if (null != list)
            {
                list.updateParentLocation();
            }
        }
    }

    private double[] getBBAttributes(final Point2DArray controlPoints)
    {
        final Point2D zero = controlPoints.get(0);

        double minx = zero.getX();

        double miny = zero.getY();

        double maxx = minx;

        double maxy = miny;

        for (final Point2D control : controlPoints)
        {
            double valu = control.getX();

            if (valu < minx)
            {
                minx = valu;
            }
            if (valu > maxx)
            {
                maxx = valu;
            }
            valu = control.getY();

            if (valu < miny)
            {
                miny = valu;
            }
            if (valu > maxy)
            {
                maxy = valu;
            }
        }
        return new double[] { minx, miny, maxx - minx, maxy - miny };
    }

    private void switchVisibility(final boolean visible)
    {
        if (null == m_parent)
        {
            return;
        }
        if (visible)
        {
            m_ctrls.showOn(m_parent);
        }
        else
        {
            for (final WiresShape shape : m_wires_shape.getChildShapes())
            {
                final WiresShapeControlHandleList list = shape.getControls();

                if (list != null)
                {
                    list.hide();
                }
            }
            m_ctrls.hide();
        }
    }

    private MultiPath getPath()
    {
        return m_wires_shape.getPath();
    }

    private Group getGroup()
    {
        return m_wires_shape.getGroup();
    }
}
