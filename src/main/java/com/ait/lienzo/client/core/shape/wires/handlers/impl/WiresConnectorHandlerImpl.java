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

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragStartEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorHandler;

public class WiresConnectorHandlerImpl implements WiresConnectorHandler
{
    private final WiresConnectorControl m_control;

    private final WiresConnector        m_connector;

    private final WiresManager          m_wiresManager;

    public WiresConnectorHandlerImpl(final WiresConnector connector, final WiresManager wiresManager)
    {
        this.m_control = wiresManager.getControlFactory().newConnectorControl(connector, wiresManager);
        this.m_connector = connector;
        m_wiresManager = wiresManager;
    }

    @Override
    public void onNodeDragStart(final NodeDragStartEvent event)
    {
        this.m_control.onMoveStart(event.getDragContext().getDragStartX(), event.getDragContext().getDragStartY());
    }

    @Override
    public void onNodeDragMove(final NodeDragMoveEvent event)
    {
        this.m_control.onMove(event.getDragContext().getDragStartX(), event.getDragContext().getDragStartY());
    }

    @Override
    public void onNodeDragEnd(final NodeDragEndEvent event)
    {
        this.m_control.onMoveComplete();
    }

    @Override
    public void onNodeMouseClick(final NodeMouseClickEvent event)
    {
        if (m_wiresManager.getSelectionManager() != null)
        {
            m_wiresManager.getSelectionManager().selected(m_connector, event.isShiftKeyDown());
        }
    }

    @Override
    public void onNodeMouseDoubleClick(final NodeMouseDoubleClickEvent event)
    {
        m_control.addControlPoint(event.getX(), event.getY());
    }

    @Override
    public WiresConnectorControl getControl()
    {
        return m_control;
    }
}