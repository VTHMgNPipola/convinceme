package com.codeguild.convinceme.model;

import com.codeguild.convinceme.utils.Debug;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>Description: A serializable proposition.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 *
 * @author Patti Schank
 */
public class Proposition {
    public static final int UNSPECIFIED = -99;
    public static final String NA = "na";
    public static final int HYPOTHESIS = 0;
    public static final int DATA = 1;

    private String mText = "";
    private String mLabel = "SEU"; // default, only SEU should get this
    private int mType = UNSPECIFIED;
    private int mRating = UNSPECIFIED;
    private int mReliability = UNSPECIFIED;
    private boolean mFact = false, mMemory = false,
            mOpinion = false, mDisagree = false;
    private int mX = UNSPECIFIED, mY = UNSPECIFIED; // location for graph

    private float mActivation = UNSPECIFIED, mNextActivation;
    // weight vector is transient, don't serialize it.
    // just used for the simulation
    private transient Vector<Weight> mWeightVector = new Vector<>();

    static Document document;

    // for XML
    public static final String HYPOTHESIS_TYPE = "HYPOTHESIS";
    public static final String DATA_TYPE = "DATA";
    public static final String TEXT = "TEXT";
    public static final String ID = "ID";
    public static final String RATING = "RATING";
    public static final String RELIABILITY = "RELIABILITY";
    public static final String X_VALUE = "X";
    public static final String Y_VALUE = "Y";
    public static final String FACT = "FACT";
    public static final String MEMORY = "MEMORY";
    public static final String OPINION = "OPINION";
    public static final String DISAGREE = "DISAGREE";


    public Proposition(String text, int type, Argument arg) {
        setText(text);
        setType(type, arg);
    }

    public Proposition() {
    }

    public void setLabel(String s) {
        mLabel = s;
    }

    public void setText(String s) {
        mText = s;
    }

    public void setX(int x) {
        mX = x;
    }

    public void setY(int y) {
        mY = y;
    }

    public void setFact(boolean b) {
        mFact = b;
    }

    public void setMemory(boolean b) {
        mMemory = b;
    }

    public void setOpinion(boolean b) {
        mOpinion = b;
    }

    public void setDisagree(boolean b) {
        mDisagree = b;
    }

    /**
     * Set the type of this proposition and update the label if
     * the type has changes
     *
     * @param type The type of this proposition
     * @param a    The argument to update
     */
    public void setType(int type, Argument a) {
        if (mType != type) {
            mType = type;
            // data type changed, need new label
            if (mType == DATA) {
                setLabel(a.getUniqueDataID());
            } else {
                setLabel(a.getUniqueHypID());
            }
        }
    }

    /**
     * Set the type of this proposition
     * Set the type of this proposition
     *
     * @param type The type of this proposition
     */
    public void setType(int type) {
        mType = type;
    }

    public void setRating(int r) {
        mRating = r;
    }

    public void setReliability(int r) {
        mReliability = r;
    }

    public void setActivation(float f) {
        mActivation = f;
    }

    public void setNextActivation(float f) {
        mNextActivation = f;
    }

    public void initWeights() {
        mWeightVector = new Vector<>();
    }

    public void addWeight(Proposition p, float w) {
        mWeightVector.addElement(new Weight(p, w));
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    public boolean isMemory() {
        return mMemory;
    }

    public boolean isFact() {
        return mFact;
    }

    public boolean isOpinion() {
        return mOpinion;
    }

    public boolean isDisagreeable() {
        return mDisagree;
    }

    public String getPropText() {
        return mText;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getText() {
        return mLabel + " " + mText;
    }

    public boolean isData() {
        return (mType == DATA);
    }

    public boolean isHypothesis() {
        return (mType == HYPOTHESIS);
    }

    public boolean isPlotted() {
        return (mX != UNSPECIFIED);
    }

    public int getRating() {
        return mRating;
    }

    public boolean hasRating() {
        return (mRating != UNSPECIFIED);
    }

    public String getRatingText() {
        if (!hasRating()) {
            return NA;
        }
        return String.valueOf(mRating);
    }

    public int getReliability() {
        return mReliability;
    }

    public boolean hasReliability() {
        return (mReliability != UNSPECIFIED);
    }

    public String getReliabilityText() {
        if (!hasReliability()) {
            return NA;
        }
        return String.valueOf(mReliability);
    }

    public float getActivation() {
        return mActivation;
    }

    public float getNextActivation() {
        return mNextActivation;
    }

    public Vector<Weight> getWeights() {
        return mWeightVector;
    }

    public String getActivationText() {
        if (mActivation == UNSPECIFIED) {
            return NA;
        }
        return getLinearTranslationText(mActivation);
    }

    /**
     * Normalize the activation value of this proposition to 0,
     * expand range, shift ot beginning of rating scale (1)
     */
    public String getLinearTranslationText(float f) {
        return String.valueOf(Math.round((((f + 1) * 4) + 1) * 10.0) / 10.0);
    }

    public boolean isValid(float f) {
        return (f != UNSPECIFIED);
    }

    /**
     * @return XML for this proposition
     */
    public Element getXML() {
        String type = HYPOTHESIS_TYPE;
        if (isData()) {
            type = DATA_TYPE;
        }

        Element root = document.createElement(type);

        root.setAttribute(ID, getLabel());
        root.setAttribute(TEXT, getPropText());
        root.setAttribute(RATING, getRatingText());
        root.setAttribute(X_VALUE, String.valueOf(getX()));
        root.setAttribute(Y_VALUE, String.valueOf(getY()));
        root.setAttribute(RATING, getRatingText());

        if (isFact()) {
            root.setAttribute(FACT, Argument.TRUE);
        }
        if (isOpinion()) {
            root.setAttribute(OPINION, Argument.TRUE);
        }
        if (isMemory()) {
            root.setAttribute(MEMORY, Argument.TRUE);
        }
        if (isDisagreeable()) {
            root.setAttribute(DISAGREE, Argument.TRUE);
        }

        if (isData()) {
            root.setAttribute(RELIABILITY, this.getReliabilityText());
        }

        return root;
    }

    /**
     * @return a proposition from XML
     */
    public static Proposition readXML(Element root) {
        Proposition prop = new Proposition();

        boolean isHyp = true;
        if (root.getTagName().equalsIgnoreCase(DATA_TYPE)) {
            isHyp = false;
        }
        if (isHyp) {
            prop.setType(HYPOTHESIS);
        } else {
            prop.setType(DATA);
        }
        prop.setLabel(root.getAttribute(ID));
        prop.setText(root.getAttribute(TEXT));

        try {
            prop.setRating(Integer.parseInt(root.getAttribute(RATING)));
        } catch (Exception e) {
            Debug.println("Can't parse rating from XML");
        }

        if (!isHyp) {
            try {
                prop.setReliability((Integer.parseInt(root.getAttribute(RELIABILITY))));
            } catch (Exception e) {
                Debug.println("Can't parse reliability from XML");
            }
        }

        try {
            prop.setX(Integer.parseInt(root.getAttribute(X_VALUE)));
            prop.setY(Integer.parseInt(root.getAttribute(Y_VALUE)));
        } catch (Exception e) {
            Debug.println("Can't parse x and/or y from XML");
        }

        if (root.getAttribute(FACT) != null) {
            prop.setFact(true);
        }
        if (root.getAttribute(OPINION) != null) {
            prop.setOpinion(true);
        }
        if (root.getAttribute(FACT) != null) {
            prop.setFact(true);
        }
        if (root.getAttribute(MEMORY) != null) {
            prop.setMemory(true);
        }
        if (root.getAttribute(DISAGREE) != null) {
            prop.setDisagree(true);
        }
        return prop;
    }

}

