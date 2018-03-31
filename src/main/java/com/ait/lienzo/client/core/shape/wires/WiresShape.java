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

import java.util.Map;
import java.util.Objects;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.IControlHandle.ControlHandleType;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer.Layout;
import com.ait.lienzo.client.core.shape.wires.MagnetManager.Magnets;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeEndHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStartHandler;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepEvent;
import com.ait.lienzo.client.core.shape.wires.event.WiresResizeStepHandler;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.google.gwt.event.shared.HandlerRegistration;

public class WiresShape extends WiresContainer
{
    private final MultiPath             m_path;

    private Magnets                     m_magnets;

    private final LayoutContainer       m_innerLayoutContainer;

    private WiresShapeControlHandleList m_ctrls;

    private boolean                     m_resizable;

    private WiresShapeControl           m_control;

    public WiresShape(final MultiPath path)
    {
        this(path, new WiresLayoutContainer());
    }

    public WiresShape(final MultiPath path, final LayoutContainer layoutContainer)
    {
        super(layoutContainer.getGroup());

        m_path = path;

        m_ctrls = null;

        m_resizable = true;

        m_innerLayoutContainer = layoutContainer;

        m_innerLayoutContainer.getGroup().setEventPropagationMode(EventPropagationMode.FIRST_ANCESTOR);

        m_innerLayoutContainer.add(m_path);

        final BoundingBox box = m_path.refresh().getBoundingBox();

        m_innerLayoutContainer.setOffset(new Point2D(box.getX(), box.getY())).setSize(box.getWidth(), box.getHeight()).execute();
    }

    @Override
    public WiresShape setLocation(final Point2D p)
    {
        super.setLocation(p);

        return this;
    }

    public WiresShape addChild(final IPrimitive<?> child)
    {
        m_innerLayoutContainer.add(child);

        return this;
    }

    public WiresShape addChild(final IPrimitive<?> child, final Layout layout)
    {
        m_innerLayoutContainer.add(child, layout);

        return this;
    }

    public WiresShape removeChild(final IPrimitive<?> child)
    {
        m_innerLayoutContainer.remove(child);

        return this;
    }

    public WiresShapeControlHandleList getControls()
    {
        return m_ctrls;
    }

    void setWiresShapeControlHandleList(final WiresShapeControlHandleList list)
    {
        m_ctrls = list;
    }

    public IControlHandleList loadControls(final ControlHandleType type)
    {
        return _loadControls(type);
    }

    @Override
    public WiresShape setDraggable(final boolean draggable)
    {
        super.setDraggable(draggable);

        return this;
    }

    public WiresShape setResizable(final boolean resizable)
    {
        m_resizable = resizable;

        return this;
    }

    public boolean isResizable()
    {
        return m_resizable;
    }

    /**
     * If the shape's path parts/points have been updated programmatically (not via human events interactions),
     * you can call this method to update the children layouts, controls and magnets.
     * The WiresResizeEvent event is not fired as this method is supposed to be called by the developer.
     */
    public void refresh()
    {
        final WiresShapeControlHandleList list = _loadControls(IControlHandle.ControlHandleStandardType.RESIZE);

        if (null != list)
        {
            list.refresh();
        }
    }

    void setWiresShapeControl(final WiresShapeControl control)
    {
        m_control = control;
    }

    public WiresShapeControl getControl()
    {
        return m_control;
    }

    public MultiPath getPath()
    {
        return m_path;
    }

    public Magnets getMagnets()
    {
        return m_magnets;
    }

    public void setMagnets(final Magnets magnets)
    {
        m_magnets = magnets;
    }

    public void removeFromParent()
    {
        if (getParent() != null)
        {
            getParent().remove(this);
        }
    }

    public final HandlerRegistration addWiresResizeStartHandler(final WiresResizeStartHandler handler)
    {
        Objects.requireNonNull(handler);

        return getHandlerManager().addHandler(WiresResizeStartEvent.TYPE, handler);
    }

    public final HandlerRegistration addWiresResizeStepHandler(final WiresResizeStepHandler handler)
    {
        Objects.requireNonNull(handler);

        return getHandlerManager().addHandler(WiresResizeStepEvent.TYPE, handler);
    }

    public final HandlerRegistration addWiresResizeEndHandler(final WiresResizeEndHandler handler)
    {
        Objects.requireNonNull(handler);

        return getHandlerManager().addHandler(WiresResizeEndEvent.TYPE, new WiresResizeEndHandler()
        {
            @Override
            public void onShapeResizeEnd(final WiresResizeEndEvent event)
            {
                handler.onShapeResizeEnd(event);

                m_innerLayoutContainer.refresh();

                refresh();
            }
        });
    }

    public String uuid()
    {
        return getGroup().uuid();
    }

    private WiresShapeControlHandleList _loadControls(final ControlHandleType type)
    {
        final WiresShapeControlHandleList list = getControls();

        if (null != list)
        {
            list.destroy();

            setWiresShapeControlHandleList(null);
        }
        final Map<ControlHandleType, IControlHandleList> handles = getPath().getControlHandles(type);

        if (null != handles)
        {
            final IControlHandleList controls = handles.get(type);

            if ((null != controls) && (controls.isActive()))
            {
                setWiresShapeControlHandleList(createControlHandles(type, (ControlHandleList) controls));
            }
        }
        return getControls();
    }

    protected WiresShapeControlHandleList createControlHandles(final ControlHandleType type, final ControlHandleList controls)
    {
        return new WiresShapeControlHandleList(this, type, controls);
    }

    @Override
    public void shapeMoved()
    {
        super.shapeMoved();

        if (getMagnets() != null)
        {
            getControl().getMagnetsControl().shapeMoved();
        }
    }

    @Override
    protected void preDestroy()
    {
        super.preDestroy();

        m_innerLayoutContainer.destroy();

        removeHandlers();

        removeFromParent();
    }

    private void removeHandlers()
    {
        final WiresShapeControlHandleList list = getControls();

        if (null != list)
        {
            list.destroy();
        }
    }

    LayoutContainer getLayoutContainer()
    {
        return m_innerLayoutContainer;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass()))
        {
            return false;
        }
        final WiresShape that = (WiresShape) o;

        return getGroup().uuid() == that.getGroup().uuid();
    }

    @Override
    public int hashCode()
    {
        return getGroup().uuid().hashCode();
    }
}
