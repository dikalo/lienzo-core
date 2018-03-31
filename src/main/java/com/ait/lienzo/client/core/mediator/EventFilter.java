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

package com.ait.lienzo.client.core.mediator;

import java.util.List;

import com.ait.lienzo.client.core.event.AbstractNodeHumanInputEvent;
import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.GwtEvent;

/**
 * EventFilter provides basic implementations of {@link IEventFilter}s.
 * Multiple event filters can be combined with {@link #and(IEventFilter...) and},
 * and {@link #or(IEventFilter...) or} and {@link #not(IEventFilter) not} operations.
 * To write a custom implementation, simply implement the {@link IEventFilter}
 * interface.
 * <p>
 * The following event filters are provided by the Lienzo toolkit:
 * <p>
 * <table cellpadding="4" cellspacing="1" style="background-color: #000000;">
 * <tr style="background: #CCCCCC;"><th>Filter</th><th>Event Type</th><th>Description</th></tr>
 * <tr style="background: #EEEEEE;"><td>ANY</td><td>any event</td><td>accepts all events</td></tr>
 * <tr style="background: #EEEEEE;"><td>BUTTON_LEFT</td><td>mouse event</td><td>whether the left mouse button is pressed</td></tr>
 * <tr style="background: #EEEEEE;"><td>BUTTON_MIDDLE</td><td>mouse event</td><td>whether the middle mouse button is pressed</td></tr>
 * <tr style="background: #EEEEEE;"><td>BUTTON_RIGHT</td><td>mouse event</td><td>whether the right mouse button is pressed</td></tr>
 * <tr style="background: #EEEEEE;"><td>CONTROL</td><td>mouse event</td><td>whether the Control key is pressed</td></tr>
 * <tr style="background: #EEEEEE;"><td>SHIFT</td><td>mouse event</td><td>whether the Shift key is pressed</td></tr>
 * <tr style="background: #EEEEEE;"><td>ALT</td><td>mouse event</td><td>whether the Alt key is pressed</td></tr>
 * <tr style="background: #EEEEEE;"><td>META</td><td>mouse event</td><td>whether the Meta key is pressed</td></tr>
 * </table>
 *
 * @since 1.1
 */
public final class EventFilter
{
    private static final IEventFilter[] FOR_TO_ARRAY  = new IEventFilter[0];

    public static final IEventFilter    ANY           = new AnyEventFilterOp();

    public static final IEventFilter    BUTTON_LEFT   = new ButtonLeftEventFilterOp();

    public static final IEventFilter    BUTTON_MIDDLE = new ButtonMiddleEventFilterOp();

    public static final IEventFilter    BUTTON_RIGHT  = new ButtonRightEventFilterOp();

    public static final IEventFilter    CONTROL       = new CtrlKeyEventFilterOp();

    public static final IEventFilter    META          = new MetaKeyEventFilterOp();

    public static final IEventFilter    SHIFT         = new ShiftKeyEventFilterOp();

    public static final IEventFilter    ALT           = new AltKeyEventFilterOp();

    private EventFilter()
    {
    }

    /**
     * Chains several filters together.
     * The resulting filter will return true, if at least one filter returns true.
     *
     * @param filters
     * @return IEventFilter
     */
    public static final IEventFilter or(final IEventFilter... filters)
    {
        return new OrOpEventFilter(filters);
    }

    /**
     * Chains several filters together.
     * The resulting filter will return true, if at least one filter returns true.
     *
     * @param filters
     * @return IEventFilter
     */
    public static final IEventFilter or(final List<IEventFilter> filters)
    {
        return new OrOpEventFilter(filters.toArray(FOR_TO_ARRAY));
    }

    /**
     * Chains several filters together.
     * The resulting filter will return false, if at least one filter returns false.
     *
     * @param filters
     * @return IEventFilter
     */
    public static final IEventFilter and(final IEventFilter... filters)
    {
        return new AndOpEventFilter(filters);
    }

    /**
     * Chains several filters together.
     * The resulting filter will return false, if at least one filter returns false.
     *
     * @param filters
     * @return IEventFilter
     */
    public static final IEventFilter and(final List<IEventFilter> filters)
    {
        return new AndOpEventFilter(filters.toArray(FOR_TO_ARRAY));
    }

    /**
     * The resulting filter will return false, if the specified filter returns true.
     *
     * @param filter IEventFilter.
     * @return IEventFilter
     */
    public static final IEventFilter not(final IEventFilter filter)
    {
        return new AbstractEventFilter()
        {
            @Override
            public final boolean test(final GwtEvent<?> event)
            {
                return (false == filter.test(event));
            }
        };
    }

    private static final class AnyEventFilterOp implements IEventFilter
    {
        @Override
        public final boolean test(final GwtEvent<?> event)
        {
            return true;
        }

        @Override
        public final boolean isEnabled()
        {
            return true;
        }

        @Override
        public final void setEnabled(final boolean enabled)
        {
        }
    }

    public static class ButtonLeftEventFilter extends AbstractEventFilter
    {
        @Override
        public boolean test(final GwtEvent<?> event)
        {
            if (event instanceof AbstractNodeMouseEvent<?, ?>)
            {
                return ((AbstractNodeMouseEvent<?, ?>) event).isButtonLeft();
            }
            else if (event instanceof MouseEvent<?>)
            {
                return AbstractNodeMouseEvent.isButtonLeft((MouseEvent<?>) event);
            }
            else
            {
                return false;
            }
        }
    }

    private static final class ButtonLeftEventFilterOp extends ButtonLeftEventFilter
    {
        @Override
        public final boolean isEnabled()
        {
            return true;
        }

        @Override
        public final void setEnabled(final boolean enabled)
        {
        }
    }

    public static class ButtonMiddleEventFilter extends AbstractEventFilter
    {
        @Override
        public boolean test(final GwtEvent<?> event)
        {
            if (event instanceof AbstractNodeMouseEvent<?, ?>)
            {
                return ((AbstractNodeMouseEvent<?, ?>) event).isButtonMiddle();
            }
            else if (event instanceof MouseEvent<?>)
            {
                return AbstractNodeMouseEvent.isButtonMiddle((MouseEvent<?>) event);
            }
            else
            {
                return false;
            }
        }
    }

    private final static class ButtonMiddleEventFilterOp extends ButtonMiddleEventFilter
    {
        @Override
        public final boolean isEnabled()
        {
            return true;
        }

        @Override
        public final void setEnabled(final boolean enabled)
        {
        }
    }

    public static class ButtonRightEventFilter extends AbstractEventFilter
    {
        @Override
        public boolean test(final GwtEvent<?> event)
        {
            if (event instanceof AbstractNodeMouseEvent<?, ?>)
            {
                return ((AbstractNodeMouseEvent<?, ?>) event).isButtonRight();
            }
            else if (event instanceof MouseEvent<?>)
            {
                return AbstractNodeMouseEvent.isButtonRight((MouseEvent<?>) event);
            }
            else
            {
                return false;
            }
        }
    }

    private final static class ButtonRightEventFilterOp extends ButtonRightEventFilter
    {
        @Override
        public final boolean isEnabled()
        {
            return true;
        }

        @Override
        public final void setEnabled(final boolean enabled)
        {
        }
    }

    public static class ShiftKeyEventFilter extends AbstractEventFilter
    {
        @Override
        public boolean test(final GwtEvent<?> event)
        {
            if (event instanceof AbstractNodeHumanInputEvent<?, ?>)
            {
                return ((AbstractNodeHumanInputEvent<?, ?>) event).isShiftKeyDown();
            }
            else if (event instanceof HumanInputEvent<?>)
            {
                return AbstractNodeHumanInputEvent.isShiftKeyDown((HumanInputEvent<?>) event);
            }
            else
            {
                return false;
            }
        }
    }

    private final static class ShiftKeyEventFilterOp extends ShiftKeyEventFilter
    {
        @Override
        public final boolean isEnabled()
        {
            return true;
        }

        @Override
        public final void setEnabled(final boolean enabled)
        {
        }
    }

    public static class CtrlKeyEventFilter extends AbstractEventFilter
    {
        @Override
        public boolean test(final GwtEvent<?> event)
        {
            if (event instanceof AbstractNodeHumanInputEvent<?, ?>)
            {
                return ((AbstractNodeHumanInputEvent<?, ?>) event).isControlKeyDown();
            }
            else if (event instanceof HumanInputEvent<?>)
            {
                return AbstractNodeHumanInputEvent.isControlKeyDown((HumanInputEvent<?>) event);
            }
            else
            {
                return false;
            }
        }
    }

    private final static class CtrlKeyEventFilterOp extends CtrlKeyEventFilter
    {
        @Override
        public final boolean isEnabled()
        {
            return true;
        }

        @Override
        public final void setEnabled(final boolean enabled)
        {
        }
    }

    public static class MetaKeyEventFilter extends AbstractEventFilter
    {
        @Override
        public boolean test(final GwtEvent<?> event)
        {
            if (event instanceof AbstractNodeHumanInputEvent<?, ?>)
            {
                return ((AbstractNodeHumanInputEvent<?, ?>) event).isMetaKeyDown();
            }
            else if (event instanceof HumanInputEvent<?>)
            {
                return AbstractNodeHumanInputEvent.isMetaKeyDown((HumanInputEvent<?>) event);
            }
            else
            {
                return false;
            }
        }
    }

    private final static class MetaKeyEventFilterOp extends MetaKeyEventFilter
    {
        @Override
        public final boolean isEnabled()
        {
            return true;
        }

        @Override
        public final void setEnabled(final boolean enabled)
        {
        }
    }

    public static class AltKeyEventFilter extends AbstractEventFilter
    {
        @Override
        public boolean test(final GwtEvent<?> event)
        {
            if (event instanceof AbstractNodeHumanInputEvent<?, ?>)
            {
                return ((AbstractNodeHumanInputEvent<?, ?>) event).isAltKeyDown();
            }
            else if (event instanceof HumanInputEvent<?>)
            {
                return AbstractNodeHumanInputEvent.isAltKeyDown((HumanInputEvent<?>) event);
            }
            else
            {
                return false;
            }
        }
    }

    private final static class AltKeyEventFilterOp extends AltKeyEventFilter
    {
        @Override
        public final boolean isEnabled()
        {
            return true;
        }

        @Override
        public final void setEnabled(final boolean enabled)
        {
        }
    }

    private static final class AndOpEventFilter extends AbstractEventFilter
    {
        private final int            m_size;

        private final IEventFilter[] m_list;

        public AndOpEventFilter(final IEventFilter[] filters)
        {
            m_list = filters;

            m_size = filters.length;
        }

        @Override
        public final boolean test(final GwtEvent<?> event)
        {
            for (int i = 0; i < m_size; i++)
            {
                final IEventFilter filter = m_list[i];

                if (filter.isEnabled())
                {
                    if (false == filter.test(event))
                    {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private static final class OrOpEventFilter extends AbstractEventFilter
    {
        private final int            m_size;

        private final IEventFilter[] m_list;

        public OrOpEventFilter(final IEventFilter[] filters)
        {
            m_list = filters;

            m_size = filters.length;
        }

        @Override
        public final boolean test(final GwtEvent<?> event)
        {
            for (int i = 0; i < m_size; i++)
            {
                final IEventFilter filter = m_list[i];

                if (filter.isEnabled())
                {
                    if (filter.test(event))
                    {
                        return true;
                    }
                }
            }
            return true;
        }
    }
}
