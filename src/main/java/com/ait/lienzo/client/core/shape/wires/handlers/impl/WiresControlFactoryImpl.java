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

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectionControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;

public class WiresControlFactoryImpl implements WiresControlFactory
{
    @Override
    public WiresShapeControl newShapeControl(final WiresShape shape, final WiresManager wiresManager)
    {
        return new WiresShapeControlImpl(shape, wiresManager);
    }

    @Override
    public WiresCompositeControl newCompositeControl(final WiresCompositeControl.Context selectionContext, final WiresManager wiresManager)
    {
        return new WiresCompositeControlImpl(selectionContext);
    }

    @Override
    public WiresShapeHighlight<PickerPart.ShapePart> newShapeHighlight(final WiresManager wiresManager)
    {
        return new WiresShapeHighlightImpl(wiresManager.getDockingAcceptor().getHotspotSize());
    }

    @Override
    public WiresConnectorControl newConnectorControl(final WiresConnector connector, final WiresManager wiresManager)
    {
        return new WiresConnectorControlImpl(connector, wiresManager);
    }

    @Override
    public WiresConnectionControl newConnectionControl(final WiresConnector connector, final boolean headNotTail, final WiresManager wiresManager)
    {
        return new WiresConnectionControlImpl(connector, headNotTail, wiresManager);
    }
}
