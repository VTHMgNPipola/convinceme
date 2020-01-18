package com.codeguild.convinceme.model;

import com.codeguild.convinceme.utils.Debug;
import java.util.Enumeration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>Description: Serializable argument. Made all data members
 * public instead of using getters and setters since serialization
 * writes methods as well, and don't want the overhead </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Codeguild</p>
 *
 * @author Patti Schank
 */
public class Argument {
    public PropositionVector mHypotheses, mData;
    public LinkVector mExplanations, mContradictions;
    public int mHypID;
    public int mDataID;
    public String mNotes;

    // for XML
    public static final String ARGUMENT = "ARGUMENT";
    public static final String NOTES = "NOTES";
    public static final String TRUE = "TRUE";

    private static Document document;

    public Argument() {
        mHypotheses = new PropositionVector();
        mData = new PropositionVector();
        mExplanations = new LinkVector();
        mContradictions = new LinkVector();
        mHypID = 0;
        mDataID = 0;
        mNotes = "";
    }

    /**
     * @return a proposition given a the proposition label/id
     */
    public Proposition getProposition(String label) {
        Proposition result = null;
        boolean found = false;

        Enumeration<Proposition> e = mHypotheses.elements();
        while (e.hasMoreElements() && !found) {
            result = e.nextElement();
            if (label.equalsIgnoreCase(result.getLabel())) {
                found = true;
            }
        }
        e = mData.elements();
        while (e.hasMoreElements() && !found) {
            result = e.nextElement();
            if (label.equalsIgnoreCase(result.getLabel())) {
                found = true;
            }
        }
        return result;
    }

    /**
     * @return All hypotheses in the current argument
     */
    public PropositionVector getHypotheses() {
        return mHypotheses;
    }

    /**
     * @return All data in the current argument
     */
    public PropositionVector getData() {
        return mData;
    }

    /**
     * @return a unique ID for a new hypothesis
     */
    public String getUniqueHypID() {
        int max = 0;
        Enumeration<Proposition> e = mHypotheses.elements();
        while (e.hasMoreElements()) {
            Proposition p = e.nextElement();
            String digit = p.getLabel().substring(1);
            try {
                max = Math.max(max, Integer.parseInt(digit));
            } catch (Exception ex) {
                max = max + 1;
            }
        }
        int next = max + 1;
        return "H" + next;
    }

    /**
     * @return a unique ID for a new hypothesis
     */
    public String getUniqueDataID() {
        int max = 0;
        Enumeration<Proposition> e = mData.elements();
        while (e.hasMoreElements()) {
            Proposition p = e.nextElement();
            String digit = p.getLabel().substring(1);
            try {
                max = Math.max(max, Integer.parseInt(digit));
            } catch (Exception ex) {
                max = max + 1;
            }
        }
        int next = max + 1;
        return "E" + next;
    }

    /**
     * @return All explanations in the current argument
     */
    public LinkVector getExps() {
        return mExplanations;
    }

    /**
     * @return All contradictions in the current argument
     */
    public LinkVector getConts() {
        return mContradictions;
    }

    /**
     * Add a new hypothesis to the argument
     *
     * @param hyp Proposition to add
     */
    public void addHypothesis(Proposition hyp) {
        if (!mHypotheses.contains(hyp)) {
            mHypotheses.addElement(hyp);
        }
        // make sure it's not in data if it was reclassified
        deleteData(hyp);
    }

    /**
     * Add a new data to the argument
     *
     * @param data Proposition to add
     */
    public void addData(Proposition data) {
        if (!mData.contains(data)) {
            mData.addElement(data);
        }
        // make sure it's not in hypotheses if it was reclassified
        deleteHypothesis(data);
    }

    /**
     * Delete a hypothesis from the argument
     *
     * @param hyp Proposition to delete
     */
    public void deleteHypothesis(Proposition hyp) {
        if (mHypotheses.contains(hyp)) {
            mHypotheses.removeElement(hyp);
        }
    }

    /**
     * Delete hypotheses from the argument
     *
     * @param indexes indices of Proposition to delete
     */
    public void deleteHypotheses(int[] indexes) {
        for (int index : indexes) {
            try {
                mHypotheses.removeElementAt(index);
            } catch (Exception e) {
                Debug.printStackTrace(e);
            }
        }
    }

    /**
     * Delete data from the argument
     *
     * @param data Proposition to delete
     */
    public void deleteData(Proposition data) {
        if (mData.contains(data)) {
            mData.removeElement(data);
        }
    }

    /**
     * Delete data from the argument
     *
     * @param indexes indices of Proposition to delete
     */
    public void deleteData(int[] indexes) {
        for (int index : indexes) {
            try {
                mData.removeElementAt(index);
            } catch (Exception e) {
                Debug.printStackTrace(e);
            }
        }
    }

    /**
     * Add an explanation to the current argument
     *
     * @param exp Explanation link to add
     */
    public void addExplanation(Link exp) {
        mExplanations.addElement(exp);
    }

    /**
     * Add a contradiction to the current argument
     *
     * @param cont Explanation link to add
     */
    public void addContradiction(Link cont) {
        mContradictions.addElement(cont);
    }

    /**
     * Delete explanations from the argument
     *
     * @param indexes indices of Proposition to delete
     */
    public void deleteExplanations(int[] indexes) {
        for (int index : indexes) {
            try {
                mExplanations.removeElementAt(index);
            } catch (Exception e) {
                Debug.printStackTrace(e);
            }
        }
    }

    /**
     * Delete a contradiction from the current argument
     *
     * @param cont Explanation link to add
     */
    public void deleteContradiction(Link cont) {
        if (mContradictions.contains(cont)) {
            mContradictions.removeElement(cont);
        }
    }

    /**
     * Delete contradictions from the argument
     *
     * @param indexes indices of Proposition to delete
     */
    public void deleteContradictions(int[] indexes) {
        for (int index : indexes) {
            try {
                mContradictions.removeElementAt(index);
            } catch (Exception e) {
                Debug.printStackTrace(e);
            }
        }
    }

    /**
     * @return XML for this argument
     */
    public Element getXML() {
        Element root = document.createElement(ARGUMENT);
        Proposition.document = document;
        Link.document = document;
        Enumeration<Proposition> propositionEnumeration = mHypotheses.elements();
        while (propositionEnumeration.hasMoreElements()) {
            Proposition p = propositionEnumeration.nextElement();
            root.appendChild(p.getXML());
        }
        propositionEnumeration = mData.elements();
        while (propositionEnumeration.hasMoreElements()) {
            Proposition p = propositionEnumeration.nextElement();
            root.appendChild(p.getXML());
        }
        Enumeration<Link> linkEnumeration = mExplanations.elements();
        while (linkEnumeration.hasMoreElements()) {
            Link l = linkEnumeration.nextElement();
            root.appendChild(l.getXML());
        }
        linkEnumeration = mContradictions.elements();
        while (linkEnumeration.hasMoreElements()) {
            Link l = linkEnumeration.nextElement();
            root.appendChild(l.getXML());
        }
        return root;
    }

    /**
     * @return an argument from XML
     */
    public static Argument readXML(Element root) {
        // TODO: Optimize

        Argument argument = new Argument();

        // add hypotheses
        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (!element.getTagName().equals(Proposition.HYPOTHESIS_TYPE)) continue;
                Proposition prop = Proposition.readXML(element);
                argument.addHypothesis(prop);
            }
        }

        // add data
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (!element.getTagName().equals(Proposition.DATA_TYPE)) continue;
                Proposition prop = Proposition.readXML(element);
                argument.addData(prop);
            }
        }

        // add explanations
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (!element.getTagName().equals(Link.EXPLANATION_TYPE)) continue;
                Link link = Link.readXML(element, argument);
                argument.addExplanation(link);
            }
        }

        // add contradictions
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if (!element.getTagName().equals(Link.CONTRADICTION_TYPE)) continue;
                Link link = Link.readXML(element, argument);
                argument.addContradiction(link);
            }
        }

        return argument;
    }

    public static void setDocument(Document document) {
        Argument.document = document;
    }
}
