package com.worldofwonder.view;

import com.worldofwonder.model.*;
import com.worldofwonder.controller.*;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.FlowLayout;

public final class UIUtil {

    private UIUtil() {
    }

    public static void fixedSize(JComponent component, int width, int height) {
        Dimension size = new Dimension(width, height);
        component.setPreferredSize(size);
        component.setMinimumSize(size);
        component.setMaximumSize(size);
    }

    public static void fullWidth(JComponent component, int height) {
        component.setPreferredSize(new Dimension(component.getPreferredSize().width, height));
        component.setMinimumSize(new Dimension(0, height));
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    public static JPanel centered(JComponent child) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(child);
        return wrapper;
    }

    public static void flexWidth(JComponent component, int height) {
        component.setPreferredSize(new Dimension(component.getPreferredSize().width, height));
        component.setMinimumSize(new Dimension(140, height));
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    public static void fixedHeight(JComponent component, int height) {
        Dimension cur = component.getPreferredSize();
        component.setPreferredSize(new Dimension(cur.width, height));
        component.setMinimumSize(new Dimension(cur.width, height));
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    /** Set a minimum width while letting the component stretch to fill available space. */
    public static void minWidth(JComponent component, int width, int height) {
        component.setPreferredSize(new Dimension(width, height));
        component.setMinimumSize(new Dimension(width, height));
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    /** Set a flexible width that grows with the container, with a max cap. */
    public static void flex(JComponent component, int prefWidth, int height, int maxWidth) {
        component.setPreferredSize(new Dimension(prefWidth, height));
        component.setMinimumSize(new Dimension(prefWidth, height));
        component.setMaximumSize(new Dimension(maxWidth, height));
    }

    /** Wrap a component in a GridBagLayout-centered panel for use inside a card. */
    public static JPanel wrapCentered(JComponent child) {
        JPanel wrapper = new JPanel(new java.awt.GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(child);
        return wrapper;
    }

    /** Set min+preferred+maximum to the same flexible dimensions (pref w, min, max). */
    public static void flexSize(JComponent component, int prefW, int prefH, int minW, int maxW) {
        component.setPreferredSize(new Dimension(prefW, prefH));
        component.setMinimumSize(new Dimension(minW, prefH));
        component.setMaximumSize(new Dimension(maxW, prefH));
    }

    /** Full four-way bounds, so a component can shrink in both axes on small windows. */
    public static void responsiveSize(JComponent component, int prefW, int prefH,
                                      int minW, int minH, int maxW, int maxH) {
        component.setPreferredSize(new Dimension(prefW, prefH));
        component.setMinimumSize(new Dimension(minW, minH));
        component.setMaximumSize(new Dimension(maxW, maxH));
    }

    /**
     * Size a dialog to its content and centre it on its owner, never growing
     * past the owner. Sizing a window before its content has been laid out is what
     * leaves a dialog looking off-centre, so always pack first.
     */
    public static void centreOnOwner(java.awt.Window dialog, java.awt.Window owner,
                                     int minW, int minH) {
        dialog.pack();
        Dimension want = dialog.getSize();
        if (owner != null && owner.isShowing()) {
            Dimension avail = owner.getSize();
            Insets in = owner.getInsets();
            int maxW = Math.max(minW, avail.width - in.left - in.right - 16);
            int maxH = Math.max(minH, avail.height - in.top - in.bottom - 16);
            want = new Dimension(Math.min(want.width, maxW), Math.min(want.height, maxH));
        }
        want.width = Math.max(want.width, minW);
        want.height = Math.max(want.height, minH);
        dialog.setSize(want);
        dialog.setLocationRelativeTo(owner);
    }

    /**
     * Borderless, transparent vertical scroller: invisible until it is needed.
     * The view is pinned to the viewport's width so a wide preferred size can never
     * silently push content off the right edge when the window shrinks.
     */
    public static JScrollPane verticalScroller(final JComponent child) {
        JScrollPane scroll = new JScrollPane();
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JViewport viewport = new JViewport() {
            @Override
            public void setBounds(int x, int y, int w, int h) {
                super.setBounds(x, y, w, h);
                Component v = getView();
                if (v instanceof JComponent && w > 0) {
                    JComponent view = (JComponent) v;
                    view.setSize(w, Math.max(h, view.getPreferredSize().height));
                }
            }
        };
        viewport.setOpaque(false);
        scroll.setViewport(viewport);
        viewport.setView(child);
        return scroll;
    }
}
