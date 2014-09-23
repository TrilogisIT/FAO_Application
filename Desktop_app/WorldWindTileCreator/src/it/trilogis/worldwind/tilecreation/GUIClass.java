/*
 * Copyright (C) 2014 Trilogis S.r.l.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.trilogis.worldwind.tilecreation;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.data.DataStoreProducer;
import gov.nasa.worldwind.data.TiledPKMImageProducer;
import gov.nasa.worldwind.data.TransparentPKMTiledImageProducer;
import gov.nasa.worldwind.data.WWDotNetLayerSetConverter;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.Logging;
import it.trilogis.worldwind.geo.Bounds;
import it.trilogis.worldwind.geo.LatLonPoint;
import it.trilogis.worldwind.tilecreation.MapAreaSelection.OnBoundingBoxSelectedListener;
import it.trilogis.worldwind.tilecreation.constants.Constants;
import it.trilogis.worldwind.tilecreation.constants.GUIConstants;
import it.trilogis.worldwind.tilecreation.constants.PropertiesConstants;
import it.trilogis.worldwind.tilecreation.datafilters.InstallableDataFilter;
import it.trilogis.worldwind.tilecreation.datafilters.ZipDataFilter;
import it.trilogis.worldwind.tilecreation.dialog.AboutDialog;
import it.trilogis.worldwind.tilecreation.dialog.HelpDialog;
import it.trilogis.worldwind.tilecreation.properties.PropertiesManager;
import it.trilogis.worldwind.tilecreation.properties.PropertiesUtils;
import it.trilogis.worldwind.tilecreation.swing.ZipDirectorySwingWorker;
import it.trilogis.worldwind.tilecreation.swing.utils.ImprovedFormattedTextField;
import it.trilogis.worldwind.tilecreation.utils.FileUtils;
import it.trilogis.worldwind.tilecreation.wwimport.utils.ImportUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.ItemSelectable;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import org.w3c.dom.Document;

/**
 * @author nmeneghini
 * @version $Id: GUIClass.java 1 2014-05-01 15:22:47Z nmeneghini $
 */
public class GUIClass extends JFrame implements ItemListener {

    /*
     * ONLY FOR TESTING:
     */
    public static final Boolean IS_DEBUG = true;
    public static final Boolean ENABLE_CHANGE_LEVELS = false;
    private static final String ZIP_FILE_NAME = "wwtiles";

    //
    private boolean _makeBackgroundLayerOpaque = false;

    // AlreadyExisting tiles option
    Object[] mDialogOptions = { "Re-download", "Overwrite", "Cancel" };

    //
    private static final int WINDOW_WIDTH = 730;
    private static final int WINDOW_HEIGHT = 640;

    // DEFAULT UI VALUES FOR COORDINATES
    private static final Double DEFAULT_MIN_LON = -180d, DEFAULT_MIN_LAT = -90d, DEFAULT_MAX_LON = +180d, DEFAULT_MAX_LAT = +90d;

    /**
     * The class serialUID
     */
    private static final long serialVersionUID = -7748014145568782949L;
    private JTextField baseDirectoryTF, subDirectoryTF;
    private ImprovedFormattedTextField minLongitude, minLatitude, maxLongitude, maxLatitude;
    private JFileChooser dirChooser;
    // Components to Disable:
    private List<JComponent> mDisableableComponents = new ArrayList<JComponent>();
    // Swing workers
    private ZipDirectorySwingWorker zipPackageWorker;
    private ProgressMonitor progressMonitor;

    // Panels that contains file chooser
    private JPanel greennessPanel, rainfallPanel, tpcPanel, landsatPanel, elevationPanel, boundariesPanel;
    // Panels for select a layer
    private JPanel tpcLeftPan, greennessLeftPan, rainfallLeftPan, landsatLeftPan, elevationLeftPan, boundariesLeftPan;
    // Panel now selected
    private JPanel panelSelected;

    final JList greennessListFiles = new JList();
    final JList rainfallListFiles = new JList();
    final JList tpcListFiles = new JList();
    final JList landsatListFiles = new JList();
    final JList elevationListFiles = new JList();
    final JList boundariesListFiles = new JList();

    final JFileChooser greennessfileChooser = new JFileChooser();
    final JFileChooser rainfallfileChooser = new JFileChooser();
    final JFileChooser tpcfileChooser = new JFileChooser();
    final JFileChooser landsatfileChooser = new JFileChooser();
    final JFileChooser elevationfileChooser = new JFileChooser();
    final JFileChooser boundariesfileChooser = new JFileChooser();
    
    List<File> greennessfiles = new ArrayList<File>();
    List<File> rainfallfiles = new ArrayList<File>();
    List<File> tpcfiles = new ArrayList<File>();
    List<File> landsatfiles = new ArrayList<File>();
    List<File> elevationfiles = new ArrayList<File>();
    

    Thread greennessThread = null;
    Thread rainfallThread = null;
    Thread tpcThread = null;
    Thread landsatThread = null;
    Thread elevationThread = null;
    Thread boundariesThread = null;

    JCheckBox chbTPC = new JCheckBox();
    JCheckBox chbLandSat = new JCheckBox();
    JCheckBox chbGreenness = new JCheckBox();
    JCheckBox chbRainfall = new JCheckBox();
    JCheckBox chbElev = new JCheckBox();
    JCheckBox chbBound = new JCheckBox();

    private JFrame aboutDialog = null, helpDialog = null;

    /** Creates new form GUIClass */
    public GUIClass() {
        setMinimumSize(new Dimension(600, 350));
        initGraphics();

        PropertiesManager.initProperties();
        setValuesFromProperties();

        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        ImageIcon icon = new ImageIcon(GUIConstants.IMAGE_URL_APP_ICON);
        this.setIconImage(icon.getImage());
        this.setTitle(GUIConstants.APPLICATION_TITLE);
    }

    private void setComponentMaxSize(JComponent component, int width, int height) {
        component.setMaximumSize(new Dimension(width, height));
    }

    private void initGraphics() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        JPanel rootLayout = new JPanel(new BorderLayout(10, 5));

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        // File props
        JMenu mnFile = new JMenu("File");
        mnFile.setIcon(new ImageIcon(GUIConstants.IMAGE_URL_FILE_ICON));
        menuBar.add(mnFile);
        JMenuItem mntmLoadProperties = new JMenuItem("Load Properties");
        mnFile.add(mntmLoadProperties);
        mntmLoadProperties.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Open dir chooser
                File tmp = new File(System.getProperty("user.home"));
                JFileChooser propChooser = new JFileChooser();
                propChooser.setCurrentDirectory(tmp);
                propChooser.setDialogTitle("Property File Selection");
                propChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                propChooser.setFileFilter(new FileFilter() {

                    @Override
                    public String getDescription() {
                        return "Property Files";
                    }

                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return false;
                        }

                        String extension = getExtension(f);
                        if (extension != null) {
                            if (extension.equalsIgnoreCase("properties")) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                        return false;
                    }

                    /*
                     * Get the extension of a file.
                     */
                    public String getExtension(File f) {
                        String ext = null;
                        String s = f.getName();
                        int i = s.lastIndexOf('.');

                        if (i > 0 && i < s.length() - 1) {
                            ext = s.substring(i + 1).toLowerCase();
                        }
                        return ext;
                    }
                });
                // disable the "All files" option.
                propChooser.setAcceptAllFileFilterUsed(false);
                //
                if (propChooser.showOpenDialog(GUIClass.this) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = propChooser.getSelectedFile();
                    PropertiesManager.setPropertiesFromFile(selectedFile);
                    setValuesFromProperties();
                } else {
                    System.out.println("No Base Directory selection, doing nothing...");
                }
            }
        });

        // Properties
        JMenu mnProps = new JMenu("Properties");
        menuBar.add(mnProps);
        final JCheckBoxMenuItem mnbackgroundOpaque = new JCheckBoxMenuItem("Background Opaque");
        mnbackgroundOpaque.setSelected(_makeBackgroundLayerOpaque);
        mnbackgroundOpaque.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent arg0) {
                _makeBackgroundLayerOpaque = mnbackgroundOpaque.isSelected();
                // System.out.println("Selected (BackgroundWILL BE OPAQUE): " + _makeBackgroundLayerOpaque);
            }
        });
        mnProps.add(mnbackgroundOpaque);

        // Right icons
        menuBar.add(Box.createHorizontalGlue());
        final JMenu mnAbout = new JMenu("About");
        mnAbout.setIcon(new ImageIcon(GUIConstants.IMAGE_URL_ABOUT_ICON));
        mnAbout.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent arg0) {
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }

            @Override
            public void mouseClicked(MouseEvent arg0) {
                showAboutDialog();
            }
        });

        JMenu mnHelp = new JMenu("Help");
        mnHelp.setIcon(new ImageIcon(GUIConstants.IMAGE_URL_HELP_ICON));
        mnHelp.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent arg0) {
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }

            @Override
            public void mouseClicked(MouseEvent arg0) {
                showHelpDialog();
            }
        });
        menuBar.add(mnAbout);
        menuBar.add(mnHelp);

        // Add layouts
        rootLayout.setBorder(new EmptyBorder(10, 10, 10, 10));
        rootLayout.add(getDirRelatedPanel(), BorderLayout.NORTH);
        rootLayout.add(getSettingsPanel(), BorderLayout.CENTER);
        rootLayout.add(getBottomPanel(), BorderLayout.SOUTH);
        this.setContentPane(rootLayout);
    }

    private void showAboutDialog() {

        if (aboutDialog != null && aboutDialog.isVisible()) {
            // aboutDialog. //DAI FOCUS
        } else {
            aboutDialog = new AboutDialog();
        }

    }

    private void showHelpDialog() {

        if (helpDialog != null && helpDialog.isVisible()) {
            // aboutDialog. //DAI FOCUS
        } else {
            helpDialog = new HelpDialog();
        }

    }

    private void toggleAllComponentsEnablement(boolean enable) {
        if (null == mDisableableComponents) {
            return;
        }
        for (JComponent component : mDisableableComponents) {
            component.setEnabled(enable);
        }
    }

    // ------------ TOP PANEL, DIRECTORY RELATED STUFF
    public JPanel getDirRelatedPanel() {
        JPanel directory = getDirectoryPanel();
        JPanel subdirectory = getSubDirectoryPanel();
        JPanel dirRelatedPanel = new JPanel();
        dirRelatedPanel.setLayout(new BoxLayout(dirRelatedPanel, BoxLayout.Y_AXIS));
        dirRelatedPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Directory for Tiles", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0,
            0)));

        dirRelatedPanel.add(directory);
        dirRelatedPanel.add(Box.createVerticalStrut(10));
        dirRelatedPanel.add(subdirectory);
        return dirRelatedPanel;
    }

    private JPanel getDirectoryPanel() {
        // Directory panel
        JPanel directory = new JPanel();
        directory.setAlignmentX(Component.LEFT_ALIGNMENT);
        directory.setLayout(new BoxLayout(directory, BoxLayout.X_AXIS));
        JLabel labelDirectory = new JLabel("Base Directory: ");
        labelDirectory.setFont(new Font("Tahoma", Font.BOLD, 11));
        baseDirectoryTF = new JTextField(System.getProperty("user.home") + File.separator + "Desktop");// FIXME Remove desktop link
        baseDirectoryTF.setMinimumSize(new Dimension(100, 20));
        setComponentMaxSize(baseDirectoryTF, 600, 25);
        baseDirectoryTF.setPreferredSize(new Dimension(100, 25));
        baseDirectoryTF.setHorizontalAlignment(SwingConstants.LEFT);
        baseDirectoryTF.setEditable(false);
        baseDirectoryTF.setToolTipText("The directory where data will be cached");

        JButton btnChooseBaseDir = new JButton("Select...");
        btnChooseBaseDir.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                onChooseBaseDirectoryClicked();
            }
        });
        mDisableableComponents.add(btnChooseBaseDir);
        Component horizontalStrut = Box.createHorizontalStrut(20);
        horizontalStrut.setMaximumSize(new Dimension(20, 20));
        Component horizontalStrut_1 = Box.createHorizontalStrut(20);
        horizontalStrut_1.setMaximumSize(new Dimension(20, 20));

        // Basedir panel additions
        directory.add(labelDirectory);
        directory.add(horizontalStrut);
        directory.add(baseDirectoryTF);
        directory.add(horizontalStrut_1);
        directory.add(btnChooseBaseDir);
        return directory;
    }

    private JPanel getSubDirectoryPanel() {
        // Subdir
        JPanel subdirectory = new JPanel();
        subdirectory.setAlignmentX(Component.LEFT_ALIGNMENT);
        subdirectory.setMaximumSize(new Dimension(600, 50));
        subdirectory.setMinimumSize(new Dimension(100, 20));
        subdirectory.setLayout(new BoxLayout(subdirectory, BoxLayout.X_AXIS));
        JLabel labelSubdir = new JLabel("Tiles Directory: ");
        labelSubdir.setFont(new Font("Tahoma", Font.BOLD, 11));
        subDirectoryTF = new JTextField("MyProjectTiles");// TODO getProperties
        subDirectoryTF.setMinimumSize(new Dimension(100, 20));
        subDirectoryTF.setMaximumSize(new Dimension(600, 25));
        subDirectoryTF.setHorizontalAlignment(SwingConstants.LEFT);
        subDirectoryTF.setEditable(true);
        subDirectoryTF.setPreferredSize(new Dimension(100, 25));
        subDirectoryTF.setToolTipText("The name of the directory/project for this operation. If the folder already exists, the content will be erased or replaced.");

        mDisableableComponents.add(subDirectoryTF);

        // Subdir panel additions
        subdirectory.add(labelSubdir);

        Component horizontalStrut_2 = Box.createHorizontalStrut(20);
        horizontalStrut_2.setMaximumSize(new Dimension(20, 20));
        subdirectory.add(horizontalStrut_2);
        subdirectory.add(subDirectoryTF);
        return subdirectory;
    }

    // ------------ CENTRAL PANEL, SETTINGS RELATED STUFF

    public JPanel getSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Cache Settings", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
        settingsPanel.add(getBoundingBoxPanel());
        return settingsPanel;
    }

    private JPanel getBoundingBoxPanel() {
        JPanel bBoxPanel = new JPanel();
        bBoxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Directory panel
        bBoxPanel.setLayout(new BoxLayout(bBoxPanel, BoxLayout.X_AXIS));
        JLabel labelbBox = new JLabel("Bounding Box: ");
        labelbBox.setFont(new Font("Tahoma", Font.BOLD, 11));

        // create textFields
        NumberFormat numberFormatInstance = NumberFormat.getNumberInstance(Locale.ENGLISH);
        // Min X
        minLongitude = new ImprovedFormattedTextField(numberFormatInstance, DEFAULT_MIN_LON);
        minLongitude.setColumns(20);
        minLongitude.setMinimumSize(new Dimension(40, 20));
        setComponentMaxSize(minLongitude, 50, 25);
        minLongitude.setPreferredSize(new Dimension(40, 25));
        minLongitude.setToolTipText("Set the leftmost coordinate for the area to download. Accepted values are -180 <= x < 180");
        // Min Y
        minLatitude = new ImprovedFormattedTextField(numberFormatInstance, DEFAULT_MIN_LAT);
        minLatitude.setColumns(20);
        minLatitude.setMinimumSize(new Dimension(40, 20));
        setComponentMaxSize(minLatitude, 50, 25);
        minLatitude.setPreferredSize(new Dimension(40, 25));
        minLatitude.setToolTipText("Set the bottom coordinate for the area to download. Accepted values are -90 <= x < 90");
        // Max X
        maxLongitude = new ImprovedFormattedTextField(numberFormatInstance, DEFAULT_MAX_LON);
        maxLongitude.setColumns(20);
        maxLongitude.setMinimumSize(new Dimension(40, 20));
        setComponentMaxSize(maxLongitude, 50, 25);
        maxLongitude.setPreferredSize(new Dimension(40, 25));
        maxLongitude.setToolTipText("Set the rightmost coordinate for the area to download. Accepted values are -180 < x <= 180");
        // Max Y
        maxLatitude = new ImprovedFormattedTextField(numberFormatInstance, DEFAULT_MAX_LAT);
        maxLatitude.setColumns(20);
        maxLatitude.setMinimumSize(new Dimension(40, 20));
        setComponentMaxSize(maxLatitude, 50, 25);
        maxLatitude.setPreferredSize(new Dimension(40, 25));
        maxLatitude.setToolTipText("Set the top coordinate for the area to download. Accepted values are -90 < x <= 90");

        // create struts
        Component horizontalStrut = Box.createHorizontalStrut(40);
        Component horizontalStrut1 = Box.createHorizontalStrut(10);
        Component horizontalStrut2 = Box.createHorizontalStrut(10);
        Component horizontalStrut3 = Box.createHorizontalStrut(10);
        Component horizontalStrut4 = Box.createHorizontalStrut(10);
        Dimension smallDimension = new Dimension(10, 20);
        horizontalStrut.setMaximumSize(new Dimension(30, 20));
        horizontalStrut1.setMaximumSize(smallDimension);
        horizontalStrut2.setMaximumSize(smallDimension);
        horizontalStrut3.setMaximumSize(smallDimension);
        horizontalStrut4.setMaximumSize(smallDimension);

        mDisableableComponents.add(minLatitude);
        mDisableableComponents.add(minLongitude);
        mDisableableComponents.add(maxLatitude);
        mDisableableComponents.add(maxLongitude);

        // Basedir panel additions
        bBoxPanel.add(labelbBox);
        bBoxPanel.add(horizontalStrut);
        bBoxPanel.add(new JLabel("Min Latitude: "));
        bBoxPanel.add(minLatitude);
        bBoxPanel.add(horizontalStrut1);
        bBoxPanel.add(new JLabel("Min Longitude: "));
        bBoxPanel.add(minLongitude);
        bBoxPanel.add(horizontalStrut2);
        bBoxPanel.add(new JLabel("Max Latitude: "));
        bBoxPanel.add(maxLatitude);
        bBoxPanel.add(horizontalStrut3);
        bBoxPanel.add(new JLabel("Max Longitude: "));
        bBoxPanel.add(maxLongitude);

        JButton javaAoiBtn = new JButton("Draw AOI");
        final OnBoundingBoxSelectedListener listnr = new OnBoundingBoxSelectedListener() {

            @Override
            public void onBoundingBoxSelectionCancelled() {
                System.out.println("Box selection cancelled");
            }

            @Override
            public void onBoundingBoxSelected(Bounds bounds) {
                minLatitude.setValue(bounds.getLowerRightPoint().getLatitude());
                maxLatitude.setValue(bounds.getUpperLeftPoint().getLatitude());
                minLongitude.setValue(bounds.getUpperLeftPoint().getLongitude());
                maxLongitude.setValue(bounds.getLowerRightPoint().getLongitude());
            }
        };
        javaAoiBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Open up the jframe giving a listener
                Bounds existingBounds = null;
                if (minLatitude.hasValidValue() && minLongitude.hasValidValue() && maxLatitude.hasValidValue() && maxLongitude.hasValidValue()) {
                    existingBounds = new Bounds();
                    existingBounds.setUpperLeftPoint(new LatLonPoint((Double) maxLatitude.getValue(), (Double) minLongitude.getValue()));
                    existingBounds.setLowerRightPoint(new LatLonPoint((Double) minLatitude.getValue(), (Double) maxLongitude.getValue()));
                }

                new MapAreaSelection(listnr, existingBounds).setVisible(true);
            }
        });
        bBoxPanel.add(horizontalStrut4);
        bBoxPanel.add(javaAoiBtn);

        return bBoxPanel;
    }

    public JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 5));

        // bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getListLayersPanel(), getOfflineLayersPanel());
        splitPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JPanel layersPanel = new JPanel(new BorderLayout());
        layersPanel.setLayout(new BoxLayout(layersPanel, BoxLayout.X_AXIS));
        layersPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Select layers", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
        layersPanel.add(splitPanel, BorderLayout.LINE_START);

        bottomPanel.add(layersPanel);
        bottomPanel.add(getPerformOperationPanel());

        return bottomPanel;
    }

    private Component getListLayersPanel() {
        JPanel list = new JPanel();
        list.setAlignmentX(Component.LEFT_ALIGNMENT);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        Dimension size = new Dimension(130, 370);
        list.setMinimumSize(size);
        list.setMaximumSize(size);
        list.setBackground(GUIConstants.COLOR_NEUTRAL);
        list.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel panBack = getLeftPanelLayerType(GUIConstants.LAYER_TYPE_BASEMAP,GUIConstants.LAYER_TYPE_BASEMAP_DESCRIPTION);
        JPanel panOver = getLeftPanelLayerType(GUIConstants.LAYER_TYPE_LATESTMAP,GUIConstants.LAYER_TYPE_LATESTMAP_DESCRIPTION);

        list.add(panBack);

        tpcLeftPan = getLeftPanelLayer(GUIConstants.LAYER_NAME_TPC, chbTPC);
        list.add(tpcLeftPan);

        landsatLeftPan = getLeftPanelLayer(GUIConstants.LAYER_NAME_LANDSAT, chbLandSat);
        list.add(landsatLeftPan);

        elevationLeftPan = getLeftPanelLayer(GUIConstants.LAYER_NAME_ELEVATION, chbElev);
        list.add(elevationLeftPan);

        boundariesLeftPan = getLeftPanelLayer(GUIConstants.LAYER_NAME_BOUNDARIES, chbBound);
        list.add(boundariesLeftPan);
        
        JPanel space = new JPanel();
        space.setBackground(GUIConstants.COLOR_NEUTRAL);
        space.setBorder(new EmptyBorder(8, 8, 8, 8));
        list.add(space);
        
        list.add(panOver);
        greennessLeftPan = getLeftPanelLayer(GUIConstants.LAYER_NAME_GREENNESS, chbGreenness);
        list.add(greennessLeftPan);

        rainfallLeftPan = getLeftPanelLayer(GUIConstants.LAYER_NAME_RAINFALL, chbRainfall);
        list.add(rainfallLeftPan);

        chbTPC.addItemListener(this);
        chbLandSat.addItemListener(this);
        chbGreenness.addItemListener(this);
        chbRainfall.addItemListener(this);
        chbElev.addItemListener(this);
        chbBound.addItemListener(this);

        mDisableableComponents.add(chbTPC);
        mDisableableComponents.add(chbLandSat);
        mDisableableComponents.add(chbGreenness);
        mDisableableComponents.add(chbRainfall);
        mDisableableComponents.add(chbElev);
        mDisableableComponents.add(chbBound);

        resetLeftComponentColor();

        return list;
    }

    // Mouse listener for left panels that represent layers
    MouseListener layersMouseListener = new MouseListener() {

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {
            // Check if component is disabled
            if (null != mDisableableComponents && !mDisableableComponents.get(0).isEnabled()) {
                // DO nothing
                return;
            }
            // Delete border in leftpanel
            JPanel panelSelect = (JPanel) e.getComponent();
            panelSelect.setBorder(new EmptyBorder(5, 5, 5, 5));
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // Check if component is disabled
            if (null != mDisableableComponents && !mDisableableComponents.get(0).isEnabled()) {
                // DO nothing
                return;
            }
            // Add border in leftpanel
            JPanel panelSelect = (JPanel) e.getComponent();
            panelSelect.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.LOWERED), new EmptyBorder(3, 3, 3, 3)));
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // Check if component is disabled
            if (null != mDisableableComponents && !mDisableableComponents.get(0).isEnabled()) {
                // DO nothing
                return;
            }
            JPanel panelSelect = (JPanel) e.getComponent();
            JCheckBox chbSelect = getCheckBoxFromPanel(panelSelect);

            if (panelSelect.equals(panelSelected)) {
                // Deselect layer
                resetLeftComponentColor();
                resetRightPanel();
                panelSelected = null;
            } else {
                // Select layer
                panelSelected = panelSelect;
                if (!chbSelect.isSelected()) {
                    chbSelect.setSelected(true);
                    // All operation be done by chb listener
                } else {
                    // Deselect other layer and select this one clicked
                    resetLeftComponentColor();
                    selectLeftComponent(chbSelect);
                }
            }
        }
    };

    private JCheckBox getCheckBoxFromPanel(JPanel panelSelect) {
        JCheckBox chbSelect = new JCheckBox();
        if (panelSelect.equals(tpcLeftPan)) {
            chbSelect = chbTPC;
        } else if (panelSelect.equals(landsatLeftPan)) {
            chbSelect = chbLandSat;
        } else if (panelSelect.equals(greennessLeftPan)) {
            chbSelect = chbGreenness;
        } else if (panelSelect.equals(rainfallLeftPan)) {
            chbSelect = chbRainfall;
        } else if (panelSelect.equals(elevationLeftPan)) {
            chbSelect = chbElev;
        } else if (panelSelect.equals(boundariesLeftPan)) {
            chbSelect = chbBound;
        }
        return chbSelect;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        ItemSelectable chb = e.getItemSelectable();

        if (e.getStateChange() == ItemEvent.SELECTED) {
            resetLeftComponentColor();
            selectLeftComponent(chb);
        }
    }

    /**
     * Enable the panel associate at the layer selected
     * 
     * @param panelSelect
     *            layer selected
     */
    public void enablePanel(JPanel panelSelect) {
        if (null == panelSelect) {
            return;
        }
        resetRightPanel();
        if (panelSelect.equals(tpcLeftPan)) {
            tpcPanel.setVisible(true);
        } else if (panelSelect.equals(landsatLeftPan)) {
            landsatPanel.setVisible(true);
        } else if (panelSelect.equals(greennessLeftPan)) {
            greennessPanel.setVisible(true);
        } else if (panelSelect.equals(rainfallLeftPan)) {
            rainfallPanel.setVisible(true);
        } else if (panelSelect.equals(elevationLeftPan)) {
            elevationPanel.setVisible(true);
        } else if (panelSelect.equals(boundariesLeftPan)) {
            boundariesPanel.setVisible(true);
        }
    }

    /**
     * Reset component of left panel that contains layers list Set all backgrond color
     */
    public void resetLeftComponentColor() {
        chbTPC.setBackground(GUIConstants.COLOR_NEUTRAL);
        chbGreenness.setBackground(GUIConstants.COLOR_NEUTRAL);
        chbLandSat.setBackground(GUIConstants.COLOR_NEUTRAL);
        chbRainfall.setBackground(GUIConstants.COLOR_NEUTRAL);
        chbElev.setBackground(GUIConstants.COLOR_NEUTRAL);
        chbBound.setBackground(GUIConstants.COLOR_NEUTRAL);
        tpcLeftPan.setBackground(GUIConstants.COLOR_NEUTRAL);
        landsatLeftPan.setBackground(GUIConstants.COLOR_NEUTRAL);
        rainfallLeftPan.setBackground(GUIConstants.COLOR_NEUTRAL);
        greennessLeftPan.setBackground(GUIConstants.COLOR_NEUTRAL);
        elevationLeftPan.setBackground(GUIConstants.COLOR_NEUTRAL);
        boundariesLeftPan.setBackground(GUIConstants.COLOR_NEUTRAL);
    }

    /**
     * Reset component of right panel that contains file choosers for all layers mutually exclusive Set all file choosers invisible
     */
    public void resetRightPanel() {
        greennessPanel.setVisible(false);
        rainfallPanel.setVisible(false);
        tpcPanel.setVisible(false);
        landsatPanel.setVisible(false);
        elevationPanel.setVisible(false);
        boundariesPanel.setVisible(false);
    }

    private void selectLeftComponent(ItemSelectable checkbox) {
        if (checkbox == chbGreenness) {
            enablePanel(greennessLeftPan);
            panelSelected = greennessLeftPan;
            greennessLeftPan.setBackground(GUIConstants.COLOR_OVERLAY);
            chbGreenness.setBackground(GUIConstants.COLOR_OVERLAY);
        } else if (checkbox == chbRainfall) {
            enablePanel(rainfallLeftPan);
            panelSelected = rainfallLeftPan;
            rainfallLeftPan.setBackground(GUIConstants.COLOR_OVERLAY);
            chbRainfall.setBackground(GUIConstants.COLOR_OVERLAY);
        } else if (checkbox == chbTPC) {
            enablePanel(tpcLeftPan);
            panelSelected = tpcLeftPan;
            tpcLeftPan.setBackground(GUIConstants.COLOR_BACKGROUND);
            chbTPC.setBackground(GUIConstants.COLOR_BACKGROUND);
        } else if (checkbox == chbLandSat) {
            enablePanel(landsatLeftPan);
            panelSelected = landsatLeftPan;
            landsatLeftPan.setBackground(GUIConstants.COLOR_BACKGROUND);
            chbLandSat.setBackground(GUIConstants.COLOR_BACKGROUND);
        } else if (checkbox == chbElev) {
            enablePanel(elevationLeftPan);
            panelSelected = elevationLeftPan;
            elevationLeftPan.setBackground(GUIConstants.COLOR_ELEVATION);
            chbElev.setBackground(GUIConstants.COLOR_ELEVATION);
        } else if (checkbox == chbBound) {
            enablePanel(boundariesLeftPan);
            panelSelected = boundariesLeftPan;
            boundariesLeftPan.setBackground(GUIConstants.COLOR_BOUNDARIES);
            chbBound.setBackground(GUIConstants.COLOR_BOUNDARIES);
        }
    }

    /**
     * Create layer type left panel like Background, Overlay ecc..
     * 
     * @param type
     * @return JPanel layer type left panel
     */
    private JPanel getLeftPanelLayerType(String type, String description) {
        JLabel lblType = new JLabel(type);
        JLabel lblDesc = new JLabel(description);
        lblType.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblDesc.setFont(new Font("Tahoma", Font.ITALIC, 12));
        JPanel panelType = new JPanel(new BorderLayout(0, 0));
        panelType.add(lblType, BorderLayout.CENTER);
        panelType.add(lblDesc, BorderLayout.SOUTH);
        panelType.setBackground(GUIConstants.COLOR_NEUTRAL);
        panelType.setBorder(new EmptyBorder(3, 0, 3, 0));
        return panelType;
    }

    /**
     * Create layer left panel like TPC, Greenness
     * 
     * @param layerName
     *            Layer name
     * @param chb
     *            CheckBox of the layer
     * @return JPanel layer left panel
     */
    private JPanel getLeftPanelLayer(String layerName, JCheckBox chb) {
        JPanel layerLeftPan = new JPanel();
        layerLeftPan.setLayout(new BorderLayout(0, 0));
        layerLeftPan.add(chb, BorderLayout.LINE_START);
        layerLeftPan.add(new JLabel(layerName), BorderLayout.CENTER);
        layerLeftPan.addMouseListener(layersMouseListener);
        layerLeftPan.setBorder(new EmptyBorder(5, 5, 5, 5));
        return layerLeftPan;
    }

    /**
     * Create layerspanel that contains all layer panels where you choose files. By default all layer panels are invisible and only when is selected left panel the layer panel
     * associate is set to visible. So only one layer panel are visible in this container at runtime.
     * 
     * @return JPanel layerspanel
     */
    public JPanel getOfflineLayersPanel() {
        JPanel offlinePanel = new JPanel(new GridBagLayout());

        greennessPanel = getLayerPanel(greennessfileChooser, greennessListFiles, GUIConstants.LAYER_NAME_GREENNESS, GUIConstants.COLOR_OVERLAY,
            PropertiesConstants.PROPERTY_GREENNESS, greennessfiles);
        rainfallPanel = getLayerPanel(rainfallfileChooser, rainfallListFiles, GUIConstants.LAYER_NAME_RAINFALL, GUIConstants.COLOR_OVERLAY, PropertiesConstants.PROPERTY_RAINFALL, rainfallfiles);
        tpcPanel = getLayerPanel(tpcfileChooser, tpcListFiles, GUIConstants.LAYER_NAME_TPC, GUIConstants.COLOR_BACKGROUND, PropertiesConstants.PROPERTY_TPC, tpcfiles);
        landsatPanel = getLayerPanel(landsatfileChooser, landsatListFiles, GUIConstants.LAYER_NAME_LANDSAT, GUIConstants.COLOR_BACKGROUND, PropertiesConstants.PROPERTY_LANDSAT, landsatfiles);
        elevationPanel = getLayerPanel(elevationfileChooser, elevationListFiles, GUIConstants.LAYER_NAME_ELEVATION, GUIConstants.COLOR_ELEVATION,
            PropertiesConstants.PROPERTY_ELEVATION, elevationfiles);
        boundariesPanel = getBoundariesPanel();

        resetRightPanel();

        offlinePanel.add(greennessPanel);
        offlinePanel.add(rainfallPanel);
        offlinePanel.add(tpcPanel);
        offlinePanel.add(landsatPanel);
        offlinePanel.add(elevationPanel);
        offlinePanel.add(boundariesPanel);

        return offlinePanel;
    }

    /**
     * Create boundaries layer panel, this is different for other layer panels because have a different fileChooser
     * 
     * @return JPanel boundariesPanel
     */
    private JPanel getBoundariesPanel() {
        final JPanel boundaries = new JPanel();
        boundaries.setBackground(GUIConstants.COLOR_BOUNDARIES);
        boundaries.setLayout(new BoxLayout(boundaries, BoxLayout.X_AXIS));
        boundaries.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), GUIConstants.LAYER_NAME_BOUNDARIES, TitledBorder.CENTER, TitledBorder.TOP, new Font("",
            0, 16), new Color(0, 0, 0)));

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setBackground(GUIConstants.COLOR_BOUNDARIES);

        JButton btnChooseFiles = new JButton("Select File(s)");
        btnChooseFiles.setBackground(GUIConstants.COLOR_BOUNDARIES);
        JButton btnClear = new JButton("Clear list");
        btnClear.setBackground(GUIConstants.COLOR_BOUNDARIES);
        buttons.add(btnChooseFiles);
        buttons.add(Box.createRigidArea(new Dimension(1, 20)));
        buttons.add(btnClear);
        mDisableableComponents.add(btnChooseFiles);
        mDisableableComponents.add(btnClear);

        buttons.setBorder(new EmptyBorder(15, 25, 15, 25));
        boundaries.add(buttons);

        JPanel listPan = new JPanel();
        listPan.setLayout(new BoxLayout(listPan, BoxLayout.Y_AXIS));
        listPan.setBackground(GUIConstants.COLOR_BOUNDARIES);

        boundariesListFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        boundariesListFiles.setLayoutOrientation(JList.VERTICAL);
        boundariesListFiles.setVisibleRowCount(7);
        JScrollPane listScroller = new JScrollPane(boundariesListFiles);
        mDisableableComponents.add(boundariesListFiles);
        mDisableableComponents.add(listScroller);

        listPan.add(Box.createRigidArea(new Dimension(300, 50)));
        listPan.add(listScroller);
        listPan.add(Box.createRigidArea(new Dimension(300, 50)));
        boundaries.add(listPan);
        boundaries.add(Box.createRigidArea(new Dimension(10, 10)));

        boundariesfileChooser.addChoosableFileFilter(new ZipDataFilter());
        boundariesfileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        btnChooseFiles.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                int retVal = boundariesfileChooser.showDialog(boundaries, "Install");
                if (retVal != JFileChooser.APPROVE_OPTION)
                    return;

                final File file = boundariesfileChooser.getSelectedFile();
                if (file == null)
                    return;

                boundariesListFiles.setModel(new DefaultListModel());
                ((DefaultListModel) boundariesListFiles.getModel()).add(0, file.getName());

                PropertiesManager.setArrayStringProperty(PropertiesConstants.PROPERTY_BOUNDARIES_FILES, new String[] { file.getName() });
                PropertiesManager.setStringProperty(PropertiesConstants.PROPERTY_BOUNDARIES_DIRECTORY, boundariesfileChooser.getCurrentDirectory().getAbsolutePath());
            }
        });

        btnClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boundariesListFiles.setModel(new DefaultListModel());
                boundariesfileChooser.setSelectedFile(new File(""));
            }
        });

        return boundaries;

    }

    /**
     * Create a layer panel
     * 
     * @return JPanel layer panel
     */
    private JPanel getLayerPanel(final JFileChooser fileChooser, final JList list, String layerName, Color color, final String propertyLayer, final List<File> presentFiles) {
        final JPanel custom = new JPanel();
        custom.setBackground(color);
        custom.setLayout(new BoxLayout(custom, BoxLayout.X_AXIS));
        custom.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), layerName, TitledBorder.CENTER, TitledBorder.TOP, new Font("", 0, 16), new Color(0, 0, 0)));

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setBackground(color);

        JButton btnChooseFiles = new JButton("Add File(s)");
        btnChooseFiles.setBackground(color);
        JButton btnClear = new JButton("Clear list");
        btnClear.setBackground(color);
        buttons.add(btnChooseFiles);
        buttons.add(Box.createRigidArea(new Dimension(1, 20)));
        buttons.add(btnClear);
        mDisableableComponents.add(btnChooseFiles);
        mDisableableComponents.add(btnClear);

        buttons.setBorder(new EmptyBorder(15, 25, 15, 25));
        custom.add(buttons);

        JPanel listPan = new JPanel();
        listPan.setLayout(new BoxLayout(listPan, BoxLayout.Y_AXIS));
        listPan.setBackground(color);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(7);
        JScrollPane listScroller = new JScrollPane(list);
        mDisableableComponents.add(list);
        mDisableableComponents.add(listScroller);

        listPan.add(Box.createRigidArea(new Dimension(300, 50)));
        listPan.add(listScroller);
        listPan.add(Box.createRigidArea(new Dimension(300, 50)));
        custom.add(listPan);
        custom.add(Box.createRigidArea(new Dimension(10, 10)));

        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.addChoosableFileFilter(new InstallableDataFilter());
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        btnChooseFiles.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                int retVal = fileChooser.showDialog(custom, "Install");
                if (retVal != JFileChooser.APPROVE_OPTION)
                    return;

                final File[] files = fileChooser.getSelectedFiles();
                if (files == null || files.length == 0)
                    return;
                
                for (int i = 0; i < files.length; i++) {
                	if(!isPresentInList(presentFiles,files[i])){
	                    presentFiles.add(files[i]);
                	}
                }

                list.setModel(new DefaultListModel());
                
                String[] filesPath = new String[presentFiles.size()];
                for(int j = 0; j< presentFiles.size();j++){
                	filesPath[j] = presentFiles.get(j).getAbsolutePath();
                	((DefaultListModel) list.getModel()).add(j, presentFiles.get(j).getName());
                }

                PropertiesManager.setArrayStringProperty(propertyLayer + PropertiesConstants.PROPERTY_FILES, filesPath);
                PropertiesManager.setStringProperty(propertyLayer + PropertiesConstants.PROPERTY_DIRECTORY, fileChooser.getCurrentDirectory().getAbsolutePath());
            }
        });

        btnClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                list.setModel(new DefaultListModel());
                fileChooser.setSelectedFile(new File(""));
                fileChooser.setSelectedFiles(new File[] { new File("") });
                presentFiles.clear();
                PropertiesManager.setArrayStringProperty(propertyLayer + PropertiesConstants.PROPERTY_FILES, null);
            }
        });
        return custom;
    }
    
    private final boolean isPresentInList(List<File> files, File file){
    	int i = 0;
    	boolean find = false;
    	while((!find)&&(i<files.size())){
    		if(files.get(i).getAbsolutePath().equals(file.getAbsolutePath())){
    			find=true;
    		}
    		i++;
    	}
    	return find;
    }

    /**
     * Create perform panel which contain the buttons: import and create package
     * 
     * @return JPanel performPanel
     */
    private JPanel getPerformOperationPanel() {
        // Directory panel
        JPanel performOperationPanel = new JPanel();

        performOperationPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Package Creation", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0,
            0, 0)));

        performOperationPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        performOperationPanel.setLayout(new BoxLayout(performOperationPanel, BoxLayout.X_AXIS));

        JButton btnImportAll = new JButton("Import all");
        btnImportAll.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnImportAll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                onImportAllClicked();
            }
        });
        mDisableableComponents.add(btnImportAll);

        JButton btnCreateZipFile = new JButton("Create Package");
        btnCreateZipFile.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnCreateZipFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                onCreatePackageClicked();
            }
        });
        mDisableableComponents.add(btnCreateZipFile);

        performOperationPanel.add(btnImportAll);
        performOperationPanel.add(Box.createHorizontalGlue());
        performOperationPanel.add(btnCreateZipFile);

        return performOperationPanel;
    }

    // ----------- LISTENERS ----------------------
    /**
     * Action performed when import button is clicked
     */
    protected void onImportAllClicked() {
        // Initialize input params
        boolean green = false, rain = false, tp = false, land = false, elev = false, bound = false;

        baseDirectoryTF.getText();
        subDirectoryTF.getText();

        final File directory = new File(baseDirectoryTF.getText(), subDirectoryTF.getText());

        boolean hasGreenness = true, hasRainfall = true, hasTPC = true, hasLandsat = true, hasElevation = true, hasBoundaries = true;
//        final File[] greennessfiles = this.greennessfileChooser.getSelectedFiles();
//        final File[] rainfallfiles = this.rainfallfileChooser.getSelectedFiles();
//        final File[] tpcfiles = this.tpcfileChooser.getSelectedFiles();
//        final File[] landsatfiles = this.landsatfileChooser.getSelectedFiles();
//        final File[] elevationfiles = this.elevationfileChooser.getSelectedFiles();
        final File boundariesfile = this.boundariesfileChooser.getSelectedFile();

        if (greennessfiles == null || greennessfiles.size() == 0 || !chbGreenness.isSelected())
            hasGreenness = false;
        if (rainfallfiles == null || rainfallfiles.size() == 0 || !chbRainfall.isSelected())
            hasRainfall = false;
        if (tpcfiles == null || tpcfiles.size() == 0 || !chbTPC.isSelected())
            hasTPC = false;
        if (landsatfiles == null || landsatfiles.size() == 0 || !chbLandSat.isSelected())
            hasLandsat = false;
        if (elevationfiles == null || elevationfiles.size() == 0 || !chbElev.isSelected())
            hasElevation = false;
        if (boundariesfile == null || !chbBound.isSelected())
            hasBoundaries = false;

        // Check if bounding box is valid
        final Sector bbox = pickBounding();
        if (null != bbox) {

            // Check if at least a layer is insert
            if (hasGreenness || hasRainfall || hasTPC || hasLandsat || hasElevation || hasBoundaries) {

                // Check if directory exist and if is present layers
                if (directory.exists()) {
                    File greenness = new File(directory.getAbsolutePath() + "/" + Constants.OVERLAY_GREENNESS_CACHE_FOLDER + "/", Constants.OVERLAY_GREENNESS_CACHE_FOLDER + ".xml");
                    File rainfall = new File(directory.getAbsolutePath() + "/" + Constants.OVERLAY_RAINFALL_CACHE_FOLDER + "/", Constants.OVERLAY_RAINFALL_CACHE_FOLDER + ".xml");
                    File tpc = new File(directory.getAbsolutePath() + "/" + Constants.BACKGROUND_TPC_CACHE_FOLDER + "/", Constants.BACKGROUND_TPC_CACHE_FOLDER + ".xml");
                    File landsat = new File(directory.getAbsolutePath() + "/" + Constants.BACKGROUND_LANDSAT_CACHE_FOLDER + "/", Constants.BACKGROUND_LANDSAT_CACHE_FOLDER + ".xml");
                    File elevation = new File(directory.getAbsolutePath() + "/" + Constants.ELEVATION_LAYER_CACHE_FOLDER + "/", Constants.ELEVATION_LAYER_CACHE_FOLDER + ".xml");
                    File boundaries = new File(directory.getAbsolutePath() + "/" + Constants.BOUNDARIES_LAYER_CACHE_FOLDER + "/", Constants.BOUNDARIES_LAYER_CACHE_FOLDER + ".xml");

                    if (greenness.exists() && hasGreenness) {
                        green = true;
                    }
                    if (rainfall.exists() && hasRainfall) {
                        rain = true;
                    }
                    if (tpc.exists() && hasTPC) {
                        tp = true;
                    }
                    if (landsat.exists() && hasLandsat) {
                        land = true;
                    }
                    if (elevation.exists() && hasElevation) {
                        elev = true;
                    }
                    if (boundaries.exists() && hasBoundaries) {
                        bound = true;
                    }

                    if (green || rain || tp || land || elev || bound) {
                        Object[] mDialogOptions = { "Add", "Replace", "Cancel" };
                        int userOptionResult = JOptionPane.showOptionDialog(this, "Tiles already present!\nDo you want to add or replace?", "Do you want to add or replace?",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, mDialogOptions, mDialogOptions[1]);
                        if (userOptionResult != JOptionPane.NO_OPTION && userOptionResult != JOptionPane.YES_OPTION) {
                            System.out.println("User wants to do nothing");
                            return;
                        }
                        // YES_OPTION is "Re-Download", so the customOverlay folder needs to be deleted
                        if (userOptionResult == JOptionPane.NO_OPTION) {
                            // replace-->delete folder present
                            if (green) {
                                // delete dir directory+"/"+WMS_LAYER_CACHE_FOLDER
                                File newCustom = new File(directory.getAbsolutePath() + "/" + Constants.OVERLAY_GREENNESS_CACHE_FOLDER);
                                FileUtils.deleteNonEmptyDirectory(newCustom);
                                newCustom.mkdir();
                            }
                            if (rain) {
                                // delete dir directory+"/"+WMS_LAYER_CACHE_FOLDER
                                File newCustom = new File(directory.getAbsolutePath() + "/" + Constants.OVERLAY_RAINFALL_CACHE_FOLDER);
                                FileUtils.deleteNonEmptyDirectory(newCustom);
                                newCustom.mkdir();
                            }
                            if (tp) {
                                File newBack = new File(directory.getAbsolutePath() + "/" + Constants.BACKGROUND_TPC_CACHE_FOLDER);
                                FileUtils.deleteNonEmptyDirectory(newBack);
                                newBack.mkdir();
                            }
                            if (land) {
                                File newBack = new File(directory.getAbsolutePath() + "/" + Constants.BACKGROUND_LANDSAT_CACHE_FOLDER);
                                FileUtils.deleteNonEmptyDirectory(newBack);
                                newBack.mkdir();
                            }
                            if (elev) {
                                File newElev = new File(directory.getAbsolutePath() + "/" + Constants.ELEVATION_LAYER_CACHE_FOLDER);
                                FileUtils.deleteNonEmptyDirectory(newElev);
                                newElev.mkdir();
                            }
                            if (bound) {
                                File newCustom = new File(directory.getAbsolutePath() + "/" + Constants.BOUNDARIES_LAYER_CACHE_FOLDER);
                                FileUtils.deleteNonEmptyDirectory(newCustom);
                                newCustom.mkdir();
                            }
                        }
                    }

                } else {
                    directory.mkdir();
                }

                toggleAllComponentsEnablement(false);

                // Start work

                if (hasGreenness) {
                    greennessThread = getThread(greennessfiles.toArray(new File[greennessfiles.size()]), Constants.OVERLAY_GREENNESS_CACHE_FOLDER, bbox);
                    greennessThread.start();
                }

                if (hasRainfall) {
                    rainfallThread = getThread(rainfallfiles.toArray(new File[rainfallfiles.size()]), Constants.OVERLAY_RAINFALL_CACHE_FOLDER, bbox);
                    rainfallThread.start();
                }

                if (hasTPC) {
                    tpcThread = getThread(tpcfiles.toArray(new File[tpcfiles.size()]), Constants.BACKGROUND_TPC_CACHE_FOLDER, bbox);
                    tpcThread.start();
                }

                if (hasLandsat) {
                    landsatThread = getThread(landsatfiles.toArray(new File[landsatfiles.size()]), Constants.BACKGROUND_LANDSAT_CACHE_FOLDER, bbox);
                    landsatThread.start();
                }

                if (hasElevation) {
                    elevationThread = getThread(elevationfiles.toArray(new File[elevationfiles.size()]), Constants.ELEVATION_LAYER_CACHE_FOLDER, bbox);
                    elevationThread.start();
                }

                if (hasBoundaries) {
                    boundariesThread = new Thread(new Runnable() {
                        public void run() {

                            try {
                                // Unzip in boundaries folder
                                final ProgressMonitor progressMonitor = new ProgressMonitor(GUIClass.this, "Importing boundaries", null, 0, 100);
                                ZipInputStream zin = new ZipInputStream(new FileInputStream(boundariesfile));
                                ZipFile z = new ZipFile(boundariesfile);
                                File directory = new File(baseDirectoryTF.getText(), subDirectoryTF.getText());
                                int count = 0;
                                int progress = 0;
                                int progressAt = z.size() / 100;
                                ZipEntry entry;
                                String name, dir;
                                while ((entry = zin.getNextEntry()) != null) {
                                    count++;
                                    if (count == progressAt) {
                                        progress++;
                                        count = 0;
                                        progressMonitor.setProgress((int) (progress));
                                    }

                                    name = entry.getName();
                                    if (entry.isDirectory()) {
                                        FileUtils.makedirectory(directory, name);
                                        continue;
                                    } else {
                                        if (name.contains("\\")) {
                                            name = name.replace('\\', '/');
                                            String nameFolder = name.substring(0, name.lastIndexOf('/') + 1);
                                            String realNameFile = name.substring(name.lastIndexOf('/') + 1, name.length());
                                            if (nameFolder != null) {
                                                FileUtils.makedirectory(directory, nameFolder);
                                            }
                                            FileUtils.extractFile(zin, new File(directory + "/" + nameFolder), realNameFile);
                                            continue;

                                        }
                                    }
                                    dir = FileUtils.directoryPart(name);
                                    if (dir != null)
                                        FileUtils.makedirectory(directory, dir);

                                    FileUtils.extractFile(zin, directory, name);

                                }
                                zin.close();

                            } catch (Exception e) {
                                final String message = e.getMessage();
                                Logging.logger().log(java.util.logging.Level.FINEST, message, e);

                                // Show a message dialog indicating that the installation failed, and why.
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        JOptionPane.showMessageDialog(GUIClass.this, message, "Installation Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                });
                            }
                            if (!isOtherThreadsAlive(boundariesThread)) {
                                toggleAllComponentsEnablement(true);
                            }
                        }
                    });
                    boundariesThread.start();
                }
                refreshProperties();
            } else {
                showErrorMessage("No file to import", "Please select files");
                // TODO error no files to import
            }
        } else {
            showErrorMessage("Bounding box error", "Please insert\nLatitude between -90 and 90.\nLongitude between -180 and 180.");
            // ERROR bounding bbox
        }
    }

    protected Thread getThread(final File[] files, final String layerName, final Sector bbox) {
        return new Thread(new Runnable() {
            public void run() {

                try {
                    // Install the file into a form usable by World Wind components.
                    installDataFromFiles(GUIClass.this, files, new File(baseDirectoryTF.getText(), subDirectoryTF.getText()), layerName, bbox);
                } catch (Exception e) {
                    final String message = e.getMessage();
                    Logging.logger().log(java.util.logging.Level.FINEST, message, e);

                    // Show a message dialog indicating that the installation failed, and why.
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            JOptionPane.showMessageDialog(GUIClass.this, message, "Installation Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }

                if (!isOtherThreadsAlive(getCurrentThread(layerName))) {
                    toggleAllComponentsEnablement(true);
                }
            }
        });
    }

    protected Document installDataFromFiles(Component parentComponent, File[] files, File directory, String datasetName, Sector bbox) throws Exception {

        // Create a ProgressMonitor that will provide feedback on how
        final ProgressMonitor progressMonitor = new ProgressMonitor(parentComponent, "Importing " + datasetName, null, 0, 100);

        final AtomicInteger progress = new AtomicInteger(0);

        final DataStoreProducer producer = getProducer(files, bbox, datasetName, progressMonitor, progress);

        progressMonitor.setProgress(0);

        // Configure a timer to check if the user has clicked the ProgressMonitor's "Cancel" button. If so, stop
        // production as soon as possible. This just stops the production from completing; it doesn't clean up any state
        // changes made during production,
        java.util.Timer progressTimer = new java.util.Timer();
        progressTimer.schedule(new TimerTask() {
            public void run() {
                progressMonitor.setProgress(progress.get());

                if (progressMonitor.isCanceled()) {
                    producer.stopProduction();
                    this.cancel();
                }
            }
        }, progressMonitor.getMillisToDecideToPopup(), 100L);

        Document doc = null;
        try {
            // Install the file into the specified FileStore.
            doc = ImportUtils.createDataStore(files, directory, producer, datasetName);

            // Create a raster server configuration document if the installation was successful
            // and we're not converting a WW.NET tile set to a WW Java tile set.
            // The raster server document enables the layer or elevation model (created to display this data)
            // to create tiles from the original sources at runtime.
            if (doc != null && !(producer instanceof WWDotNetLayerSetConverter))
                ImportUtils.createRasterServerConfigDoc(directory, producer);

            // The user clicked the ProgressMonitor's "Cancel" button. Revert any change made during production, and
            // discard the returned DataConfiguration reference.
            if (progressMonitor.isCanceled()) {
                doc = null;
                producer.removeProductionState();
            }
        } finally {
            // Remove the progress event listener from the DataStoreProducer. stop the progress timer, and signify to the
            // ProgressMonitor that we're done.
            // producer.removePropertyChangeListener(progressListener);TODO FIXME
            producer.removeAllDataSources();
            progressMonitor.close();
            progressTimer.cancel();
        }

        return doc;
    }

    private DataStoreProducer getProducer(File[] files, Sector bbox, String datasetName, final ProgressMonitor progressMonitor, final AtomicInteger progress) {
        final Thread currentThread = getCurrentThread(datasetName);
        // final boolean makeBackgroundsNonTransparent = false;//FIXME this should be selected in UI
        final boolean background = (datasetName == Constants.BACKGROUND_LANDSAT_CACHE_FOLDER || datasetName == Constants.BACKGROUND_TPC_CACHE_FOLDER);
        final DataStoreProducer producer = ImportUtils.createDataStoreProducerFromFiles(files, bbox, _makeBackgroundLayerOpaque && background);
        PropertyChangeListener progressListener = null;

        if (datasetName == Constants.ELEVATION_LAYER_CACHE_FOLDER) {
            progressListener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (progressMonitor.isCanceled()) {
                        if (!isOtherThreadsAlive(currentThread)) {
                            toggleAllComponentsEnablement(true);
                        }
                        return;
                    }
                    if (evt.getPropertyName().equals(AVKey.PROGRESS)) {
                        Double tileProgress = (Double) evt.getNewValue();
                        progress.set((int) (100 * tileProgress));
                    }
                }
            };
        } else {
            progressListener = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (progressMonitor.isCanceled()) {
                        if (!isOtherThreadsAlive(currentThread)) {
                            toggleAllComponentsEnablement(true);
                        }
                        return;
                    }
                    if (evt.getPropertyName().equals(AVKey.PROGRESS)) {
                        Double tileProgress = (Double) evt.getNewValue();
                        int count = 0;
                        if (_makeBackgroundLayerOpaque && background) {
                            count = ((TiledPKMImageProducer) producer).getTotalTileCount();
                        } else {
                            count = ((TransparentPKMTiledImageProducer) producer).getTotalTileCount();
                        }
                        progress.set((int) (100 * tileProgress));
                        String message = String.format("Import %d of " + count + " tiles", (int) (tileProgress * count));
                        progressMonitor.setNote(message);
                    }
                }
            };
        }

        producer.addPropertyChangeListener(progressListener);

        return producer;
    }

    public Thread getCurrentThread(String layerFolder) {
        if (layerFolder == Constants.BACKGROUND_LANDSAT_CACHE_FOLDER) {
            return landsatThread;
        }
        if (layerFolder == Constants.BACKGROUND_TPC_CACHE_FOLDER) {
            return tpcThread;
        }
        if (layerFolder == Constants.OVERLAY_GREENNESS_CACHE_FOLDER) {
            return greennessThread;
        }
        if (layerFolder == Constants.OVERLAY_RAINFALL_CACHE_FOLDER) {
            return rainfallThread;
        }
        if (layerFolder == Constants.ELEVATION_LAYER_CACHE_FOLDER) {
            return elevationThread;
        }
        if (layerFolder == Constants.BOUNDARIES_LAYER_CACHE_FOLDER) {
            return boundariesThread;
        }

        return null;
    }

    public boolean isOtherThreadsAlive(Thread currentThread) {
        boolean isAlive = true;
        if ((null == elevationThread || !elevationThread.isAlive()) || currentThread == elevationThread)
            if ((null == greennessThread || !greennessThread.isAlive()) || currentThread == greennessThread)
                if ((null == rainfallThread || !rainfallThread.isAlive()) || currentThread == rainfallThread)
                    if ((null == tpcThread || !tpcThread.isAlive()) || currentThread == tpcThread)
                        if ((null == landsatThread || !landsatThread.isAlive()) || currentThread == landsatThread)
                            if ((null == boundariesThread || !boundariesThread.isAlive()) || currentThread == boundariesThread)
                                isAlive = false;
        return isAlive;
    }

    public void onChooseBaseDirectoryClicked() {
        // Open dir chooser
        File tmp = new File(baseDirectoryTF.getText());
        if (!tmp.exists()) {
            tmp = new File(System.getProperty("user.home"));
        }
        dirChooser = new JFileChooser();
        dirChooser.setCurrentDirectory(tmp);
        dirChooser.setDialogTitle("Base Folder selection:");
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // disable the "All files" option.
        dirChooser.setAcceptAllFileFilterUsed(false);
        //
        if (dirChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = dirChooser.getSelectedFile();
            baseDirectoryTF.setText(selectedFolder.getAbsolutePath());
        } else {
            System.out.println("No Base Directory selection, doing nothing...");
        }
    }

    public void onCreatePackageClicked() {
        boolean hasErrors = false;
        StringBuilder sb = new StringBuilder();
        // Cache directory checking
        String baseCacheDir = this.baseDirectoryTF.getText();
        if (null == baseCacheDir || baseCacheDir.trim().isEmpty()) {
            hasErrors = true;
            sb.append(" - The directory in which put data is not valid. Please select one.\n");
        }
        String projectCacheDirName = this.subDirectoryTF.getText();
        if (null == projectCacheDirName || projectCacheDirName.trim().isEmpty()) {
            hasErrors = true;
            sb.append(" - The project directory name is not valid. Please provide a alphanumeric one.\n");
        } else {
            File f = new File(projectCacheDirName);
            try {
                f.getCanonicalPath();
            } catch (IOException e) {
                hasErrors = true;
                sb.append(" - The project directory name is not valid. Please provide a alphanumeric one.\n");
            }
        }
        // Check if cache directory exists for the customOverlay layer
        final String cacheDirectory = baseCacheDir + File.separator + projectCacheDirName;
        // CACHE DIRECTORY
        File cacheDir = new File(cacheDirectory);
        if (!cacheDir.exists()) {
            // Give Error
            showErrorMessage("Caching directory is invalid", "The caching directory does not exists.\nCheck if " + cacheDir.getAbsolutePath() + " exists and contains data.");
            return;
        }

        // SHOW ERRORS
        if (hasErrors) {
            showErrorMessage("Input values are not valid", sb.toString());
            return;
        }

        // Check child folders
        File[] dirs = cacheDir.listFiles();
        boolean hasGreen = false, hasRain = false, hasTPC = false, hasLand = false, hasElevation = false;
        for (File f : dirs) {
            if (!f.isDirectory()) {
                continue;
            }
            if (f.getName().equalsIgnoreCase(Constants.OVERLAY_GREENNESS_CACHE_FOLDER)) {
                File xml = new File(f, Constants.OVERLAY_GREENNESS_CACHE_FOLDER + ".xml");
                if (xml.exists()) {
                    hasGreen = true;
                }
            } else if (f.getName().equalsIgnoreCase(Constants.OVERLAY_RAINFALL_CACHE_FOLDER)) {
                File xml = new File(f, Constants.OVERLAY_RAINFALL_CACHE_FOLDER + ".xml");
                if (xml.exists()) {
                    hasRain = true;
                }
            } else if (f.getName().equalsIgnoreCase(Constants.BACKGROUND_TPC_CACHE_FOLDER)) {
                File xml = new File(f, Constants.BACKGROUND_TPC_CACHE_FOLDER + ".xml");
                if (xml.exists()) {
                    hasTPC = true;
                }
            } else if (f.getName().equalsIgnoreCase(Constants.BACKGROUND_LANDSAT_CACHE_FOLDER)) {
                File xml = new File(f, Constants.BACKGROUND_LANDSAT_CACHE_FOLDER + ".xml");
                if (xml.exists()) {
                    hasLand = true;
                }
            } else if (f.getName().equalsIgnoreCase(Constants.ELEVATION_LAYER_CACHE_FOLDER)) {
                File xml = new File(f, Constants.ELEVATION_LAYER_CACHE_FOLDER + ".xml");
                if (xml.exists()) {
                    hasElevation = true;
                }
            } else {
                // the data found is not ok
                System.out.println("Unknown file: " + f.getName());
            }
        }
        if (!hasGreen && !hasRain && !hasTPC && !hasLand && !hasElevation) {
            showErrorMessage("No tiles are created!", "First import your files");
            return;
        } else {
            if ((!hasGreen && !hasRain) || (!hasTPC && !hasLand) || !hasElevation) {

                Object[] mDialogOptions = { "Yes", "No" };
                int userOptionResult = JOptionPane.showOptionDialog(this, "Not all tiles are created!\nDo you want to create package anyway?", "Create package",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, mDialogOptions, mDialogOptions[1]);
                if (userOptionResult != JOptionPane.NO_OPTION && userOptionResult != JOptionPane.YES_OPTION) {
                    System.out.println("User wants to do nothing");
                    return;
                }
                // YES_OPTION is "Re-Download", so the customOverlay folder needs to be deleted
                if (userOptionResult == JOptionPane.NO_OPTION) {
                    System.out.println("User cancel operation");
                    return;
                }
                // NO_OPTION is "Append", so continue the normal processing doing nothing else...
            }
        }

        // if you have both landsat and custom, create package
        progressMonitor = new ProgressMonitor(GUIClass.this, "Creating Package...", "Creating zip package...", 0, 100);
        progressMonitor.setProgress(0);
        progressMonitor.setMillisToDecideToPopup(10);
        progressMonitor.setMillisToPopup(15);
        // start swingWorker
        zipPackageWorker = new ZipDirectorySwingWorker(cacheDir, ZIP_FILE_NAME);
        zipPackageWorker.addPropertyChangeListener(zipPackagePropListener);
        zipPackageWorker.execute();
        toggleAllComponentsEnablement(false);

        // if you have both landsat and custom, create package
        // FileUtils.createZipFile(cacheDir, projectCacheDirName);
    }

    PropertyChangeListener zipPackagePropListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress" == evt.getPropertyName()) {
                int progress = (Integer) evt.getNewValue();
                // double ratio = 100.0 / (double) mTotalTilesNumber;
                progressMonitor.setProgress((int) (progress));
                String message = String.format("Creating zip package... %d%%", progress);
                progressMonitor.setNote(message);
                if (progressMonitor.isCanceled() || zipPackageWorker.isDone()) {
                    toggleAllComponentsEnablement(true);
                    if (progressMonitor.isCanceled()) {
                        zipPackageWorker.cancel(true);
                    } else {
                        // do nothing
                    }
                }
            }
            if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.DONE == evt.getNewValue()) {
                try {
                    boolean success = zipPackageWorker.get();
                    System.out.println("[PropertyChangeListener] Task completed.");
                    Toolkit.getDefaultToolkit().beep();
                    // work finished, now if finished not correctly, report error
                    if (!success) {
                        showErrorMessage("Zip operation failed", "Error while zipping tiles, the operation did not complete successfully.");
                    }
                } catch (CancellationException ce) {
                    System.out.println("Task cancelled by the user, cancellation Exception raised: " + ce.getMessage());
                } catch (InterruptedException e) {
                    System.out.println("Task cancelled by the user: " + e.getMessage());
                } catch (ExecutionException e) {
                    System.out.println("Excecution Exception to Worker: " + e.getMessage());
                }
            }
        }
    };

    // -------------- PROPERTIES METHOD -------------------------------
    private void setValuesFromProperties() {
        PropertiesUtils.assignValueFromProperties(PropertiesConstants.PROPERTY_MIN_LAT, minLatitude);
        PropertiesUtils.assignValueFromProperties(PropertiesConstants.PROPERTY_MAX_LAT, maxLatitude);
        PropertiesUtils.assignValueFromProperties(PropertiesConstants.PROPERTY_MIN_LON, minLongitude);
        PropertiesUtils.assignValueFromProperties(PropertiesConstants.PROPERTY_MAX_LON, maxLongitude);
        PropertiesUtils.assignValueFromProperties(PropertiesConstants.PROPERTY_TILES_DIRECTORY, subDirectoryTF);

        PropertiesUtils.assignDirPathFromProperties(PropertiesConstants.PROPERTY_GREENNESS_DIRECTORY, greennessfileChooser);
        PropertiesUtils.assignDirPathFromProperties(PropertiesConstants.PROPERTY_RAINFALL_DIRECTORY, rainfallfileChooser);
        PropertiesUtils.assignDirPathFromProperties(PropertiesConstants.PROPERTY_TPC_DIRECTORY, tpcfileChooser);
        PropertiesUtils.assignDirPathFromProperties(PropertiesConstants.PROPERTY_LANDSAT_DIRECTORY, landsatfileChooser);
        PropertiesUtils.assignDirPathFromProperties(PropertiesConstants.PROPERTY_ELEVATION_DIRECTORY, elevationfileChooser);
        PropertiesUtils.assignDirPathFromProperties(PropertiesConstants.PROPERTY_BOUNDARIES_DIRECTORY, boundariesfileChooser);

        PropertiesUtils.assignListFilesFromProperties(PropertiesConstants.PROPERTY_GREENNESS_FILES, greennessfileChooser, greennessListFiles, greennessfiles);
        PropertiesUtils.assignListFilesFromProperties(PropertiesConstants.PROPERTY_RAINFALL_FILES, rainfallfileChooser, rainfallListFiles, rainfallfiles);
        PropertiesUtils.assignListFilesFromProperties(PropertiesConstants.PROPERTY_TPC_FILES, tpcfileChooser, tpcListFiles, tpcfiles);
        PropertiesUtils.assignListFilesFromProperties(PropertiesConstants.PROPERTY_LANDSAT_FILES, landsatfileChooser, landsatListFiles, landsatfiles);
        PropertiesUtils.assignListFilesFromProperties(PropertiesConstants.PROPERTY_ELEVATION_FILES, elevationfileChooser, elevationListFiles, elevationfiles);
        PropertiesUtils.assignListFilesBoundariesFromProperties(PropertiesConstants.PROPERTY_BOUNDARIES_FILES, boundariesfileChooser, boundariesListFiles);
    }

    private void refreshProperties() {
        PropertiesManager.setStringProperty(PropertiesConstants.PROPERTY_MIN_LAT, minLatitude.getText());
        PropertiesManager.setStringProperty(PropertiesConstants.PROPERTY_MAX_LAT, maxLatitude.getText());
        PropertiesManager.setStringProperty(PropertiesConstants.PROPERTY_MIN_LON, minLongitude.getText());
        PropertiesManager.setStringProperty(PropertiesConstants.PROPERTY_MAX_LON, maxLongitude.getText());
        PropertiesManager.setStringProperty(PropertiesConstants.PROPERTY_TILES_DIRECTORY, subDirectoryTF.getText());
    }

    // -------------- VALIDATE INPUTS --------------------------------
    private boolean validateCoordinates(StringBuilder sb, Double minLatitude, Double minLongitude, Double maxLatitude, Double maxLongitude) {
        boolean hasErrors = false;
        if (null == minLatitude || minLatitude < -90 || minLatitude > 90) {
            hasErrors = true;
            sb.append(" - The minimum Latitude is not valid. You should provide a value between -90 and +90.\n");
        }
        // Get minimum Longitude
        if (null == minLongitude || minLongitude < -180 || minLongitude > 180) {
            hasErrors = true;
            sb.append(" - The minimum Longitude is not valid. You should provide a value between -180 and +180.\n");
        }
        // get Max Latitude
        if (null == maxLatitude || maxLatitude < -90 || maxLatitude > 90) {
            hasErrors = true;
            sb.append(" - The maximum Latitude is not valid. You should provide a value between -90 and +90.\n");
        }
        // get max Longitude
        if (null == maxLongitude || maxLongitude < -180 || maxLongitude > 180) {
            hasErrors = true;
            sb.append(" - The maximum Longitude is not valid. You should provide a value between -180 and +180.\n");
        }
        // If everything else is ok, check the validity of coordinates
        if (!hasErrors) {
            if (minLatitude >= maxLatitude || minLongitude >= maxLongitude) {
                hasErrors = true;
                sb.append(" - The combination of min-max Latitude/Longitude is not valid. Double check that minimum values are lower than maximum ones.\n");
            }
        }
        return hasErrors;
    }

    private Sector pickBounding() {
        try {
            StringBuilder sb = new StringBuilder();
            Double minLat = Double.valueOf(minLatitude.getText());
            Double minLon = Double.valueOf(minLongitude.getText());
            Double maxLat = Double.valueOf(maxLatitude.getText());
            Double maxLon = Double.valueOf(maxLongitude.getText());

            if (!validateCoordinates(sb, minLat, minLon, maxLat, maxLon)) {
                return Sector.fromDegrees(minLat, maxLat, minLon, maxLon);
            }

        } catch (Exception e) {

        }
        return null;
    }

    // -------------- ERRORS MESSAGES AND POPUPS -----------------------
    public void showErrorMessage(String title, String error) {
        JOptionPane.showMessageDialog(this, error, title, JOptionPane.ERROR_MESSAGE);
    }

    // ------------- MAIN
    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String args[]) {
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // handle exceptione
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUIClass().setVisible(true);
            }
        });
    }

}
