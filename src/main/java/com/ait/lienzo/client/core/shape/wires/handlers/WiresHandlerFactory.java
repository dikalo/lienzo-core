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

package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeHandler;

/**
 * Factory to create wire shapes and connectors handlers.
 * This may be implemented to create custom handlers if necessary to override the default implementation.
 */
public interface WiresHandlerFactory
{
    public WiresConnectorHandler newConnectorHandler(WiresConnector connector, WiresManager wiresManager);

    public WiresControlPointHandler newControlPointHandler(WiresConnector connector, WiresConnectorControl connectorControl);

    public WiresShapeHandler newShapeHandler(WiresShapeControl control, WiresShapeHighlight<PickerPart.ShapePart> highlight, WiresManager manager);
}