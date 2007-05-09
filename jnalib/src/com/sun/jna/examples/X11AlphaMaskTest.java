package com.sun.jna.examples;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Experiement with different compositing methods. Unfortunately you
 * can't use anything other than AlphaComposite on Graphics derived from
 * screen surface data.
 * 
 * @author twall
 */
public class X11AlphaMaskTest {
    private static int alpha = 128;

    public static void main(String[] args) {
        JFrame frame = new JFrame(
                                  "X11 alpha test",
                                  WindowUtils
                                             .getAlphaCompatibleGraphicsConfiguration());
        final JComponent content = new JComponent() {
            public Dimension getPreferredSize() {
                return new Dimension(100, 100);
            }

            private Color mix(Color c) {
                float f = (float)alpha / 255;
                return new Color((int)(c.getRed() * f + 255 * (1 - f)),
                                 (int)(c.getGreen() * f + 255 * (1 - f)),
                                 (int)(c.getBlue() * f + 255 * (1 - f)));
            }

            protected void paintComponent(Graphics graphics) {
                // if (!WindowUtils.doPaint) return;
                BufferedImage buf = new BufferedImage(
                                                      getWidth(),
                                                      getHeight(),
                                                      BufferedImage.TYPE_INT_ARGB);
                // Graphics2D g = (Graphics2D)graphics;
                Graphics2D g = buf.createGraphics();
                g.setComposite(AlphaComposite.Clear);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setComposite(AlphaComposite.SrcOver);
                int w = getWidth() / 10;
                int h = getHeight();
                g.setColor(Color.red);
                g.fillRect(0, 0, w, h);
                g.setColor(new Color(255, 0, 0, alpha));
                g.fillRect(w, 0, w, h / 2);
                g.setColor(mix(Color.red));
                g.fillRect(w, h / 2, w, h / 2);
                g.setColor(Color.green);
                g.fillRect(2 * w, 0, w, h);
                g.setColor(new Color(0, 255, 0, alpha));
                g.fillRect(3 * w, 0, w, h / 2);
                g.setColor(mix(Color.green));
                g.fillRect(3 * w, h / 2, w, h / 2);
                g.setColor(Color.blue);
                g.fillRect(4 * w, 0, w, h);
                g.setColor(new Color(0, 0, 255, alpha));
                g.fillRect(5 * w, 0, w, h / 2);
                g.setColor(mix(Color.blue));
                g.fillRect(5 * w, h / 2, w, h / 2);
                g.setColor(Color.white);
                g.fillRect(6 * w, 0, w, h);
                g.setColor(new Color(255, 255, 255, alpha));
                g.fillRect(7 * w, 0, w, h / 2);
                g.setColor(mix(Color.white));
                g.fillRect(7 * w, h / 2, w, h / 2);
                g.setColor(Color.black);
                g.fillRect(8 * w, 0, w, h);
                g.setColor(new Color(0, 0, 0, alpha));
                g.fillRect(9 * w, 0, w, h / 2);
                g.setColor(mix(Color.black));
                g.fillRect(9 * w, h / 2, w, h / 2);
                g.setColor(new Color(0, 0, 0, 128));
                g.fillRect(0, 0, getWidth(), h / 10);
                g = (Graphics2D)graphics.create();
                g.setComposite(AlphaComposite.Src);
                g.drawImage(buf, 0, 0, getWidth(), h, null);
                g.dispose();
            }
        };
        frame.getContentPane().add(content);
        final JSlider slider = new JSlider(0, 255, 128);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (!slider.getModel().getValueIsAdjusting()) {
                    alpha = slider.getValue();
                    content.repaint();
                }
            }
        });
        frame.getContentPane().add(slider, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // WindowUtils.setWindowTransparent(frame, true);
        frame.setVisible(true);
    }
}
