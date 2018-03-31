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

import com.ait.lienzo.client.core.types.Point2D;

/**
 * Control for changing locations for wires objects (shapes, connectors, etc).
 *
 * This type allows decoupling the drag handlers added by default to wires objects from each control's logic.
 */
public interface WiresMoveControl
{
    /**
     * The move is starting at this point.
     */
    public void onMoveStart(double x, double y);

    /**
     * Moving the wires object a certain distance (dx, dy) from
     * the starting point.
     * @return <code>true</code> if the wires object location should be adjusted, and
     * <code>false</code> in case no adjustment is required.
     */
    public boolean onMove(double dx, double dy);

    /**
     * The moving has been completed, so operations can be performed at this point.
     * @return <code>true</code> in case everything if fine, operations completed
     * and no errors. Returns <code>false</code> otherwise.
     */
    public boolean onMoveComplete();

    /**
     * Returns the current adjustment to apply to the wires object, in case
     * the <code>onMove</code> method returns <code>true</code>.
     */
    public Point2D getAdjust();
}
