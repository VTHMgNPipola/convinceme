package com.codeguild.convinceme.gui;

import java.awt.Color;
import java.awt.Graphics;

/**
 * <p>Description: A contradiction edge that can draw itself.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 *
 * @author Patti Schank
 */
public class ContradictionEdge extends Edge {

    public ContradictionEdge() {
    }

    public void drawEdge(Graphics g, int x1, int y1, int x2, int y2) {
        g.setColor(Color.red);
        int dashLength = 10;
        double dist = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        int numDashes = (int) Math.round(dist) / dashLength;
        int dx = (x2 - x1) / (numDashes * 2);
        int dy = (y2 - y1) / (numDashes * 2);
        for (int i = 0; i < numDashes; i++) {
            g.drawLine(x1, y1, x1 + dx, y1 + dy);
            x1 = x1 + (dx * 2);
            y1 = y1 + (dy * 2);
        }
    }
}
