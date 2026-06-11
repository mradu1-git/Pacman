import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Pac Man");

        Pacman pacmanGame = new Pacman();
        frame.add(pacmanGame);

        frame.setResizable(false);
        frame.pack(); // <--- Add this instead of frame.setSize()

        frame.setLocationRelativeTo(null); // Keep this AFTER pack() so it centers correctly
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
