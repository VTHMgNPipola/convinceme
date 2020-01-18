package com.codeguild.convinceme.gui;

import com.codeguild.convinceme.model.Proposition;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

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

    private int width, height;

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
        width = fm.stringWidth(mLabel) + 12;
        height = fm.getHeight() + 4;
        drawNode(g, width, height);
        g.setColor(Color.black);
        g.drawString(mLabel, getX() - (width - 10) / 2, (getY() - (height - 4) / 2) + fm.getAscent());
        g.setColor(old);
    }

    public void drawNode(Graphics g, int width, int height) {
        g.setColor(selected ? Color.red : Color.black);
        g.drawOval(getX() - width / 2, getY() - height / 2, width - 1, height - 1);
    }

    public boolean isInside(Point p) { // This method assumes the node has already been drawn
        return (p.x >= getX() && p.x <= getX() + width) &&
                (p.y >= getY() && p.y <= getY() + height);
    }
}
