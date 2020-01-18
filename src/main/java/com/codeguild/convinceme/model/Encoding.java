package com.codeguild.convinceme.model;

import java.util.Enumeration;

/**
 * <p>Description: Model for encoding text </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 *
 * @author Patti Schank
 */
public class Encoding {

    public static final String EXPLAIN_TEXT = "explains ";
    public static final String CONTRADICT_TEXT = "contradicts ";
    public static final String DATA_TEXT = "data ";
    public static final String PROP_TEXT = "proposition ";

    protected String mEncodingString;

    public Encoding() {
    }

    public Encoding(Argument argument) {
        setArgument(argument);
    }

    /**
     * Get the encoding text for this Encoding
     *
     * @return The encoding text
     */
    public String getText() {
        return mEncodingString;
    }

    /**
     * Set the argument for this encoding
     *
     * @param argument The argument
     */
    public void setArgument(Argument argument) {
        mEncodingString = getEncoding(argument);
    }

    /**
     * Get the encoding for the given argument
     *
     * @param argument The argument
     * @return The encoding as a String
     */
    public String getEncoding(Argument argument) {
        StringBuffer encoding = new StringBuffer();
        encoding.append(getPropEncodingText(argument.mHypotheses));
        encoding.append(getPropEncodingText(argument.mData));
        encoding.append(getDataEncodingText(argument.mData));
        encoding.append(getLinkEncodingText(argument.mExplanations));
        encoding.append(getLinkEncodingText(argument.mContradictions));
        return encoding.toString();
    }


    /**
     * Given a proposition vector, return the encoding text
     *
     * @param pv The proposition vector
     * @return The encoding as a String
     */
    public String getPropEncodingText(PropositionVector pv) {
        StringBuffer encoding = new StringBuffer();
        Proposition prop;
        for (Enumeration e = pv.elements(); e.hasMoreElements(); ) {
            prop = (Proposition) e.nextElement();
            encoding.append(PROP_TEXT);
            encoding.append(prop.getText());
            encoding.append("\n");
        }
        return encoding.toString();
    }

    /**
     * Given a proposition vector, return the encoding text
     *
     * @param pv The proposition vector
     * @return The encoding as a String
     */
    public String getDataEncodingText(PropositionVector pv) {
        StringBuffer encoding = new StringBuffer();
        encoding.append(DATA_TEXT);
        boolean foundData = false;
        Proposition prop;
        for (Enumeration e = pv.elements(); e.hasMoreElements(); ) {
            prop = (Proposition) e.nextElement();
            if (prop.isData()) {
                foundData = true;
                encoding.append(" ");
                encoding.append(prop.getLabel());
            }
        }
        encoding.append("\n");
        if (!foundData) {
            return "";
        }
        return encoding.toString();
    }

    /**
     * Given a link vector, return the encoding text
     *
     * @param lv The link vector
     * @return The encoding as a String
     */
    public String getLinkEncodingText(LinkVector lv) {
        StringBuffer encoding = new StringBuffer();
        Link link;
        for (Enumeration e = lv.elements(); e.hasMoreElements(); ) {
            link = (Link) e.nextElement();
            if (link.isExplanation()) {
                encoding.append(EXPLAIN_TEXT);
            } else {
                encoding.append(CONTRADICT_TEXT);
            }
            encoding.append(link.getProps().getLabelsText());
            encoding.append("\n");
        }
        return encoding.toString();
    }
}
