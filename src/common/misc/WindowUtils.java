/**
 * @author Petr (http://www.sallyx.org/)
 */
package common.misc;
import Raven.Main;
import Raven.Script1.MyMenuBar;
import common.D2.Vector2D;
import static common.D2.Vector2D.POINTtoVector;
import static common.windows.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class WindowUtils {
    //JFrame + Menu

    public static class Window extends JDialog {

        private JMenuBar menu;
        private JPanel panel;

        public Window(String title) {
            super((JFrame) null, title);
            this.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        }

        public MyMenuBar getMenu() {
            JMenuBar bar = this.getJMenuBar();
            if (bar == null) {
                return null;
            }
            return (MyMenuBar) bar;
        }

        @Override
        public void setJMenuBar(JMenuBar menu) {
            assert (menu instanceof MyMenuBar);
            super.setJMenuBar(menu);
        }

        public void addButtons(JComponent hwndToolBar) {
            this.add(hwndToolBar, BorderLayout.SOUTH);
        }
        
        public void addPanel(JPanel comp) {
            super.add(comp);
            panel = comp;
        }
        
        public JPanel getPanel() {
            return panel;
        }
    }

    /**
     *  Changes the state of a menu item given the item identifier, the 
     *  desired state and the HWND of the menu owner
     */
    public static void ChangeMenuState(MyMenuBar hwnd, int MenuItem, long state) {
        //hwnd.setMenuState(MenuItem,state);
        hwnd.setMenuState(MenuItem, state);
    }

    /**
     * Instead of SendMessage(hwnd, WM_COMMAND, MenuItem, NULL);
     */
    public static void SendChangeMenuMessage(MyMenuBar hwnd, int MenuItem) {
        hwnd.changeMenuState(MenuItem);
    }

    /**
     * if b is true MenuItem is checked, otherwise it is unchecked
     */
    public static void CheckMenuItemAppropriately(MyMenuBar hwnd, int MenuItem, boolean b) {
        if (b) {
            ChangeMenuState(hwnd, MenuItem, MFS_CHECKED);
        } else {
            ChangeMenuState(hwnd, MenuItem, MFS_UNCHECKED);
        }
    }

    /**
     *  this is a replacement for the StringCchLength function found in the 
     *  platform SDK. See MSDN for details. Only ever used for checking toolbar
     *  strings
     */
    public static boolean CheckBufferLength(String buff, int MaxLength, int BufferLength) {
        return true;
    }

    public static void ErrorBox(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    //gets the coordinates of the cursor relative to an active window 
    public static Vector2D GetClientCursorPosition()
    {
        POINT MousePos = new POINT();

        CppToJava.mouseCache.GetCursorPos(MousePos);
     
        //ScreenToClient(GetActiveWindow(), &MousePos);

        return POINTtoVector(MousePos);
    }

    public static class IcoCheckBox extends JCheckBox {

        private int idCommand = -1;

        public IcoCheckBox(Image imageIcon, final int idCommand) {
            super(new CheckBoxIcon(imageIcon));
            CheckBoxIcon ico = (CheckBoxIcon) this.getIcon();
            ico.addObserver(this);
            this.idCommand = idCommand;

            this.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    Main.HandleMenuItems(idCommand, null);
                }
            });
        }
        
        
        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)  {
            this.repaint();
            return true;
        }
        
        @Override
        public Dimension getMaximumSize() {
            return new Dimension(24,24);
        }
    }

    static class CheckBoxIcon implements Icon {
        Image ico;
        
        CheckBoxIcon(Image ico) {
            this.ico = ico;
        }
        
        public void addObserver(ImageObserver o) {
            this.ico.getWidth(o);
        }
        
        @Override
        public void paintIcon(Component component, Graphics g, int x, int y) {
            AbstractButton abstractButton = (AbstractButton) component;
            ButtonModel buttonModel = abstractButton.getModel();

            if(buttonModel.isSelected()) {
                g.setColor(new Color(69,69,69));
                g.drawImage(ico, 5, 5, null);
                g.drawLine(0, 0, 23, 0);
                g.drawLine(0, 0, 0, 23);
                g.setColor(new Color(0xa0,0xa0,0xa0));
                g.drawLine(1,1,23,1);
                g.drawLine(1,1,1,22);
            } else {
                g.setColor(new Color(69,69,69));
                g.drawImage(ico, 4, 4, null);
                g.drawLine(24, 0, 24, 23);
                g.drawLine(24, 23, 1, 23);
                g.setColor(new Color(0xa0,0xa0,0xa0));
                g.drawLine(23,1,23,22);
                g.drawLine(22,22,2,22);
            
            }
        }

        @Override
        public int getIconWidth() {
            return 24;
        }

        @Override
        public int getIconHeight() {
            return 24;
        }
    }
    
    public static boolean IS_KEY_PRESSED(int key) {
        return CppToJava.KEYDOWN(key);
    }
    
}