package com.ait.lienzo.client.core.shape.wires.handlers;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.shape.wires.PickerPart;
import com.ait.lienzo.client.core.shape.wires.WiresContainer;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeHandler;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.widget.DragContext;

/**
 * This handler's goals are:
 * - Delegate some mouse interactions for a single wires shape to its wires shape m_control instance
 * - Displays some highlights to provide feedback for containment and docking operations.
 */
public class WiresShapeHandlerImpl extends WiresManager.WiresDragHandler implements WiresShapeHandler {

    private final WiresShapeControl                         m_control;
    private final WiresShapeHighlight<PickerPart.ShapePart> m_highlight;

    public WiresShapeHandlerImpl(final WiresShapeControl control, final WiresShapeHighlight<PickerPart.ShapePart> highlight, final WiresManager manager)
    {
        super(manager);
        this.m_control = control;
        this.m_highlight = highlight;
    }

    @Override
    public void startDrag(DragContext dragContext)
    {
        super.startDrag(dragContext);

        // Delegate start dragging to shape m_control.
        m_control.onMoveStart(dragContext.getDragStartX(), dragContext.getDragStartY());

        // Highlights.
        final WiresShape parent = getParentShape();
        if (null != parent)
        {
            if (isDocked(getShape()))
            {
                m_highlight.highlight(getParentShape(), PickerPart.ShapePart.BORDER);
            }
            else
            {
                m_highlight.highlight(getParentShape(), PickerPart.ShapePart.BODY);
            }
        }
        batch();
    }

    @Override
    protected boolean doAdjust(Point2D dxy)
    {

        // Keep parent shape and part instances before moving to another location.
        final WiresShape parent = getParentShape();
        final PickerPart.ShapePart parentPart = getParentShapePart();

        boolean adjusted = false;
        // Delegate drag adjustments to shape m_control.
        if (m_control.onMove(dxy.getX(), dxy.getY()))
        {
            dxy.set(m_control.getAdjust());
            adjusted = true;
        }

        // Check acceptors' allow methods.
        final boolean isDockAllowed = null != getControl().getDockingControl() && getControl().getDockingControl().isAllow();
        final boolean isContAllow = null != getControl().getContainmentControl() && getControl().getContainmentControl().isAllow();

        // Highlights.
        final WiresShape newParent = getParentShape();
        final PickerPart.ShapePart newParentPart = getParentShapePart();
        if (parent != newParent || parentPart != newParentPart)
        {
            m_highlight.restore();
        }

        if (null != newParent)
        {
            if (isDockAllowed)
            {
                m_highlight.highlight(newParent, PickerPart.ShapePart.BORDER);
            }
            else if (isContAllow)
            {
                m_highlight.highlight(newParent, PickerPart.ShapePart.BODY);
            }
            else
            {
                m_highlight.error(newParent, PickerPart.ShapePart.BODY);
            }
        }

        batch();

        return adjusted;
    }

    @Override
    protected void doOnNodeDragEnd(NodeDragEndEvent event)
    {
        final int dx = event.getDragContext().getDx();
        final int dy = event.getDragContext().getDy();
        m_control.onMove(dx, dy);

        // Complete the m_control operation.
        if (m_control.onMoveComplete() && m_control.accept())
        {
            m_control.execute();
        }
        else
        {
            reset();
        }

        // Restore highlights, if any.
        m_highlight.restore();

        batch();
    }

    @Override
    protected void doReset()
    {
        super.doReset();
        m_highlight.restore();
    }

    @Override
    public void onNodeMouseClick(NodeMouseClickEvent event)
    {
        m_control.onMouseClick(new MouseEvent(event.getX(), event.getY(), event.isShiftKeyDown(), event.isAltKeyDown(), event.isControlKeyDown()));
    }

    @Override
    public void onNodeMouseDown(NodeMouseDownEvent event)
    {
        m_control.onMouseDown(new MouseEvent(event.getX(), event.getY(), event.isShiftKeyDown(), event.isAltKeyDown(), event.isControlKeyDown()));
    }

    @Override
    public void onNodeMouseUp(NodeMouseUpEvent event)
    {
        m_control.onMouseUp(new MouseEvent(event.getX(), event.getY(), event.isShiftKeyDown(), event.isAltKeyDown(), event.isControlKeyDown()));
    }

    @Override
    public WiresShapeControl getControl()
    {
        return m_control;
    }

    private WiresShape getShape()
    {
        return getControl().getParentPickerControl().getShape();
    }

    private WiresShape getParentShape()
    {
        final WiresContainer parent = getControl().getParentPickerControl().getParent();
        return null != parent && parent instanceof WiresShape ? (WiresShape) parent : null;
    }

    private PickerPart.ShapePart getParentShapePart()
    {
        return getControl().getParentPickerControl().getParentShapePart();
    }

    private boolean isDocked(final WiresShape shape)
    {
        return null != shape.getDockedTo();
    }

    private void batch()
    {
        getShape().getGroup().getLayer().batch();
    }
}
