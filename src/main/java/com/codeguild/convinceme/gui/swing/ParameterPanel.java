package com.codeguild.convinceme.gui.swing;

import com.codeguild.convinceme.model.ECHOSimulation;
import com.codeguild.convinceme.utils.Debug;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

/**
 * <p>Description: Window for adjusting parameter values</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 *
 * @author Patti Schank
 */
public class ParameterPanel extends JPanel {

    private JSlider mExcitationSlider, mInhibitionSlider,
            mDataExcitationSlider, mDecaySlider;
    private float mExcitation, mInhibition, mDataExcitation, mDecay;

    public ParameterPanel() {
        // set intial values
        mExcitation = ECHOSimulation.EXCITATION;
        mInhibition = ECHOSimulation.INHIBITION;
        mDataExcitation = ECHOSimulation.DATA_EXCITATION;
        mDecay = ECHOSimulation.THETA;

        // create the sliders
        mExcitationSlider = createSlider(mExcitation, "Excitation");
        mInhibitionSlider = createSlider(mInhibition, "Inhibition");
        mDecaySlider = createSlider(mDecay, "Skepticism");
        mDataExcitationSlider = createSlider(mDataExcitation, "Data Priority");

        // lay them out
        JPanel sliders = new JPanel(new GridLayout(4, 1, 5, 5));
        sliders.add(mExcitationSlider);
        sliders.add(mInhibitionSlider);
        sliders.add(mDataExcitationSlider);
        sliders.add(mDecaySlider);

        JButton useDefaults = new JButton("Use Defaults");
        useDefaults.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                useDefaults();
            }
        });

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttons.add(useDefaults);

        setLayout(new BorderLayout(5, 5));
        add(sliders, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        setSize(getPreferredSize());
    }

    public Dimension getPreferredSize() {
        return new Dimension(350, 400);
    }

    /**
     * Create a slider for parameter value that shows the given value
     * in the middle
     *
     * @param intiialValue Initial value for the slider
     * @return The new slider
     */
    public JSlider createSlider(float intiialValue, String title) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, intValue(intiialValue, -3),
                intValue(intiialValue, 3), intValue(intiialValue, 0));
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setBorder(new TitledBorder(title));
        return slider;
    }

    public void useDefaults() {
        mExcitationSlider.setValue(intValue(ECHOSimulation.EXCITATION, 0));
        mInhibitionSlider.setValue(intValue(ECHOSimulation.INHIBITION, 0));
        mDataExcitationSlider.setValue(intValue(ECHOSimulation.DATA_EXCITATION, 0));
        mDecaySlider.setValue(intValue(ECHOSimulation.THETA, 0));
    }

    /**
     * Show this pane in a dialog, and save values if not cancelled
     */
    public void showDialog() {
        loadValues();
        int response = JOptionPane.showConfirmDialog(null,
                this,
                "Simulation Parameters",
                JOptionPane.OK_CANCEL_OPTION);

        if (response == JOptionPane.CANCEL_OPTION) {
            Debug.println("User cancelled settings change.");
        } else {
            Debug.println("Saving new property settings.");
            saveValues();
        }
    }

    public int intValue(float p, int w) {
        return (Math.round((p + w / 100.0f) * 1000f));
    }

    public float getExcitation() {
        return mExcitation;
    }

    public float getInhibition() {
        return mInhibition;
    }

    public float getDataExcitation() {
        return mDataExcitation;
    }

    public float getDecay() {
        return mDecay;
    }

    /**
     * Load initial values to show in slider
     */
    private void loadValues() {
        mExcitationSlider.setValue(intValue(mExcitation, 0));
        mInhibitionSlider.setValue(intValue(mInhibition, 0));
        mDataExcitationSlider.setValue(intValue(mDataExcitation, 0));
        mDecaySlider.setValue(intValue(mDecay, 0));
    }

    /**
     * User clicked OK so save the values
     */
    private void saveValues() {
        mExcitation = getParameterValue(mExcitationSlider);
        mInhibition = getParameterValue(mInhibitionSlider);
        mDataExcitation = getParameterValue(mDataExcitationSlider);
        mDecay = getParameterValue(mDecaySlider);
    }

    /**
     * Convert slider value to parameter value
     *
     * @param slider The slider to convert
     * @return Parameter value as a float
     */
    private float getParameterValue(JSlider slider) {
        float f = 0.0f;
        try {
            f = slider.getValue() / 1000f;
        } catch (NumberFormatException e) {
            Debug.println("Parameter value is not right type");
        }
        return f;
    }

}