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

    public void drawNode(Graphics g, int width, int height) {
        g.setColor((Color.yellow).brighter());
        width += 8;
        height += 6;
        g.fillOval(getX() - width / 2, getY() - height / 2, width, height);
        g.setColor(selected ? Color.red : Color.black);
        g.drawOval(getX() - width / 2, getY() - height / 2, width - 1, height - 1);
    }
}