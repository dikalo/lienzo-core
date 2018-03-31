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

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.picker.ColorMapBackedPicker;

public class WiresParentPickerCachedControl extends WiresParentPickerControlImpl
{
    private double  m_x;

    private double  m_y;

    private boolean adjusted;

    public WiresParentPickerCachedControl(final WiresShape m_shape, final ColorMapBackedPicker.PickerOptions pickerOptions)
    {
        super(m_shape, pickerOptions);

        clear();
    }

    public WiresParentPickerCachedControl(final WiresShapeLocationControlImpl shapeLocationControl, final ColorMapBackedPicker.PickerOptions pickerOptions)
    {
        super(shapeLocationControl, pickerOptions);

        clear();
    }

    public WiresParentPickerCachedControl(final WiresShapeLocationControlImpl shapeLocationControl, final ColorMapBackedPickerProvider colorMapBackedPickerProvider)
    {
        super(shapeLocationControl, colorMapBackedPickerProvider);
    }

    @Override
    public void onMoveStart(final double x, final double y)
    {
        if ((x != m_x) || (y != m_y))
        {
            clear();

            super.onMoveStart(x, y);

            m_x = x;

            m_y = y;
        }
    }

    @Override
    public boolean onMove(final double dx, final double dy)
    {
        if ((dx != m_x) || (dy != m_y))
        {
            adjusted = super.onMove(dx, dy);

            m_x = dx;

            m_y = dy;
        }
        return adjusted;
    }

    @Override
    public boolean onMoveComplete()
    {
        return true;
    }

    @Override
    public void clear()
    {
        super.clear();

        m_x = 0;

        m_y = 0;

        adjusted = false;
    }
}
