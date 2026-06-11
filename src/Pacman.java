import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.HashSet;
import java.util.Random;

public class Pacman extends JPanel implements ActionListener, KeyListener {
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {}

    @Override
    public void keyPressed(KeyEvent keyEvent) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        } else {
            pacman.updateDirection('L');
        }
        if (pacman.direction == 'U') {
            pacman.image = pacmanUpImage;
        } else if (pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        } else if (pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        } else {
            pacman.image = pacmanLeftImage;
        }
    }

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        public Block(int x, int y, int width, int height, Image image) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.image = image;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        private void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            } else if (this.direction == 'R') {
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            } else {
                this.velocityY = 0;
                this.velocityX = -tileSize/4;
            }
        }
        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }
    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for (Block ghost : ghosts) {
            if (collision(ghost, pacman)) {
                lives--;
                if (lives == 0) {
                    gameOver = true;
                }
                resetPositions();
            }
            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                    break;
                }
            }
        }
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);
    }

    private void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    private final int rowCount = 21;
    private final int colCount = 19;
    private final int tileSize = 32;
    private int boardWidth = tileSize * colCount;
    private int boardHeight = tileSize * rowCount;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image redGhostImage;
    private Image pinkGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;
    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;
    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    Pacman() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        wallImage = new ImageIcon((getClass().getResource("./Images/wall.png"))).getImage();
        blueGhostImage = new ImageIcon((getClass().getResource("./Images/blueGhost.png"))).getImage();
        orangeGhostImage = new ImageIcon((getClass().getResource("./Images/orangeGhost.png"))).getImage();
        redGhostImage = new ImageIcon((getClass().getResource("./Images/redGhost.png"))).getImage();
        pinkGhostImage = new ImageIcon((getClass().getResource("./Images/pinkGhost.png"))).getImage();
        pacmanUpImage = new ImageIcon((getClass().getResource("./Images/pacmanUp.png"))).getImage();
        pacmanDownImage = new ImageIcon((getClass().getResource("./Images/pacmanDown.png"))).getImage();
        pacmanLeftImage = new ImageIcon((getClass().getResource("./Images/pacmanLeft.png"))).getImage();
        pacmanRightImage = new ImageIcon((getClass().getResource("./Images/pacmanRight.png"))).getImage();
        walls = new HashSet<Block>();
        ghosts = new HashSet<Block>();
        foods = new HashSet<Block>();
        loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadMap() {
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                String row = tileMap[r];
                char dec = row.charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;
                if (dec == 'X') {
                    Block wall = new Block(x, y, tileSize, tileSize, wallImage);
                    walls.add(wall);
                } else if (dec == 'b') {
                    Block blueGhost = new Block(x, y, tileSize, tileSize, blueGhostImage);
                    ghosts.add(blueGhost);
                }  else if (dec == 'o') {
                    Block blueGhost = new Block(x, y, tileSize, tileSize, orangeGhostImage);
                    ghosts.add(blueGhost);
                }  else if (dec == 'r') {
                    Block blueGhost = new Block(x, y, tileSize, tileSize, redGhostImage);
                    ghosts.add(blueGhost);
                }  else if (dec == 'p') {
                    Block blueGhost = new Block(x, y, tileSize, tileSize, pinkGhostImage);
                    ghosts.add(blueGhost);
                } else if (dec == 'P') {
                    pacman = new Block(x, y, tileSize, tileSize, pacmanRightImage );
                } else {
                    Block food = new Block(x + 14, y + 14, 4, 4, null);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }
        for (Block wall: walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }
        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over" + String.valueOf(score), tileSize/2, tileSize/2);
        } else {
            g.drawString("Lives" + String.valueOf(lives) + "Score: " + String.valueOf(score), tileSize/ 2, tileSize /2);
        }
    }
    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }
}
