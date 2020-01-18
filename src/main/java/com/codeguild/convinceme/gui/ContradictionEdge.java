package com.codeguild.convinceme.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * <p>Description: A contradiction edge that can draw itself.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 *
 * @author Patti Schank
 */
public class ContradictionEdge extends Edge {
    private static final Stroke dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
            10.0f, new float[]{10.0f}, 0.0f);

    public ContradictionEdge() {
    }

    public void drawEdge(Graphics graphics, int x1, int y1, int x2, int y2) {
        Graphics2D g = (Graphics2D) graphics;
        g.setColor(Color.red);
        Stroke oldStroke = g.getStroke();
        g.setStroke(dashedStroke);
        g.drawLine(x1, y1, x2, y2);
        g.setStroke(oldStroke);
    }
}
