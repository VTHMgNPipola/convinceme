package com.codeguild.convinceme.gui;

import java.awt.Color;
import java.awt.Graphics;

/**
 * <p>Description: An explanation edge that can draw itself.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 *
 * @author Patti Schank
 */

public class Edge {
    protected Node mFrom, mTo;

    public void setFrom(Node n) {
        mFrom = n;
    }

    public void setTo(Node n) {
        mTo = n;
    }

    public void draw(Graphics g) {
        Color old = g.getColor();
        int x1 = mFrom.getX();
        int y1 = mFrom.getY();
        int x2 = mTo.getX();
        int y2 = mTo.getY();
        drawEdge(g, x1, y1, x2, y2);
        g.setColor(old);
    }

    public void drawEdge(Graphics g, int x1, int y1, int x2, int y2) {
        g.setColor(Color.black);
        g.drawLine(x1, y1, x2, y2);
    }
}




