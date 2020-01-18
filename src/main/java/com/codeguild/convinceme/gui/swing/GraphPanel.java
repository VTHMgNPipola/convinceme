package com.codeguild.convinceme.gui.swing;

import com.codeguild.convinceme.gui.ContradictionEdge;
import com.codeguild.convinceme.gui.DataNode;
import com.codeguild.convinceme.gui.Edge;
import com.codeguild.convinceme.gui.ExplanationEdge;
import com.codeguild.convinceme.gui.HypothesisNode;
import com.codeguild.convinceme.gui.Node;
import com.codeguild.convinceme.model.Link;
import com.codeguild.convinceme.model.LinkVector;
import com.codeguild.convinceme.model.Proposition;
import com.codeguild.convinceme.model.PropositionVector;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JPanel;

/**
 * <p>Description: Panel on which graph is drawn.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 *
 * @author Patti Schank
 */

public class GraphPanel extends JPanel {
    private List<Node> mNodes;
    private Node mSelectedNode;
    private Node mEnteredNode;
    private List<Edge> mEdges;
    private DiagramPanel mDiagramPanel;

    public GraphPanel(DiagramPanel diagramPanel) {
        initGraph();
        mDiagramPanel = diagramPanel;
        setLayout(new BorderLayout(1, 1));
        setSize(getPreferredSize());
        setBackground(Color.white);
        repaint();

        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                double bestdist = Double.MAX_VALUE;
                int x = e.getX();
                int y = e.getY();
                for (Node n : mNodes) {
                    double dist = (n.getX() - x) * (n.getX() - x) + (n.getY() - y) * (n.getY() - y);
                    if (dist < bestdist) {
                        mSelectedNode = n;
                        bestdist = dist;
                    }
                }
                if (mSelectedNode != null) {
                    mSelectedNode.setX(x);
                    mSelectedNode.setY(y);
                    repaint();
                }
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                double threshHold = 652; // 25 squared
                double mindist = threshHold;
                int x = e.getX();
                int y = e.getY();
                for (Node n : mNodes) {
                    double dist = (n.getX() - x) * (n.getX() - x) + (n.getY() - y) * (n.getY() - y);
                    if (dist < mindist) {
                        mEnteredNode = n;
                        mindist = dist;
                    }
                }
                if (mindist < threshHold) { // got within threshold pixels of a node
                    mDiagramPanel.setText(mEnteredNode.getFullText());
                }
            }
        });
        this.addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
                mEnteredNode = null;
                mDiagramPanel.setText("");
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (mSelectedNode != null) {
                    int x = e.getX();
                    int y = e.getY();
                    mSelectedNode.setX(x);
                    mSelectedNode.setY(y);
                    repaint();
                }
            }
        });
    }

    public void initGraph() {
        mNodes = new ArrayList<>();
        mEdges = new ArrayList<>();
    }

    public void setGraph(PropositionVector h, PropositionVector d, LinkVector e, LinkVector c) {
        initGraph();
        graph(h);
        graph(d);
        graph(e);
        graph(c);
        repaint();
    }

    public void graph(PropositionVector pv) {
        for (Enumeration<Proposition> e = pv.elements(); e.hasMoreElements(); ) {
            Proposition p = e.nextElement();
            addNode(p);
        }
    }

    public void graph(LinkVector lv) {
        for (Enumeration<Link> e = lv.elements(); e.hasMoreElements(); ) {
            addEdge(e.nextElement());
        }
    }

    private Node findNode(Proposition p) {
        for (int i = 0; i < mNodes.size(); i++) {
            if (mNodes.get(i).getLabel().equals(p.getLabel())) {
                return mNodes.get(i);
            }
        }
        return addNode(p);
    }

    private Node addNode(Proposition p) {
        Node n;
        int w = 200;
        int h = 200;
        if (p.isData()) {
            n = new DataNode(p);
            if (!p.isPlotted()) {
                p.setX((int) (20 + (w - 40) * Math.random()));
                p.setY((int) (h / 2 + 20 + (h / 2 - 40) * Math.random()));
            }
        } else {
            n = new HypothesisNode(p);
            if (!p.isPlotted()) {
                p.setX((int) (20 + (w - 40) * Math.random()));
                p.setY((int) (20 + (h / 2 - 40) * Math.random()));
            }
        }
        mNodes.add(n);
        return n;
    }

    private void addEdge(Link l) {
        Edge edge;
        PropositionVector starts = l.getExplainers();
        Proposition end = l.getExplained();

        for (Enumeration<Proposition> e = starts.elements(); e.hasMoreElements(); ) {
            if (l.isContradiction()) {
                edge = new ContradictionEdge();
            } else {
                edge = new ExplanationEdge();
            }
            edge.setFrom(findNode(e.nextElement()));
            edge.setTo(findNode(end));
            mEdges.add(edge);
        }
    }

    // Override update so it doesn't clear background and flicker
    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        g.clearRect(0, 0, getSize().width, getSize().height);
        g.setColor(Color.white);
        g.fillRect(0, 0, getSize().width - 2, getSize().height - 2);
        g.setColor(Color.black);
        g.drawRect(0, 0, getSize().width - 2, getSize().height - 2);
        // draw all nodes and edges
        for (Edge e : mEdges) {
            e.draw(g);
        }
        for (Node n : mNodes) {
            n.draw(g, n == mSelectedNode);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(300, 300);
    }
}

