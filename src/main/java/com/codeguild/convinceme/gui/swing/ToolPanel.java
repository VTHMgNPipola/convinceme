package com.codeguild.convinceme.gui.swing;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * <p>Description: A tool palette of hypotheses, data, and links.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 *
 * @author Patti Schank
 */
public class ToolPanel extends JPanel {
    private MainFrame mController;

    public ToolPanel(MainFrame controller) {
        mController = controller;

        // create the tools
        JButton hypButton = new JButton("Claim");
        JButton dataButton = new JButton("Evidence");
        JButton expButton = new JButton("Explanation");
        JButton contButton = new JButton("Contradiction");

        hypButton.addActionListener(e -> mController.addHypDialog());

        dataButton.addActionListener(e -> mController.addDataDialog());

        expButton.addActionListener(e -> mController.addExpDialog());

        contButton.addActionListener(e -> mController.addContDialog());

        setLayout(new FlowLayout());
        add(hypButton);
        add(dataButton);
        add(expButton);
        add(contButton);
    }
}