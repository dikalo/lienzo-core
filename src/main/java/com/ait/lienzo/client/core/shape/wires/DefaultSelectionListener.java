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

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorControlImpl;

public class DefaultSelectionListener implements SelectionListener
{
    public DefaultSelectionListener(final Layer layer, final SelectionManager.SelectedItems selectedItems)
    {
    }

    @Override
    public void onChanged(final SelectionManager.SelectedItems selectedItems)
    {
        final SelectionManager.ChangedItems changed = selectedItems.getChanged();


        // leaving in comments for now, as I re-enable those during debug, if there are problems.
        //            for (WiresShape shape : selectedItems.getShapes())
        //            {
        //                Console.get().info(shape.getContainer().getUserData().toString() + " : " + shape.getGroup().getLocation());
        //            }
        //
        //            for (WiresConnector connector : selectedItems.getConnectors())
        //            {
        //                Console.get().info("connector + " + connector.getGroup().uuid());
        //            }
        //
        //            for (WiresShape shape : changed.getRemovedShapes())
        //            {
        //                Console.get().info("removed" + shape.getContainer().getUserData().toString() + " : " + shape.getGroup().getLocation());
        //            }
        //
        //            for (WiresConnector connector : changed.getRemovedConnectors())
        //            {
        //                Console.get().info("removed connector + "  + connector.getGroup().uuid() );
        //            }
        //
        //            for (WiresShape shape : changed.getAddedShapes())
        //            {
        //                Console.get().info("added" + shape.getContainer().getUserData().toString() + " : " + shape.getGroup().getLocation());
        //            }
        //
        //            for (WiresConnector connector : changed.getAddedConnectors())
        //            {
        //                Console.get().info("added connector + "  + connector.getGroup().uuid() );
        //            }
        //
        for (final WiresShape shape : changed.getRemovedShapes())
        {
            //Console.get().info("unselected" + shape.getContainer().getUserData().toString() + " : " + shape.getGroup().getLocation());
            unselect(shape);
        }
        for (final WiresConnector connector : changed.getRemovedConnectors())
        {
            unselect(connector);
        }
        if (!selectedItems.isSelectionGroup() && (selectedItems.size() == 1))
        {
            // it's one or the other, so attempt both, it'll short circuit if the first selects.
            if (selectedItems.getShapes().size() == 1)
            {
                for (final WiresShape shape : selectedItems.getShapes())
                {
                    //                    Console.get().info("select" + shape.getContainer().getUserData().toString() + " : " + shape.getGroup().getLocation());
                    select(shape);
                    break;
                }
            }
            else
            {
                for (final WiresConnector connector : selectedItems.getConnectors())
                {
                    select(connector);
                    break;
                }
            }
        }
        else if (selectedItems.isSelectionGroup())
        {
            // we don't which have selectors shown, if any. Just iterate and unselect all
            // null check will do nothing, if it's already unselected.
            for (final WiresShape shape : selectedItems.getShapes())
            {
                //                Console.get().info("unselected" + shape.getContainer().getUserData().toString() + " : " + shape.getGroup().getLocation());
                unselect(shape);
            }
            for (final WiresConnector connector : selectedItems.getConnectors())
            {
                unselect(connector);
            }
        }
    }

    private void select(final WiresShape shape)
    {
        if (shape.getControls() != null)
        {
            shape.getControls().show();
        }
    }

    private void unselect(final WiresShape shape)
    {
        if (shape.getControls() != null)
        {
            shape.getControls().hide();
        }
    }

    private void select(final WiresConnector connector)
    {
        ((WiresConnectorControlImpl) connector.getWiresConnectorHandler().getControl()).showControlPoints();
    }

    private void unselect(final WiresConnector connector)
    {
        ((WiresConnectorControlImpl) connector.getWiresConnectorHandler().getControl()).hideControlPoints();
    }
}
