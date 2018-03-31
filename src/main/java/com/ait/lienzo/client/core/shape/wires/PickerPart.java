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

public class PickerPart
{
    public enum ShapePart
    {
        BORDER, BORDER_HOTSPOT, BODY
    }

    private final WiresShape m_shape;

    private final ShapePart  m_part;

    public PickerPart(final WiresShape shape, final ShapePart part)
    {
        m_shape = shape;

        m_part = part;
    }

    public WiresShape getShape()
    {
        return m_shape;
    }

    public ShapePart getShapePart()
    {
        return m_part;
    }

    @Override
    public String toString()
    {
        return getShapePart().toString() + " for " + getShape().toString();
    }
}
