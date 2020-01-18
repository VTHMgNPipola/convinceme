package com.codeguild.convinceme.gui;

import com.codeguild.convinceme.model.Proposition;
import java.awt.Color;
import java.awt.Graphics;

/**
 * <p>Description: A hypothesis node that can draw itself</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 *
 * @author patti
 */
public class HypothesisNode extends Node {

    public HypothesisNode(Proposition p) {
        super(p);
    }

    public void drawNode(Graphics g, int w, int h) {
        g.setColor((Color.yellow).brighter());
        w += 8;
        h += 6;
        g.fillOval(getX() - w / 2, getY() - h / 2, w, h);
        g.setColor(selected ? Color.red : Color.black);
        g.drawOval(getX() - w / 2, getY() - h / 2, w - 1, h - 1);
    }
}