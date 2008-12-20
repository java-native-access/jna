/* Copyright (c) 2008 Stefan Endrullis, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package jnacontrib.x11.demos;

import jnacontrib.x11.api.X;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Demonstrates some possibilities on your X Window System.
 *
 * @author Stefan Endrullis
 */
public class XDesktopDemo extends JFrame {
    public static void main(String[] args) throws X.X11Exception {
        new XDesktopDemo();
    }

    private X.Display display = new X.Display();
    private JList desktopList;
    private JTable windowTable;
    private JButton refreshButton;
    private JButton moveWindowToDesktopButton;
    private JButton goToDesktopButton;
    private JButton moveWindowAndGoToDesktopButton;
    private JButton closeWindowButton;
    private JButton goToWindowButton;
    private JButton showDesktop;
    
    public XDesktopDemo() throws X.X11Exception {
        super("XDesktopDemo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initGui();

        refreshDesktopsAndWindows();

        pack();
        addListeners();
        setVisible(true);

        printWmInfo();
    }

    private void printWmInfo() throws X.X11Exception {
        X.Window wm = display.getWindowManagerInfo();
        System.out.println("wm.getTitle() = " + wm.getTitle());
        System.out.println("wm.getWindowClass() = " + wm.getWindowClass());
        System.out.println("wm.getPID() = " + wm.getPID());
    }

    private void addListeners() {
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    refreshDesktopsAndWindows();
                } catch (X.X11Exception e) {
                    e.printStackTrace();
                }
            }
        });
        goToDesktopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int desktopNr = desktopList.getSelectedIndex();
                if (desktopNr >= 0) {
                    try {
                        display.switchDesktop(desktopNr);
                        display.flush();
                    } catch (X.X11Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        goToWindowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                X.Window window = getSelectedWindow();
                try {
                    window.activate();
                    display.flush();
                } catch (X.X11Exception e) {
                    e.printStackTrace();
                }
            }
        });
        showDesktop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    display.showingDesktop(true);
                    display.flush();
                } catch (X.X11Exception e) {
                    e.printStackTrace();
                }
            }
        });
        moveWindowToDesktopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                X.Window window = getSelectedWindow();
                try {
                    window.moveToDesktop(desktopList.getSelectedIndex());
                    display.flush();
                } catch (X.X11Exception e) {
                    e.printStackTrace();
                }
            }
        });
        moveWindowAndGoToDesktopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                X.Window window = getSelectedWindow();
                try {
                    window.moveToDesktop(desktopList.getSelectedIndex());
                    window.activate();
                    display.flush();
                } catch (X.X11Exception e) {
                    e.printStackTrace();
                }
            }
        });
        closeWindowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                X.Window window = getSelectedWindow();
                try {
                    window.close();
                    display.flush();
                } catch (X.X11Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private X.Window getSelectedWindow() {
        WindowTableModel tableModel = (WindowTableModel) windowTable.getModel();
        return tableModel.getWindow(windowTable.getSelectedRow());
    }

    private void refreshDesktopsAndWindows() throws X.X11Exception {
        // update desktop list
        X.Desktop[] desktops = display.getDesktops();
        ArrayList list = new ArrayList(desktops.length);
        for (int i = 0; i < desktops.length; i++) {
            list.add(desktops[i].name);
        }
        desktopList.clearSelection();
        desktopList.setModel(new SimpleListModel(list));

        // select active desktop
        int activeDesktop = display.getActiveDesktopNumber();
        desktopList.setSelectedIndex(activeDesktop);

        // update window list
        int activeWindowId = display.getActiveWindow().getID();
        int activeWindowNumber = -1;
        X.Window[] windows = display.getWindows();
        String[] head = new String[]{
            "ID", "Desktop", "Title",
            "X", "Y", "Width", "Height"
        };
        String[][] data = new String[windows.length][head.length];
        for (int i = 0; i < windows.length; i++) {
            X.Window window = windows[i];
            X.Window.Geometry geo = window.getGeometry();
            int windowId = window.getID();
            data[i][0] = String.format("0x%08X", new Object[]{new Integer(windowId)});
            data[i][1] = "" + window.getDesktop();
            data[i][2] = window.getTitle();
            data[i][3] = "" + geo.x;
            data[i][4] = "" + geo.y;
            data[i][5] = "" + geo.width;
            data[i][6] = "" + geo.height;
            if (windowId == activeWindowId) {
                activeWindowNumber = i;
            }
        }
        windowTable.setModel(new WindowTableModel(head, data, windows));
        if (activeWindowNumber >= 0) {
            windowTable.getSelectionModel().setSelectionInterval(activeWindowNumber, activeWindowNumber);
        }
    }

    private void initGui() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(panel1, gbc);
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Desktops"));
        desktopList = new JList();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(desktopList, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(panel2, gbc);
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Windows"));
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(scrollPane1, gbc);
        windowTable = new JTable();
        windowTable.setEnabled(true);
        scrollPane1.setViewportView(windowTable);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(panel3, gbc);
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Actions"));
        goToDesktopButton = new JButton();
        goToDesktopButton.setText("go to desktop");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(goToDesktopButton, gbc);
        refreshButton = new JButton();
        refreshButton.setText("refresh");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(refreshButton, gbc);
        goToWindowButton = new JButton();
        goToWindowButton.setText("go to window");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(goToWindowButton, gbc);
        moveWindowAndGoToDesktopButton = new JButton();
        moveWindowAndGoToDesktopButton.setText("move window and go to desktop");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(moveWindowAndGoToDesktopButton, gbc);
        closeWindowButton = new JButton();
        closeWindowButton.setText("close window");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(closeWindowButton, gbc);
        moveWindowToDesktopButton = new JButton();
        moveWindowToDesktopButton.setText("move window to desktop");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(moveWindowToDesktopButton, gbc);
        showDesktop = new JButton();
        showDesktop.setText("show desktop");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(showDesktop, gbc);

        // more attributes
        desktopList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        windowTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setContentPane(mainPanel);
    }

    /**
     * A simple ListModel managing a list of objects.
     */
    public static class SimpleListModel extends AbstractListModel {
        private ArrayList list;

        public SimpleListModel(ArrayList list) {
            this.list = list;
        }

        public int getSize() { return list.size(); }
        public Object getElementAt(int i) { return list.get(i); }
    }

    /**
     * A simple TableModel managing an array of Strings.
     */
    public static class WindowTableModel implements TableModel {
        private String[] head;
        private String[][] data;
        private X.Window[] windows;

        public WindowTableModel(String[] head, String[][] data, X.Window[] windows) {
            this.head = head;
            this.data = data;
            this.windows = windows;
        }

        public int getRowCount() {
            return data.length;
        }
        public int getColumnCount() {
            return head.length;
        }
        public String getColumnName(int columnIndex) {
            return head[columnIndex];
        }
        public Class getColumnClass(int columnIndex) {
            return String.class;
        }
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        }
        public void addTableModelListener(TableModelListener l) {
        }
        public void removeTableModelListener(TableModelListener l) {
        }

        public X.Window getWindow(int rowIndex) {
            return windows[rowIndex];
        }
    }
}
