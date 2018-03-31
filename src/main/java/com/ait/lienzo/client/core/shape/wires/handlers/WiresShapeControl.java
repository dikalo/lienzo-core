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

/**
 * The Wires Shape control type.
 * This orchestrates the different controls related to Wires Shapes.
 */
public interface WiresShapeControl extends WiresMoveControl, WiresMouseControl, WiresControl, WiresBoundsConstraintControl
{
    public void setAlignAndDistributeControl(AlignAndDistributeControl control);

    public WiresMagnetsControl getMagnetsControl();

    public AlignAndDistributeControl getAlignAndDistributeControl();

    public WiresDockingControl getDockingControl();

    public WiresContainmentControl getContainmentControl();

    public WiresParentPickerControl getParentPickerControl();

    public boolean accept();
}
