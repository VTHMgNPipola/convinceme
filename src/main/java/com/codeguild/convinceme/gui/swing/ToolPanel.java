package com.codeguild.convinceme.gui.swing;

import com.codeguild.convinceme.utils.ImageUtils;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 * <p>Description: A tool palette of hypotheses, data, and links.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 *
 * @author Patti Schank
 */

public class ToolPanel extends JPanel {

    final static String IMAGEPATH = "/images/";

    private MainFrame mController;

    public ToolPanel(MainFrame controller) {
        mController = controller;

        // create the tools
        JButton hypButton = getButton("hyp.gif", "Add Hypothesis");
        JButton dataButton = getButton("data.gif", "Add Evidence");
        JButton expButton = getButton("exp.gif", "Add Explanation");
        JButton contButton = getButton("cont.gif", "Add Contradiction");

        hypButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mController.addHypDialog();
            }
        });

        dataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mController.addDataDialog();
            }
        });

        expButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mController.addExpDialog();
            }
        });

        contButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mController.addContDialog();
            }
        });

        setLayout(new FlowLayout());
        add(new JLabel("Add..."));
        add(hypButton);
        add(dataButton);
        add(expButton);
        add(contButton);
    }

    private JButton getButton(String iconPath, String toolTip) {
        JButton button = new JButton();
        if (iconPath != null) {
            Icon icon = ImageUtils.getIconFromResource(IMAGEPATH + iconPath);
            button.setIcon(icon);
        }
        if (toolTip != null) {
            button.setToolTipText(toolTip);
        }
        button.setBorder(new EtchedBorder());
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setHorizontalAlignment(JButton.CENTER);
        button.setVerticalAlignment(JButton.CENTER);
        button.setEnabled(true);
        return button;
    }
}