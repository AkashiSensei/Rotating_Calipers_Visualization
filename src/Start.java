import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;

/**
 * @author : AkashiSensei
 * &#064;date : 2022/12/30 16:02
 */
public class Start {
    static {
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println("!!! StyleCtrl : style set error :" + e);
        }
    }

    private static MainFrame mainFrame= new MainFrame();

    public static void main(String[] args) {
        mainFrame.setVisible(true);
    }

    public static void restart() {
        mainFrame.setVisible(false);
        mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }
}
