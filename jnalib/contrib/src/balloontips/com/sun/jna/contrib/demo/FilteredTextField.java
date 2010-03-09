package com.sun.jna.contrib.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Popup;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
//TODO: FilteredTextField - Comment this class.
/**
 * The FilteredTextField class is a JTextField that only allows specified
 * characters to be entered into it.  The allowed characters can be added to
 * the text field, and entry validation is performed as each character is
 * typed.  In addition, complete string validation is tested against a
 * configurable regular expression when leaving the field.  If the string is
 * invalid the text field is bordered with a red line, and the user is notified
 * of the error upon returning to the text field.  The text field can also be
 * configured to accept a limited number of characters.
 */
@SuppressWarnings("serial")
public class FilteredTextField extends JTextField {
  public static final Character[] UPPERCASE_CHARS = {'A', 'B', 'C', 'D', 'E',
    'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
    'U', 'V', 'W', 'X', 'Y', 'Z'};
  public static final Character[] LOWERCASE_CHARS = {'a', 'b', 'c', 'd', 'e',
    'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
    'u', 'v', 'w', 'x', 'y', 'z'};
  public static final Character[] NUMERIC_CHARS = {'1', '2', '3', '4', '5',
    '6', '7', '8', '9', '0'};
  
  private static final Integer ENTRY_BALLOON = 0;
  private static final Integer VALID_BALLOON = 1;
  private static final Integer LENGTH_BALLOON = 2;
  private static final Border RED_BORDER =
    BorderFactory.createLineBorder(Color.RED, 2);
  
  private ArrayList<Character> allowable = new ArrayList<Character>();
  private int maximumLength = String.valueOf(Long.MAX_VALUE).length();
  
  private Border defaultBorder = null;
  private boolean isValid = true;
  
  private Popup balloon = null;
  private String entryError = null;
  private String validRegex = null;
  private String validError = null;
  private Color balloonBorderColor = null;
  private Color balloonBackgroundColor = null;
  private Color balloonTextColor = null;
  private Integer balloonDuration = null;
  
  private Integer balloonType = null;
  
  /**
   * Create a FilteredTextField.
   */
  public FilteredTextField () {
    super();
    init();
  }
  
  /**
   * Create a FilteredTextField.
   * @param columns the number of columns to use to calculate the preferred
   * width
   */
  public FilteredTextField (int columns) {
    super(columns);
    init();
  }
  
  /*
   * Initialize the FilteredTextField.
   */
  private void init () {
    defaultBorder = getBorder();
    entryError = "";
    validRegex = "";
    validError = "";
    balloonBorderColor = BalloonTipManager.DEFAULT_BORDER_COLOR;
    balloonBackgroundColor = BalloonTipManager.DEFAULT_BACKGROUND_COLOR;
    balloonTextColor = BalloonTipManager.DEFAULT_TEXT_COLOR;
    balloonDuration = 10000;
    balloonType = ENTRY_BALLOON;
    addFocusListener(new ValidationEar());
  }
  
  /**
   * Sets the allowable character used for entry validation.
   * @param characters the allowable characters
   */
  public void setCharacters (Character[] characters) {
    clearCharacters();
    for (int i = 0; i < characters.length; i++) {
      addCharacter(characters[i]);
    }
  }
  
  /**
   * Adds the character array to the list used for entry validation.
   * @param characters the character array
   */
  public void addCharacters (Character[] characters) {
    for (int i = 0; i < characters.length; i++) {
      addCharacter(characters[i]);
    }
  }
  
  /**
   * Adds the character to the list used for entry validation.
   * @param characters the character
   */
  public void addCharacter (Character character) {
    if (!allowable.contains(character)) {
      allowable.add(character);
    }
  }
  
  /**
   * Clears the list of allowable characters for entry validation.
   */
  public void clearCharacters () {
    allowable.clear();
  }
  
  /**
   * Removes the character array from the list used for entry validation.
   * @param characters the character array
   */
  public void removeCharacters (Character[] characters) {
    for (int i = 0; i < characters.length; i++) {
      removeCharacter(characters[i]);
    }
  }
  
  /**
   * Removes the character from the list used for entry validation.
   * @param character the character
   */
  public void removeCharacter (Character character) {
    if (allowable.contains(character)) {
      allowable.remove(character);
    }
  }
  
  /**
   * Sets the maximum number of characters for the length of the entry string.
   * @param maximumLength the number of characters
   */
  public void setMaximumLength (int maximumLength) {
    this.maximumLength = maximumLength;
  }
  
  /**
   * Sets the message that is displayed when there is an entry error.
   * @param entryError the entry error message
   */
  public void setEntryError (String entryError) {
    this.entryError = entryError;
  }
  
  /**
   * Sets the regular expression that is used for string validation.  String
   * validation is checked when exiting the text field.
   * @param validRegex the validation regular expression
   */
  public void setValidRegex (String validRegex) {
    this.validRegex = validRegex;
  }
  
  /**
   * Sets the message that is displayed when there is a validation error.
   * @param validError the validation error message
   */
  public void setValidError (String validError) {
    this.validError = validError;
  }
  
  /**
   * Sets the color to use for the balloon border.
   * @param borderColor the balloon border color
   */
  public void setBalloonBorderColor (Color borderColor) {
    balloonBorderColor = borderColor;
  }
  
  /**
   * Sets the color to use for the balloon background.
   * @param backgroundColor the balloon background color
   */
  public void setBalloonBackgroundColor (Color backgroundColor) {
    balloonBackgroundColor = backgroundColor;
  }
  
  /**
   * Sets the color to use for the balloon text.
   * @param textColor the balloon text color
   */
  public void setBalloonTextColor (Color textColor) {
    balloonTextColor = textColor;
  }
  
  /**
   * Sets the time in milliseconds that the balloon is visible before
   * disappearing.  This is the maximum time that the balloon will be visible,
   * as other events can also make the balloon disappear.
   * @param duration the time in milliseconds
   */
  public void setBalloonDuration (Integer duration) {
    balloonDuration = duration;
  }
  
  /*
   * (non-Javadoc)
   * @see javax.swing.JTextField#createDefaultModel()
   */
  protected Document createDefaultModel () {
    return new FilteredTextFieldDocument();
  }
  
  /*
   * This class defines the document used for the FilteredTextField.
   */
  private class FilteredTextFieldDocument extends PlainDocument {
    /*
     * Create a FilteredTextFieldDocument.
     */
    public FilteredTextFieldDocument () {
      addDocumentListener(new FilteredTextFieldEar());
    }
    
    /*
     * (non-Javadoc)
     * @see javax.swing.text.PlainDocument#insertString(
     * int, java.lang.String, javax.swing.text.AttributeSet)
     */
    public void insertString (int offset, String str, AttributeSet a)
        throws BadLocationException
    {
      if (balloon != null && BalloonTipManager.isShowing()) {
        if (balloonType == VALID_BALLOON) {
          balloon.hide();
        }
      }
      StringBuffer buffer =
        new StringBuffer(FilteredTextField.this.getText());
      if (offset >= 0 && offset <= buffer.length()) {
        buffer.insert(offset, str);
        String strBuf = buffer.toString();
        
        if (buffer.length() > maximumLength) {
          if (balloon != null && BalloonTipManager.isShowing()) {
            if (balloonType == LENGTH_BALLOON) {
              BalloonTipManager.restartTimer();
              return;
            }
            else {
              balloon.hide();
            }
          }
          balloon = BalloonTipManager.getBalloonTip(FilteredTextField.this,
            "The number of characters must be less than or equal to " +
              maximumLength, 0, 0, balloonDuration, balloonBorderColor,
              balloonBackgroundColor, balloonTextColor);
          balloon.show();
          balloonType = LENGTH_BALLOON;
          return;
        }
        
        if (strBuf == null || strBuf.equals("")) {
          remove(0, getLength());
          super.insertString(0, "", null);
          if (balloon != null && BalloonTipManager.isShowing()) {
            balloon.hide();
          }
          return;
        }

        if (allowable.contains(str.charAt(0))) {
          super.insertString(offset, str, a);
          if (balloon != null && BalloonTipManager.isShowing()) {
            balloon.hide();
          }
        }
        else {
          if (balloon != null && BalloonTipManager.isShowing()) {
            if (balloonType == ENTRY_BALLOON) {
              BalloonTipManager.restartTimer();
              return;
            }
            else {
              balloon.hide();
            }
          }
          balloon = BalloonTipManager.getBalloonTip(FilteredTextField.this,
            entryError, 0, 0, balloonDuration, balloonBorderColor,
            balloonBackgroundColor, balloonTextColor);
          balloon.show();
          balloonType = ENTRY_BALLOON;
        }
      }
    }
    
    /*
     * This listener class is needed to catch character removal events.
     */
    private class FilteredTextFieldEar implements DocumentListener {
      /*
       * (non-Javadoc)
       * @see javax.swing.event.DocumentListener#insertUpdate(
       * javax.swing.event.DocumentEvent)
       */
      public void insertUpdate (DocumentEvent e) {/* N/A */}
      
      /*
       * (non-Javadoc)
       * @see javax.swing.event.DocumentListener#removeUpdate(
       * javax.swing.event.DocumentEvent)
       */
      public void removeUpdate (DocumentEvent e) {
        if (balloon != null && BalloonTipManager.isShowing())
        {
          balloon.hide();
        }
      }
      
      /*
       * (non-Javadoc)
       * @see javax.swing.event.DocumentListener#changedUpdate(
       * javax.swing.event.DocumentEvent)
       */
      public void changedUpdate (DocumentEvent e) {/* N/A */}
    }
  }

  /*
   * This listener class is used to determine whether the string is valid based
   * on a regular expression.  The validation is tested when leaving the text
   * field, and notification is performed when returning to the text field.
   */
  private class ValidationEar extends FocusAdapter {
    /*
     * (non-Javadoc)
     * @see java.awt.event.FocusAdapter#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost (FocusEvent e) {
      String entered = getText().trim();
      if (!entered.matches(validRegex)) {
        if (balloon != null) {
          balloon.hide();
        }
        setBorder(
          BorderFactory.createCompoundBorder(RED_BORDER, defaultBorder));
        isValid = false;
      }
      else {
        setBorder(defaultBorder);
        isValid = true;
      }
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.event.FocusAdapter#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained (FocusEvent e) {
      if (!isValid) {
        balloon = BalloonTipManager.getBalloonTip(FilteredTextField.this,
          validError, 0, 0, balloonDuration, balloonBorderColor,
          balloonBackgroundColor, balloonTextColor);
        balloon.show();
        balloonType = VALID_BALLOON;
      }
    }
  }

  /*
   * A main entry point to test the FilteredTextField.
   * @param args application arguments
   */
  public static void main (String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    JFrame jframe = new JFrame("FilteredTextField Test");
    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jframe.setSize(400, 75);
    jframe.setLocation(400, 400);
    JPanel jpanel = new JPanel();
    jpanel.setLayout(new BorderLayout());
    FilteredTextField ftfield = new FilteredTextField(10);
    ftfield.setCharacters(LOWERCASE_CHARS);
    ftfield.addCharacter('-');
    ftfield.addCharacter('_');
    ftfield.addCharacter(' ');
    ftfield.setMaximumLength(10);
    ftfield.setEntryError(
      "Only lower case letters, hyphens, underscores, and spaces allowed.");
    ftfield.setValidRegex("^a+[a-z-_ ]*");
    ftfield.setValidError("The string must begin with the letter 'a'.");
    jpanel.add(ftfield, BorderLayout.CENTER);
    jpanel.add(new FilteredTextField(10), BorderLayout.SOUTH);
    jframe.getContentPane().add(jpanel);
    jframe.setVisible(true);
  }
}
