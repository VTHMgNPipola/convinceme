package com.codeguild.convinceme.gui;

import com.codeguild.convinceme.model.Proposition;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * <p>Description: A node that can draw itself</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 *
 * @author Patti Schank
 */

public class Node {
    protected Proposition mProp;
    protected String mLabel = " ";
    protected boolean selected;

    public Node(Proposition p) {
        mProp = p;
        setText();
    }

    public int getX() {
        return mProp.getX();
    }

    public int getY() {
        return mProp.getY();
    }

    public String getLabel() {
        return mProp.getLabel();
    }

    public void setX(int x) {
        mProp.setX(x);
    }

    public void setY(int y) {
        mProp.setY(y);
    }

    public void setText() {
        String s = mProp.getText();
        mLabel = s.substring(0, Math.min(s.length(), 12)) + "...";
    }

    public void setFullText() {
        String s = mProp.getText();
        mLabel = s.substring(0, Math.min(s.length(), 60));
    }

    public String getFullText() {
        return mProp.getText();
    }

    public void draw(Graphics g, boolean selected) {
        this.selected = selected;
        FontMetrics fm = g.getFontMetrics();
        Color old = g.getColor();
        int w = fm.stringWidth(mLabel) + 12;
        int h = fm.getHeight() + 4;
        drawNode(g, w, h);
        g.setColor(Color.black);
        g.drawString(mLabel, getX() - (w - 10) / 2, (getY() - (h - 4) / 2) + fm.getAscent());
        g.setColor(old);
    }

    public void drawNode(Graphics g, int w, int h) {
        g.setColor(selected ? Color.red : Color.black);
        g.drawOval(getX() - w / 2, getY() - h / 2, w - 1, h - 1);
    }
}



