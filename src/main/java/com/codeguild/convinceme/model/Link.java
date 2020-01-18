package com.codeguild.convinceme.model;

import java.util.Enumeration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>Description: A serializable link .</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 *
 * @author Patti Schank
 */
public class Link {
    public static final int EXPLAIN = 0;
    public static final int CONTRADICT = 1;

    public static final String EXPLAIN_TEXT = "explains";
    public static final String CONTRADICT_TEXT = "contradict";

    private PropositionVector mProps = new PropositionVector();
    private int mType = EXPLAIN;

    // for XML
    public static final String EXPLANATION_TYPE = "EXPLAINS";
    public static final String CONTRADICTION_TYPE = "CONTRADICTS";
    public static final String EXPLAINED = "EXPLAINED";
    public static final String EXPLAINERS = "EXPLAINERS";

    static Document document;

    public Link(PropositionVector props) {
        mProps = props;
    }

    public Link(int linkType) {
        mType = linkType;
    }

    public Link(PropositionVector props, int linkType) {
        super();
        mType = linkType;
        mProps = props;
    }

    /**
     * Set the type of Link this is (Contradition or Explanation)
     *
     * @param type The type
     */
    public void setType(int type) {
        mType = type;
    }

    /**
     * Set the propositions for this link
     *
     * @param pv The proposition vector
     */
    public void setProps(PropositionVector pv) {
        mProps = pv;
    }

    /**
     * Get the proposition at position i of this Link
     *
     * @param pos The position
     * @return A proposition
     */
    public Proposition getPropAt(int pos) {
        return mProps.getPropAt(pos);
    }

    /**
     * @return The propositions in this Link
     */
    public PropositionVector getProps() {
        return mProps;
    }

    /**
     * Set the weights of this link. If there are multiple propositions
     * in the link, it establishes links between pairs
     *
     * @param w         The weight to set
     * @param divWeight if true, divide the weight between
     */
    public void setWeights(float w, boolean divWeight) {
        int n = mProps.size();
        Proposition last = mProps.lastElement();

        // divide weight among links?
        if (divWeight) {
            w = w / (n - 1);
        }
        for (int i = 0; i < mProps.size() - 1; i++) {
            // add symmetric weights
            getPropAt(i).addWeight(last, w);
            last.addWeight(getPropAt(i), w);

            // if there are multiple propositions in the link,
            // establish links between pairs
            for (int j = i + 1; j < mProps.size() - 1; j++) {
                getPropAt(i).addWeight(getPropAt(j), w);
                getPropAt(j).addWeight(getPropAt(i), w);
            }
        }
    }

    /**
     * Get the sources of this link. E.g., if
     * P1 P2 explains P3 then it would return P1 and P2
     *
     * @return A vector of explaining propositions
     */
    public PropositionVector getExplainers() {
        PropositionVector prop = new PropositionVector();
        int i = 0;
        for (Enumeration<Proposition> e = mProps.elements();
             e.hasMoreElements() && (i < mProps.size() - 1); i++) {
            prop.addElement(e.nextElement());
        }
        return prop;
    }

    /**
     * Get the target of this link, e.g., if the link is
     * P1 explains P2 then it would return P2
     *
     * @return The last proposi
     */
    public Proposition getExplained() {
        return mProps.lastElement();
    }

    /**
     * Get the text encoding of this Link, e.g.
     * P1 explains P2
     *
     * @return The text encoding
     */
    public String getText() {
        StringBuffer text = new StringBuffer();
        int i = 0;
        for (Enumeration e = mProps.elements();
             e.hasMoreElements() && (i < mProps.size() - 1); i++) {
            text.append(((Proposition) e.nextElement()).getLabel());
            text.append(" ");
        }
        if (isExplanation()) {
            text.append(EXPLAIN_TEXT);
        } else {
            text.append(CONTRADICT_TEXT);
        }
        text.append(" ");
        text.append(mProps.lastElement().getLabel());
        return text.toString();
    }

    /**
     * @return true if this link is a contradiction link
     */
    public boolean isContradiction() {
        return (mType == CONTRADICT);
    }

    /**
     * @return true if this link is a explanation link
     */
    public boolean isExplanation() {
        return (mType == EXPLAIN);
    }

    /**
     * @return true if this link is a join explanation link
     */
    public boolean isJointExplanation() {
        return (isExplanation() && (mProps.size() > 2));
    }

    /**
     * @return XML for this proposition
     */
    public Element getXML() {
        String type = EXPLANATION_TYPE;
        if (isContradiction()) {
            type = CONTRADICTION_TYPE;
        }

        Element root = document.createElement(type);
        if (isContradiction()) {
            Proposition start = mProps.firstElement();
            type = Proposition.HYPOTHESIS_TYPE;
            if (start.isData()) {
                type = Proposition.DATA_TYPE;
            }
            Element cont = (Element) root.appendChild(document.createElement(type));
            cont.setAttribute(Proposition.ID, start.getLabel());

            Proposition end = mProps.lastElement();
            type = Proposition.HYPOTHESIS_TYPE;
            if (end.isData()) {
                type = Proposition.DATA_TYPE;
            }
            cont = (Element) root.appendChild(document.createElement(type));
            cont.setAttribute(Proposition.ID, end.getLabel());
        } else {
            Proposition explained = getExplained();
            type = Proposition.HYPOTHESIS_TYPE;
            if (explained.isData()) {
                type = Proposition.DATA_TYPE;
            }
            Element explainedRoot = (Element) root.appendChild(document.createElement(type));
            explainedRoot.setAttribute(Proposition.ID, explained.getLabel());
            explainedRoot.setAttribute(EXPLAINED, Argument.TRUE);

            PropositionVector explainers = getExplainers();
            Enumeration<Proposition> e = explainers.elements();
            while (e.hasMoreElements()) {
                Proposition p = e.nextElement();
                type = Proposition.HYPOTHESIS_TYPE;
                if (p.isData()) {
                    type = Proposition.DATA_TYPE;
                }
                Element explainerCollection = (Element) root.appendChild(document.createElement(type));
                explainerCollection.setAttribute(Proposition.ID, p.getLabel());
            }
        }

        return root;
    }

    /**
     * @return A proposition from XML
     */
    public static Link readXML(Element root, Argument arg) {
        Link link;
        PropositionVector propVector = new PropositionVector();
        boolean isExp = true;
        if (root.getTagName().equalsIgnoreCase(CONTRADICTION_TYPE)) {
            isExp = false;
        }
        if (isExp) {
            link = new Link(EXPLAIN);
            Proposition explained = null;
            NodeList props = root.getChildNodes();
            for (int i = 0; i < props.getLength(); i++) {
                Node node = props.item(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    String label = element.getAttribute(Proposition.ID);
                    Proposition prop = arg.getProposition(label);
                    if (!element.getAttribute(EXPLAINED).isEmpty()) {
                        explained = prop;
                    } else {
                        propVector.addElement(prop);
                    }
                }
            }
            // add explained last
            propVector.addElement(explained);
        } else {
            // contradictions
            link = new Link(CONTRADICT);
            NodeList props = root.getChildNodes();
            for (int i = 0; i < props.getLength(); i++) {
                Node node = props.item(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    String label = element.getAttribute(Proposition.ID);
                    Proposition prop = arg.getProposition(label);
                    propVector.addElement(prop);
                }
            }
        }

        link.setProps(propVector);
        return link;
    }
}

