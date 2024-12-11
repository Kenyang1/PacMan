// Import necessary libraries for GUI, event handling, and utilities
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

// The PacMan class extends JPanel for drawing the game and implements ActionListener and KeyListener for handling events
public class PacMan extends JPanel implements ActionListener, KeyListener {
    // Inner class representing a game block (e.g., PacMan, walls, ghosts, and food)
    class Block {
        int x, y; // Current position of the block
        int width, height; // Dimensions of the block
        Image image; // The image representing this block
        
        int startX, startY; // Initial starting position of the block
        char direction = 'U'; // Direction the block is moving: U (Up), D (Down), L (Left), R (Right)
        int velocityX = 0, velocityY = 0; // Current velocity in X and Y directions

        // Constructor to initialize a block's properties
        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        // Updates the block's direction and checks for wall collisions
        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;

            // Check for collisions with walls and revert movement if collided
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        // Updates the block's velocity based on its direction
        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize / 4;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize / 4;
            } else if (this.direction == 'L') {
                this.velocityX = -tileSize / 4;
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                this.velocityX = tileSize / 4;
                this.velocityY = 0;
            }
        }

        // Resets the block's position to its initial starting position
        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    // Game configuration variables
    private int rowCount = 21; // Number of rows in the game grid
    private int columnCount = 19; // Number of columns in the game grid
    private int tileSize = 32; // Size of each tile in pixels
    private int boardWidth = columnCount * tileSize; // Total width of the game board
    private int boardHeight = rowCount * tileSize; // Total height of the game board

    // Game objects (images for walls, ghosts, PacMan, etc.)
    private Image wallImage, blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;

    // Sets to store walls, food, and ghosts
    HashSet<Block> walls, foods, ghosts;
    Block pacman; // The PacMan block

    Timer gameLoop; // Timer for the game loop
    char[] direction = {'U', 'D', 'L', 'R'}; // Possible directions
    Random random = new Random(); // Random generator for ghost movement
    int score = 0; // Player's score
    int lives = 3; // Number of lives
    boolean gameOver = false; // Game-over state

    // Map defining the initial layout of the game
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

    // Constructor to initialize the game
    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight)); // Set panel size
        setBackground(Color.BLACK); // Set background color
        addKeyListener(this); // Add key listener for player controls
        setFocusable(true);

        // Load images for game objects
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap(); // Load the initial game map
        for (Block ghost : ghosts) {
            char newDirection = direction[random.nextInt(4)];
            ghost.updateDirection(newDirection); // Assign random directions to ghosts
        }

        gameLoop = new Timer(50, this); // Timer for 20 frames per second
        gameLoop.start(); // Start the game loop
    }

    // Method to load the map and create game objects
    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tileMapChar = tileMap[r].charAt(c);
                int x = c * tileSize, y = r * tileSize;

                if (tileMapChar == 'X') { // Wall
                    walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == 'b') { // Blue ghost
                    ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == 'o') { // Orange ghost
                    ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == 'p') { // Pink ghost
                    ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == 'r') { // Red ghost
                    ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == 'P') { // PacMan
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                } else if (tileMapChar == ' ') { // Food
                    foods.add(new Block(null, x + 14, y + 14, 4, 4));
                }
            }
        }
    }

        // In Java you can ultilzie the graphics component to draw
        // features 
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
        public void draw(Graphics g) {
            // draw image with the set parameters
            g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

            for (Block ghost: ghosts) {
                g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
            }

            for (Block wall: walls) {
                g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
            }
            
            g.setColor(Color.WHITE);
            for (Block food: foods) {
                g.fillRect(food.x, food.y, food.width, food.height);
            }
            //score
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            if (gameOver) {
                g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
            }
            else {
                g.drawString("Lives: " + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);

            }
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            move();
            repaint();
            if (gameOver) {
                gameLoop.stop();
            }
        }

        private void move() {
            pacman.x += pacman.velocityX;
            pacman.y += pacman.velocityY;

            //check wall collision
            for (Block wall : walls) {
                if (collision(pacman, wall)) {
                    pacman.x -= pacman.velocityX;
                    pacman.y -= pacman.velocityY;
                    break;
                }
            }
            for (Block ghost : ghosts) {
                if (collision(ghost, pacman)) {
                    lives -= 1;
                    if (lives == 0) {
                        gameOver = true;
                        return;
                    }
                    resetPositions();
                }
                if (ghost.y == tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D') {
                    ghost.updateDirection('U');
                }
                ghost.x += ghost.velocityX;
                ghost.y += ghost.velocityY;
                for (Block wall: walls) {
                    if(collision(wall, ghost) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                        ghost.x -= ghost.velocityX;
                        ghost.y -= ghost.velocityY;
                        char newDirection = direction[random.nextInt(4)];
                        ghost.updateDirection(newDirection);
                    }
                }
                
                }
                Block foodEaten = null;
                for (Block food: foods) {
                    if (collision(pacman, food)) {
                        foodEaten = food;
                        score += 10;
                    }
                }
                foods.remove(foodEaten);

                if(foods.isEmpty()) {
                    loadMap();
                    resetPositions();
                }
            }

        
        
        
        //collision detection formula
        public boolean collision(Block a, Block b) {
            return a.x < b.x + b.width &&
                    a.x + a.width > b.x &&
                    a.y < b.y + b.height &&
                    a.y + a.height > b.y;
        }

        public void resetPositions() {
            pacman.reset();
            pacman.velocityX = 0;
            pacman.velocityY = 0;
            for (Block ghost: ghosts) {
                ghost.reset();
                char newDirection = direction[random.nextInt(4)];
                ghost.updateDirection(newDirection);
            }
        }

        @Override
        // arrow keys
        public void keyTyped(KeyEvent e) {
            
        }
        @Override
        public void keyPressed(KeyEvent e) {
            
        }
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
            // System.out.println("KeyEvent" + e.getKeyCode());
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                pacman.updateDirection('U');
            }
            else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                pacman.updateDirection('D');
            }
            else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                pacman.updateDirection('L');
            }
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                pacman.updateDirection('R');
            }

            if (pacman.direction == 'U') {
                pacman.image = pacmanUpImage;
            }
            else if (pacman.direction == 'D') {
                pacman.image = pacmanDownImage;
            }
            else if (pacman.direction == 'L') {
                pacman.image = pacmanLeftImage;
            }
            else if (pacman.direction == 'R') {
                pacman.image = pacmanRightImage;
            }
        }
    }

