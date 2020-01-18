package com.codeguild.convinceme.model;

import java.util.Enumeration;
import java.util.Vector;

/**
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 *
 * @author Patti Schank
 */
public class ECHOSimulation {

    public static final float VALERR = -999.0f; // error code from getval()

    public static final float DATA_EXCITATION = 0.055f;    // echo defaults
    public static final float EXCITATION = 0.030f;
    public static final float INHIBITION = 0.060f;
    public static final float THETA = 0.040f;
    public static final float START_VALUE = 0.010f;
    public static final float STOP_VALUE = 0.001f;
    public static final int MAX_ITERATIONS = 200;
    public static final float ANALOGY_IMPACT = 1.000f;
    public static final float MAXACT = 1.00f;
    public static final float MINACT = -1.00f;

    public static final int MAX_BELIEVABILITY = 9;
    public static final int MAX_RELIABILITY = 3;

    private Proposition mSeu = new Proposition();
    private int mIter = 0;

    private Encoding mEncoding;
    private StringBuffer mLog;

    private float mDataExcitation = DATA_EXCITATION;
    private float mExcitation = EXCITATION;
    private float mInhibition = INHIBITION;
    private float mDecay = THETA;

    private Argument mArgument;

    public ECHOSimulation(float excitation, float inhibition, float dataexcit, float decay,
                          Argument argument) {
        mExcitation = excitation;
        mInhibition = inhibition;
        mDataExcitation = dataexcit;
        mDecay = decay;
        mArgument = argument;
    }

    /**
     * Convenience method if you want to use the default parameter values
     */
    public ECHOSimulation(Argument argument) {
        this(EXCITATION, INHIBITION, DATA_EXCITATION, THETA, argument);
    }

    public Encoding getEncoding() {
        return mEncoding;
    }

    public PropositionVector getHyps() {
        return mArgument.mHypotheses;
    }

    public PropositionVector getData() {
        return mArgument.mData;
    }

    public LinkVector getExplanations() {
        return mArgument.mExplanations;
    }

    public LinkVector getContradictions() {
        return mArgument.mContradictions;
    }

    public String getLog() {
        return mLog.toString();
    }

    /**
     * Run the echo simulation with default parameter settings
     *
     * @return String result that describes correlation (for more information, getLog())
     */
    public String runECHO() {
        return runECHO(mExcitation, mInhibition, mDataExcitation, mDecay);
    }

    /**
     * Run echo simulation given parameters
     *
     * @param excitation
     * @param inhibition
     * @param dataexcit
     * @param decay
     * @return String result that describes correlation (for more information, getLog())
     */
    public String runECHO(float excitation, float inhibition, float dataexcit, float decay) {
        mExcitation = excitation;
        mInhibition = inhibition;
        mDataExcitation = dataexcit;
        mDecay = decay;

        mEncoding = new Encoding(mArgument);

        mLog = new StringBuffer();
        mLog.append("Running simulation with parameters:\n excitation = ");
        mLog.append(excitation);
        mLog.append(", inhibition = ");
        mLog.append(inhibition);
        mLog.append(", data excitation = ");
        mLog.append(dataexcit);
        mLog.append(", decay = ");
        mLog.append(decay);
        mLog.append("\n");
        mLog.append(mEncoding.getText());

        // initialize proposition activations
        PropositionVector props = getHyps().concatenate(getData());
        props.setActivations(START_VALUE);
        props.initWeights();

        // create SEU links to data
        mSeu.setActivation(1);
        LinkVector seuLinks = new LinkVector();
        Proposition dataProp;
        PropositionVector p;
        for (Enumeration j = getData().elements(); j.hasMoreElements(); ) {
            dataProp = (Proposition) j.nextElement();
            p = new PropositionVector(dataProp);
            p.addElement(mSeu);
            Link seuLink = new Link(p, Link.EXPLAIN);
            // seu Links are special; set weights separately here, dividing
            // data excitation by the reliability of the evidence
            float relFactor = (float) dataProp.getReliability() / (float) MAX_RELIABILITY;
            seuLink.setWeights(mDataExcitation * relFactor, false);
            seuLinks.addElement(seuLink);
        }

        // set weights, pass parameter values, and flag whether to
        // divide parameter weigh among links or not. note that seu links
        // have already been set, weighted by reliability, above
        LinkVector explanationLinks = getExplanations();
        LinkVector contradictionLinks = getContradictions();
        explanationLinks.setWeights(mExcitation, true);  // divide excitation if joint explanation
        contradictionLinks.setWeights(-mInhibition, true);  // divide inhibition if necessary

        float change = 99, net = 0, temp, nextact, thisact, thischange;
        Proposition propj;

        Enumeration ej, ei;
        Vector wv;
        Weight w;

        mLog.append("Simulation weights:");
        // report link weights
        for (ej = props.elements(); ej.hasMoreElements(); ) {
            propj = (Proposition) ej.nextElement();
            wv = propj.getWeights();
            for (ei = wv.elements(); ei.hasMoreElements(); ) {
                w = (Weight) ei.nextElement();
//				mLog.append(propj.getLabel() + " to " + w.getProposition().getLabel() + ": " + w.getWeight());
            }
        }

        // run simulation and set activations
        for (mIter = 1; (mIter < MAX_ITERATIONS) && (change > STOP_VALUE); mIter++) {
            change = 0.0f;
//			Debug.println("Iteration: " + mIter);
            for (ej = props.elements(); ej.hasMoreElements(); ) {
                propj = (Proposition) ej.nextElement();
                wv = propj.getWeights();
                net = 0;
                for (ei = wv.elements(); ei.hasMoreElements(); ) {
                    w = (Weight) ei.nextElement();
                    net = net + (w.getWeight() * (w.getProposition().getActivation()));
                }
                thisact = propj.getActivation();
                if (net > 0) {
                    temp = net * (MAXACT - thisact);
                } else {
                    temp = net * (thisact - MINACT);
                }
                nextact = (thisact * (1.0f - mDecay)) + temp;
//				Debug.println(propj.getLabel() + " " + thisact + " net: " + net + " temp: " + temp);
                propj.setNextActivation(nextact);
                change = (Math.max(Math.abs(thisact - nextact), change));
            }
            for (ej = props.elements(); ej.hasMoreElements(); ) {
                propj = (Proposition) ej.nextElement();
                propj.setActivation(propj.getNextActivation());
            }
        }

        props.initWeights(); // delete weights, not needed now

        // log simulation results
        mLog.append("Simulation finished. Iterations = ");
        mLog.append(mIter);
        mLog.append(" (max iterations = ");
        mLog.append(MAX_ITERATIONS);
        mLog.append(")");

        String s = "";
        for (Enumeration j = props.elements(); j.hasMoreElements(); ) {
            propj = (Proposition) j.nextElement();
            s = s + propj.getLabel() + "      " + propj.getActivationText() + "           " +
                    propj.getRatingText() + "\n";
        }

        mLog.append("      Activation    Rating\n");
        mLog.append(s);

        String correlationText = getCorrelationText(props);
        mLog.append("Correlation between ratings and activations: ");
        mLog.append(getCorrelationText(props));
        return correlationText;
    }

    /**
     * Get the text description of the correlation between simulation
     * results and belief ratings
     *
     * @param pv the proposition vector
     * @return The text description of the correlation
     */
    public String getCorrelationText(PropositionVector pv) {
        String result;
        double corr = getCorrelation(pv);
        if ((corr < -1) || (corr > 1)) {
            result = "Not enough ratings, or no variation in ratings.";
        } else {
            result = String.valueOf(Math.round(corr * 100.0) / 100.0);
        }
        return result;
    }

    /**
     * Get the correlation between simulation results and belief ratings
     *
     * @param pv the proposition vector
     * @return The correlation
     */
    public double getCorrelation(PropositionVector pv) {
        double sumx1 = 0, sumx2 = 0, sumy1 = 0, sumy2 = 0, sumxy = 0,
                total = 0, r, a, numerator, d1, d2, corr;
        Proposition p;
        for (Enumeration e = pv.elements(); e.hasMoreElements(); ) {
            p = (Proposition) e.nextElement();
            r = p.getRating();
            a = p.getActivation();
            if (p.isValid((float) r) && p.isValid((float) a)) {
                sumx1 += r;
                sumy1 += a;
                sumx2 += r * r;
                sumy2 += a * a;
                sumxy += r * a;
                total += 1;
            }
        }
        numerator = (total * sumxy) - (sumx1 * sumy1);
        d1 = Math.sqrt((total * sumx2) - (sumx1 * sumx1));
        d2 = Math.sqrt((total * sumy2) - (sumy1 * sumy1));
        corr = numerator / (d1 * d2);
        return corr;
    }
}
