package com.sun.jna.examples;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.Transparency;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Experiment with different compositing methods. Unfortunately you
 * can't use anything other than AlphaComposite on Graphics derived from
 * screen surface data.
 * 
 * @author twall
 */
public class X11AlphaMaskTest {
    private static int alpha = 128;
    private static AlphaComposite composite = AlphaComposite.Src;

    public static void main(String[] args) {
        GraphicsConfiguration gc = 
            WindowUtils.getAlphaCompatibleGraphicsConfiguration();
        JFrame frame = new JFrame("X11 alpha test", gc);
        final JComponent content = new JComponent() {
            public Dimension getPreferredSize() {
                return new Dimension(100, 100);
            }

            private Color mix(Color c, int base) {
                float f = (float)alpha / 255;
                return new Color((int)(c.getRed() * f + base * (1 - f)),
                                 (int)(c.getGreen() * f + base * (1 - f)),
                                 (int)(c.getBlue() * f + base * (1 - f)));
            }

            protected void paintComponent(Graphics graphics) {
                BufferedImage buf =
                    ((Graphics2D)graphics).getDeviceConfiguration().
                    createCompatibleImage(getWidth(), 
                                          getHeight(),
                                          Transparency.TRANSLUCENT);
                Graphics2D g = buf.createGraphics();
                g.setComposite(AlphaComposite.Clear);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setComposite(AlphaComposite.SrcOver);
                int barw = getWidth() / 10;
                int barh = getHeight();

                g.setColor(Color.red);
                g.fillRect(0, 0, barw, barh);
                g.setColor(new Color(255, 0, 0, alpha));
                g.fillRect(barw, 0, barw, barh / 2);
                g.setColor(mix(Color.red, 255));
                g.fillRect(barw, barh / 2, barw, barh / 4);
                g.setColor(mix(Color.red, 0));
                g.fillRect(barw, barh *3 / 4, barw, barh / 4);

                g.setColor(Color.green);
                g.fillRect(2 * barw, 0, barw, barh);
                g.setColor(new Color(0, 255, 0, alpha));
                g.fillRect(3 * barw, 0, barw, barh / 2);
                g.setColor(mix(Color.green, 255));
                g.fillRect(3 * barw, barh / 2, barw, barh / 4);
                g.setColor(mix(Color.green, 0));
                g.fillRect(3 * barw, barh *3 / 4, barw, barh / 4);

                g.setColor(Color.blue);
                g.fillRect(4 * barw, 0, barw, barh);
                g.setColor(new Color(0, 0, 255, alpha));
                g.fillRect(5 * barw, 0, barw, barh / 2);
                g.setColor(mix(Color.blue, 255));
                g.fillRect(5 * barw, barh / 2, barw, barh / 4);
                g.setColor(mix(Color.blue, 0));
                g.fillRect(5 * barw, barh * 3 / 4, barw, barh / 4);

                g.setColor(Color.white);
                g.fillRect(6 * barw, 0, barw, barh);
                g.setColor(new Color(255, 255, 255, alpha));
                g.fillRect(7 * barw, 0, barw, barh / 2);
                g.setColor(mix(Color.white, 255));
                g.fillRect(7 * barw, barh / 2, barw, barh / 4);
                g.setColor(mix(Color.white, 0));
                g.fillRect(7 * barw, barh * 3 / 4, barw, barh / 4);

                g.setColor(Color.black);
                g.fillRect(8 * barw, 0, barw, barh);
                g.setColor(new Color(0, 0, 0, alpha));
                g.fillRect(9 * barw, 0, barw, barh / 2);
                g.setColor(mix(Color.black, 255));
                g.fillRect(9 * barw, barh / 2, barw, barh / 4);
                g.setColor(mix(Color.black, 0));
                g.fillRect(9 * barw, barh * 3 / 4, barw, barh / 4);

                // small bar on top, black 50% alpha
                g.setColor(new Color(0, 0, 0, 128));
                g.fillRect(0, 0, getWidth(), barh / 10);

                if (false) {
                    BufferedImage prealpha =
                        new BufferedImage(getWidth(), getHeight(), 
                                          BufferedImage.TYPE_INT_ARGB_PRE);
                    g = prealpha.createGraphics();
                    g.setComposite(AlphaComposite.Src);
                    g.drawImage(buf, 0, 0, getWidth(), getHeight(), null);
                    
                    g = (Graphics2D)graphics.create();
                    g.setComposite(composite);
                    g.drawImage(prealpha, 0, 0, getWidth(), getHeight(), null);
                }
                else {
                    g = (Graphics2D)graphics.create();
                    g.setComposite(composite);
                    g.drawImage(buf, 0, 0, getWidth(), getHeight(), null);
                }
                g.dispose();
            }
        };
        final JSlider slider = new JSlider(0, 255, 128);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (!slider.getModel().getValueIsAdjusting()) {
                    alpha = slider.getValue();
                    content.repaint();
                }
            }
        });
        AlphaComposite[] options = {
            AlphaComposite.Clear,
            AlphaComposite.Src,
            AlphaComposite.SrcOver,
            AlphaComposite.SrcAtop,
            AlphaComposite.SrcIn,
            AlphaComposite.SrcOut,
            AlphaComposite.Dst,
            AlphaComposite.DstOver,
            AlphaComposite.DstAtop,
            AlphaComposite.DstIn,
            AlphaComposite.DstOut,
            AlphaComposite.Xor,
        };
        final JComboBox combo = new JComboBox(options);
        combo.setOpaque(true);
        combo.setSelectedItem(composite);
        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                composite = (AlphaComposite)combo.getSelectedItem();
                content.repaint();
            }
        });
        
        frame.getContentPane().add(content);
        frame.getContentPane().add(slider, BorderLayout.SOUTH);
        frame.getContentPane().add(combo, BorderLayout.NORTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        if (System.getProperty("os.name").startsWith("Windows"))
            WindowUtils.setWindowTransparent(frame, true);
        frame.setLocation(100, 100);
        frame.setVisible(true);
    }
}
