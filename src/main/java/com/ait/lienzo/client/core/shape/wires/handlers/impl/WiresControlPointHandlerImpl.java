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
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlPointHandler;
import com.ait.lienzo.client.core.types.Point2D;

public class WiresControlPointHandlerImpl implements WiresControlPointHandler
{
    private final WiresConnector        m_connector;

    private final WiresConnectorControl m_connectorControl;

    public WiresControlPointHandlerImpl(final WiresConnector connector, final WiresConnectorControl connectorControl)
    {
        this.m_connector = connector;

        this.m_connectorControl = connectorControl;
    }

    @Override
    public void onNodeMouseDoubleClick(final NodeMouseDoubleClickEvent event)
    {
        if (m_connector.getPointHandles().isVisible())
        {
            m_connectorControl.destroyControlPoint((IPrimitive<?>) event.getSource());

            m_connector.getLine().getLayer().batch();
        }
    }

    @Override
    public void onNodeDragEnd(final NodeDragEndEvent event)
    {
        //no default implementation
    }

    @Override
    public void onNodeDragStart(final NodeDragStartEvent event)
    {
        //no default implementation
    }

    @Override
    public void onNodeDragMove(final NodeDragMoveEvent event)
    {
        final IPrimitive<?> primitive = (IPrimitive<?>) event.getSource();

        final Point2D adjust = m_connectorControl.adjustControlPointAt(primitive.getX(), primitive.getY(), event.getX(), event.getY());

        if (adjust != null)
        {
            primitive.setX(adjust.getX());

            primitive.setY(adjust.getY());
        }
    }
}
