package com.codeguild.convinceme.gui.swing;

import com.codeguild.convinceme.gui.Configuration;
import com.codeguild.convinceme.model.Argument;
import com.codeguild.convinceme.model.Encoding;
import com.codeguild.convinceme.model.Link;
import com.codeguild.convinceme.model.Proposition;
import com.codeguild.convinceme.model.PropositionVector;
import com.codeguild.convinceme.utils.Debug;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>Description: Create Convince Me menus and windows.
 * ECHO simulation run as a method on this class</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 *
 * @author Patti Schank
 */
public class MainFrame extends Configuration {

    public static void main(String[] args) {
        new MainFrame();
    }

    protected ListPanel mTextListPanel;
    protected DiagramPanel mDiagramPanel;
    protected TextPanel mNotePanel, mEncodingPanel, mLogPanel;
    protected ParameterPanel mParameterPanel;
    protected JFrame mAppWindow;

    protected File mCurrentDir;

    public MainFrame() {
        mTextListPanel = new ListPanel(this);
        mDiagramPanel = new DiagramPanel(this);
        mLogPanel = new TextPanel("Log");
        mLogPanel.setUseBuffer(true);
        mNotePanel = new TextPanel("Notes");
        mNotePanel.setEditable(true);
        mEncodingPanel = new TextPanel("ECHO Encoding");
        mParameterPanel = new ParameterPanel();

        // create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Diagram", mDiagramPanel);
        tabbedPane.setSelectedIndex(0);
        tabbedPane.addTab("Text", mTextListPanel);
        tabbedPane.addTab("Notes", mNotePanel);
        tabbedPane.addTab("Log", mLogPanel);
        tabbedPane.addTab("Encoding", mEncodingPanel);
        tabbedPane.setBackground(Configuration.BACKGROUND);

        // Create application window
        mAppWindow = new JFrame("Convince Me");
        mAppWindow.setJMenuBar(getMenuBar());
        mAppWindow.getContentPane().setBackground(Configuration.BACKGROUND);
        mAppWindow.getContentPane().setLayout(new BorderLayout(10, 10));
        mAppWindow.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        mAppWindow.setSize(getPreferredSize());

        mAppWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Utils.centerFrameOnScreen(mAppWindow);
        mAppWindow.setVisible(true);
    }

    public Dimension getPreferredSize() {
        return new Dimension(500, 500);
    }

    public void log(String s) {
        mLogPanel.appendText(s);
    }

    public void newCM() {
        new MainFrame();
    }

    public void dispose() {
        mAppWindow.dispose();
    }

    /**
     * Save the current argument to a file
     */
    public void save() {
        JFileChooser fileChooser = new JFileChooser();
        if (mCurrentDir != null) {
            fileChooser.setCurrentDirectory(mCurrentDir);
        }
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        FileFilter xmlFilter = new FileNameExtensionFilter("XML File", "xml");
        fileChooser.addChoosableFileFilter(xmlFilter);
        fileChooser.setFileFilter(xmlFilter);
        int choice = fileChooser.showDialog(mAppWindow, "Save Argument");
        if (choice == JFileChooser.APPROVE_OPTION) {
            try {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filename.endsWith(".xml") && fileChooser.getFileFilter() == xmlFilter) {
                    filename += ".xml";
                }
                mCurrentDir = fileChooser.getCurrentDirectory();
                // create necessary output streams to save Argument
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                Argument.setDocument(doc);
                Element arg = mArgument.getXML();
                String notes = mNotePanel.getText();
                if (notes.length() > 0) {
                    arg.setAttribute(Argument.NOTES, notes);
                }
                doc.appendChild(arg);

                log("Saving argument: " + filename);
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new FileWriter(new File(filename)));
                transformer.transform(source, result);
            } catch (Exception e) {
                Debug.println("Couldn't write argument file.");
                Debug.printStackTrace(e);
            }
        }
    }

    /**
     * Load an argument
     */
    public void load() {
        JFileChooser fileChooser = new JFileChooser();
        if (mCurrentDir != null) {
            fileChooser.setCurrentDirectory(mCurrentDir);
        }
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileFilter xmlFilter = new FileNameExtensionFilter("XML File", "xml");
        fileChooser.addChoosableFileFilter(xmlFilter);
        fileChooser.setFileFilter(xmlFilter);
        int option = fileChooser.showDialog(mAppWindow, "Open Argument");
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                // get full file path
                String filename = fileChooser.getSelectedFile().getName();
                mCurrentDir = fileChooser.getCurrentDirectory();
                filename = mCurrentDir + "/" + filename;
                // create necessary input streams
                DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = docBuilder.parse(new File(filename));
                Element root = doc.getDocumentElement();
                mNotePanel.setText(root.getAttribute(Argument.NOTES));
                mArgument = Argument.readXML(root);
                log("Loading argument: " + filename);
                updatePanels();
            } catch (Exception e) {
                Debug.println("Couldn't read argument file.");
                Debug.printStackTrace(e);
            }
        }
    }

    public void saveLog() {
        mCurrentDir = mLogPanel.saveFile(mCurrentDir);
    }

    public void saveEncoding() {
        mCurrentDir = mEncodingPanel.saveFile(mCurrentDir);
    }

    public void clearLog() {
        mLogPanel.clear();
    }

    public void addHypDialog() {
        PropositionEditPanel dialog = new PropositionEditPanel(this, new Proposition(), true);
        dialog.showDialog();
    }

    void addDataDialog() {
        PropositionEditPanel dialog = new PropositionEditPanel(this, new Proposition(), false);
        dialog.showDialog();
    }

    void addExpDialog() {
        LinkEditPanel dialog = new LinkEditPanel(this, new Link(Link.EXPLAIN));
        dialog.showDialog();
    }

    void addContDialog() {
        LinkEditPanel dialog = new LinkEditPanel(this, new Link(Link.CONTRADICT));
        dialog.showDialog();
    }

    public void updatePanels() {
        updateEncoding();
        updateText();
        updateGraph();
    }

    public void updateEncoding() {
        mEncoding = new Encoding(mArgument);
        mEncodingPanel.setText(mEncoding.getText());
    }

    public void updateText() {
        mTextListPanel.setText(getArg().getHypotheses(),
                getArg().getData(),
                getArg().getExps(),
                getArg().getConts());
    }

    public void updateGraph() {
        mDiagramPanel.setGraph(getArg().getHypotheses(),
                getArg().getData(),
                getArg().getExps(),
                getArg().getConts());
    }

    public void deleteSelected() {
        log("Deleting selected propositions and links...");
        getArg().deleteHypotheses(mTextListPanel.getSelectedHyps());
        getArg().deleteData(mTextListPanel.getSelectedData());
        getArg().deleteExplanations(mTextListPanel.getSelectedExps());
        getArg().deleteContradictions(mTextListPanel.getSelectedConts());
        updatePanels();
    }

    public void editSelected() {
        log("Editing selected propositions...");
        editFromVector(getArg().getHypotheses(), mTextListPanel.getSelectedHyps(), true);
        editFromVector(getArg().getData(), mTextListPanel.getSelectedData(), false);
        updatePanels();
    }

    public void editFromVector(PropositionVector v, int[] indexes, boolean isHyp) {
        Proposition prop;
        for (int index : indexes) {
            try {
                prop = v.getPropAt(index);
                log("Editing " + prop.getLabel());
                PropositionEditPanel dialog = new PropositionEditPanel(this, prop, isHyp);
                dialog.showDialog();
            } catch (Exception e) {
                Debug.printStackTrace(e);
            }
        }
    }

    public void showParams() {
        mParameterPanel.showDialog();
    }

    /**
     * Run the ECHO simulation, log what happens, and tell the user
     */
    public void runECHO() {
        runECHO(mParameterPanel.getExcitation(),
                mParameterPanel.getInhibition(),
                mParameterPanel.getDataExcitation(),
                mParameterPanel.getDecay());
    }

    public void showCorrelationMessage(String message) {
        JOptionPane.showMessageDialog(mAppWindow, message, "Simulation Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private JMenuBar getMenuBar() {
        // file menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem menuItem = new JMenuItem("New Argument");
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        menuItem.addActionListener(e -> newCM());
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Open Argument...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        menuItem.addActionListener(e -> load());
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Close Argument");
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl W"));
        menuItem.addActionListener(e -> dispose());
        fileMenu.add(menuItem);

        fileMenu.addSeparator();

        menuItem = new JMenuItem("Save Argument As...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        menuItem.addActionListener(e -> save());
        fileMenu.add(menuItem);


        menuItem = new JMenuItem("Save Log As...");
        menuItem.addActionListener(e -> saveLog());
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Save Encoding As...");
        menuItem.addActionListener(e -> saveEncoding());
        fileMenu.add(menuItem);

        fileMenu.addSeparator();

        menuItem = new JMenuItem("Quit");
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        menuItem.addActionListener(e -> dispose());
        fileMenu.add(menuItem);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");

        menuItem = new JMenuItem("Add Hypothesis...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl H"));
        menuItem.addActionListener(e -> addHypDialog());
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Add Data...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
        menuItem.addActionListener(e -> addDataDialog());
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Add Explanation...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl X"));
        menuItem.addActionListener(e -> addExpDialog());
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Add Contradiction...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
        menuItem.addActionListener(e -> addContDialog());

        editMenu.addSeparator();

        menuItem = new JMenuItem("Edit Selected Propositions");
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
        menuItem.addActionListener(e -> editSelected());
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Un-select All");
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl U"));
        menuItem.addActionListener(e -> updatePanels());
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Delete Selected");
        menuItem.addActionListener(e -> deleteSelected());
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Delete All");
        menuItem.addActionListener(e -> deleteArgument());
        editMenu.add(menuItem);

        editMenu.addSeparator();

        menuItem = new JMenuItem("Clear Log");
        menuItem.addActionListener(e -> clearLog());
        editMenu.add(menuItem);

        // Simulation menu
        JMenu simulationMenu = new JMenu("Simulation");

        menuItem = new JMenuItem("Run");
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
        menuItem.addActionListener(e -> runECHO());
        simulationMenu.add(menuItem);

        menuItem = new JMenuItem("Set parameters...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl P"));
        menuItem.addActionListener(e -> showParams());
        simulationMenu.add(menuItem);

        JMenuBar menubar = new JMenuBar();
        menubar.add(fileMenu);
        menubar.add(editMenu);
        menubar.add(simulationMenu);

        return menubar;
    }

}

