package com.codeguild.convinceme.gui;

import com.codeguild.convinceme.model.Proposition;
import java.awt.Color;
import java.awt.Graphics;

/**
 * <p>Description: A data node that can draw itself</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 *
 * @author Patti Schank
 */
public class DataNode extends Node {
    public DataNode(Proposition p) {
        super(p);
    }

    public void drawNode(Graphics g, int width, int height) {
        g.setColor((Color.green).brighter());
        g.fillRect(getX() - width / 2, getY() - height / 2, width, height);
        g.setColor(selected ? Color.red : Color.black);
        g.drawRect(getX() - width / 2, getY() - height / 2, width - 1, height - 1);
    }
}