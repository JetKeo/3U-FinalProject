
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 *
 * @author lamon
 */
public class finalProject extends JComponent {

    // Height and Width of our game
    static final int WIDTH = 800;
    static final int HEIGHT = 600;

    //Title of the window
    String title = "Don't Get Stuck On The Wall";

    // sets the framerate and delay for our game
    // you just need to select an approproate framerate
    long desiredFPS = 60;
    long desiredTime = (1000) / desiredFPS;

    // YOUR GAME VARIABLES WOULD GO HERE
    // player position
    Rectangle player = new Rectangle(100, 500, 35, 35);
    Font points = new Font("Arial", Font.BOLD, 30);

    int dy = 0; // displacement of y - how much you move up/down each time
    int dx = 0; // displacement of x - how much you move left/right each frame
    double decay = 0.8; // how much the dx should decrease by if not "moving". This gives the decelerating effect

    int gravity = 1; // .... Its gravity. It pulls you down....
    boolean inAir = false;  // is the player in the air or not? Prevents "air jumping"

    int JUMP_VELOCITY = -7; // how hard the character jumps up. 
    int MAX_Y_VELOCITY = 6; // maximum speed the dy can be
    int MAX_X_VELOCITY = 5; // maximum speed the dx can be
    double blockSpeed = 1; // the speed the blocks are moving at you
    int blockSpawn = 780;
    int blockSpawn2 = 1180;
    int blockWidth = 10;
    int blockLength = 95;
    int score = 0;
    long coolDown = 0;
    int nextSpeed = 5000;
    long scoreDelay = 100;
    long pointScore = 0;
    int random = 0;
    int random2 = 0;
    // key variables
    boolean right = false;
    boolean left = false;
    boolean jump = false;
    boolean reset = false;
    boolean ranBlock = true;
    boolean ranBlock2 = true;
    // block variable - I'll use an array to make this easier!
    Rectangle[] blocks = new Rectangle[12]; // I'll add these in the pre setup section

    // GAME VARIABLES END HERE   
    // Constructor to create the Frame and place the panel in
    // You will learn more about this in Grade 12 :)
    public finalProject() {
        // creates a windows to show my game
        JFrame frame = new JFrame(title);

        // sets the size of my game
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // adds the game to the window
        frame.add(this);

        // sets some options and size of the window automatically
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // shows the window to the user
        frame.setVisible(true);

        // add listeners for keyboard and mouse
        frame.addKeyListener(new Keyboard());
        Mouse m = new Mouse();

        this.addMouseMotionListener(m);
        this.addMouseWheelListener(m);
        this.addMouseListener(m);
    }

    // drawing of the game happens in here
    // we use the Graphics object, g, to perform the drawing
    // NOTE: This is already double buffered!(helps with framerate/speed)
    @Override
    public void paintComponent(Graphics g) {
        // always clear the screen first!
        g.clearRect(0, 0, WIDTH, HEIGHT);

        // GAME DRAWING GOES HERE
        // draw a black background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // draw blocks in gray
        g.setColor(Color.GRAY);
        // use a for loop to go through the array of blocks :)
        for (int i = 0; i < blocks.length; i++) {
            g.fillRect(blocks[i].x, blocks[i].y, blocks[i].width, blocks[i].height);
        }

        // draw player
        g.setColor(Color.red);
        g.fillRect(player.x, player.y, player.width, player.height);

        g.setColor(Color.blue);
        g.setFont(points);
        g.drawString("" + score, 710, 30);
        // GAME DRAWING ENDS HERE
    }

    // This method is used to do any pre-setup you might need to do
    // This is run before the game loop begins!
    public void preSetup() {
        // Any of your pre setup before the loop starts should go here

        // create all the different blocks to use in the level
        // They are each in the array
        blocks[0] = new Rectangle(blockSpawn, 0, blockWidth, blockLength);
        blocks[1] = new Rectangle(blockSpawn, 100, blockWidth, blockLength);
        blocks[2] = new Rectangle(blockSpawn, 200, blockWidth, blockLength);
        blocks[3] = new Rectangle(blockSpawn, 300, blockWidth, blockLength);
        blocks[4] = new Rectangle(blockSpawn, 400, blockWidth, blockLength);
        blocks[5] = new Rectangle(blockSpawn, 500, blockWidth, blockLength);

        blocks[6] = new Rectangle(blockSpawn2, 0, blockWidth, blockLength);
        blocks[7] = new Rectangle(blockSpawn2, 100, blockWidth, blockLength);
        blocks[8] = new Rectangle(blockSpawn2, 200, blockWidth, blockLength);
        blocks[9] = new Rectangle(blockSpawn2, 300, blockWidth, blockLength);
        blocks[10] = new Rectangle(blockSpawn2, 400, blockWidth, blockLength);
        blocks[11] = new Rectangle(blockSpawn2, 500, blockWidth, blockLength);
    }

    // The main game loop
    // In here is where all the logic for my game will go
    public void run() {
        // Used to keep track of time used to draw and update the game
        // This is used to limit the framerate later on
        long startTime;
        long deltaTime;

        preSetup();

        // the main game loop section
        // game will end if you set done = false;
        boolean done = false;
        while (!done) {
            // determines when we started so we can keep a framerate
            startTime = System.currentTimeMillis();

            // all your game rules and move is done in here
            // GAME LOGIC STARTS HERE 
            // apply gravity!
            dy = dy + gravity; // gravity always pulls down!
            // clamp maximum down force
            if (dy > MAX_Y_VELOCITY) {
                dy = MAX_Y_VELOCITY; // biggest positive dy
            } else if (dy < -MAX_Y_VELOCITY) {
                dy = -MAX_Y_VELOCITY; // biggest negative dy
            }

            // look at keys for left/right movement
            if (right) {
                dx = dx + 1; // start ramping up my movement
                // cap my max speed
                if (dx > MAX_X_VELOCITY) {
                    dx = MAX_X_VELOCITY;
                }
            } else if (left) {
                dx = dx - 1; // start ramping up my movement
                // cap my max speed
                if (dx < -MAX_X_VELOCITY) {
                    dx = -MAX_X_VELOCITY;
                }
            } else {
                // need to start slowing down
                dx = (int) (dx * decay); // takes a percentage of what dx was... needs to be an int
            }

            // is jump being pressed and are you standing on something?
            if (jump) {
                inAir = false; // I'm going to be jumping... not on the ground :)
                dy = JUMP_VELOCITY; // start moving up!
            }

            // apply the forces to x and y
            player.x = player.x + dx;
            player.y = player.y + dy;

            //every 5 seconds it speed up by 0.5
            if (startTime > coolDown) {
                coolDown = (startTime + nextSpeed);
                blockSpeed = (double) (blockSpeed + 0.5);
            }
            //move the blocks left
            for (int i = 0; i < blocks.length; i++) {
                blocks[i].x = (int) (blocks[i].x - blockSpeed);
            }
            //remove 1 block everytime it resets for the first wave
            if (ranBlock == true) {
                for (int i = 0; i < 1; i++) {
                    if (blocks[0].x >= 770 || blocks[5].x <= 770) {
                        random = (int) ((Math.random() * (6)));

                        blocks[random].width = 0;
                        blocks[random].height = 0;
                        ranBlock = false;
                    }
                }
            }

            //}
            //remove 1 block everytime it resets for wave 2
            if (ranBlock2 == true) {
                for (int i = 0; i < 1; i++) {
                    if (blocks[6].x >= 777 || blocks[11].x <= 780) {
                        random2 = (int) ((Math.random() * (11 - 5) + 6));

                        blocks[random2].width = 0;
                        blocks[random2].height = 0;
                        ranBlock2 = false;
                    }
                }
            }
            //once it hits 0 reset the blocks to its original place
            for (int i = 0; i < blocks.length; i++) {
                if (blocks[i].x <= 0) {
                    blocks[i].x = 780;
                    blocks[i].height = 95;
                    blocks[i].width = 10;
                    ranBlock = true;
                    ranBlock2 = true;
                }
            }
            //scoring code
            if (startTime > pointScore) {
                pointScore = startTime + scoreDelay;
                score++;
            }

            if (player.x <= 1 || player.y + player.height >= HEIGHT + 25) {
                done = true;

            }
            // check for any collisions and fix them
            // see the method below
            checkCollisions();

            // GAME LOGIC ENDS HERE 
            // update the drawing (calls paintComponent)
            repaint();

            // SLOWS DOWN THE GAME BASED ON THE FRAMERATE ABOVE
            // USING SOME SIMPLE MATH
            deltaTime = System.currentTimeMillis() - startTime;
            try {
                if (deltaTime > desiredTime) {
                    //took too much time, don't wait
                    Thread.sleep(1);
                } else {
                    // sleep to make up the extra time
                    Thread.sleep(desiredTime - deltaTime);
                }
            } catch (Exception e) {
            };
        }
    }

    // Used to implement any of the Mouse Actions
    private class Mouse extends MouseAdapter {

        // if a mouse button has been pressed down
        @Override
        public void mousePressed(MouseEvent e) {

        }

        // if a mouse button has been released
        @Override
        public void mouseReleased(MouseEvent e) {

        }

        // if the scroll wheel has been moved
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

        }

        // if the mouse has moved positions
        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }

    // Used to implements any of the Keyboard Actions
    private class Keyboard extends KeyAdapter {

        // if a key has been pressed down
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_RIGHT) {
                right = true;
            } else if (key == KeyEvent.VK_LEFT) {
                left = true;
            } else if (key == KeyEvent.VK_SPACE) {
                jump = true;
            } else if (key == KeyEvent.VK_R) {
                reset = true;
            }
        }

        // if a key has been released
        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_RIGHT) {
                right = false;
            } else if (key == KeyEvent.VK_LEFT) {
                left = false;
            } else if (key == KeyEvent.VK_SPACE) {
                jump = false;
            } else if (key == KeyEvent.VK_R) {
                reset = false;
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // creates an instance of my game
        finalProject game = new finalProject();

        // starts the game loop
        game.run();
    }

    // method the check if there are collisions in the level
    public void checkCollisions() {
        // use a loop to go through each block
        // see if we are hitting any block... if we are not, we should be falling
        boolean colliding = false;
        for (int i = 0; i < blocks.length; i++) {
            // if the player is hitting a block at position i
            if (player.intersects(blocks[i])) {
                // handle the collision with the block at position i    
                handleCollision(i);
                colliding = true;

            }

        }

        // if no collision seen, I'm in the air!
        if (!colliding) {
            inAir = true;
        }
        if (player.y <= 0) {
            player.y = 0;
        }
        if (player.x <= 0) {
            player.x = 0;
        }
        if (player.x + player.width >= WIDTH) {
            player.x = player.x - 5;
        }
        if (player.y + player.height >= HEIGHT) {
            player.y = player.y - 6;
        }
    }

    // method to fix any collisions that happen
    // the position integer is which block it is colliding with in the array of blocks
    // since all of our blocks are "axis-aligned" it is easier
    // we will determine how much of an overlap we have, and fix the smaller one (x or y)
    public void handleCollision(int position) {
        // set my overlap as a number - -1 means not set
        int overlapX = -1;
        // player is on the left
        if (player.x <= blocks[position].x) {
            // right corner of player subtract left corner of block
            overlapX = player.x + player.width - blocks[position].x;

        } else {
            // right corner of block subtract left corner of player
            overlapX = blocks[position].x + blocks[position].width - player.x;
        }

        // do the same but for the y values
        // set my overlap as a number - -1 means not set
        int overlapY = -1;
        // player is above the block
        if (player.y <= blocks[position].y) {
            // bottom of player subtract top of block
            overlapY = player.y + player.height - blocks[position].y;
        } else {
            // bottom of block subtract top of player
            overlapX = blocks[position].y + blocks[position].height - player.y;
        }

        // now check which overlap is smaller
        // we will correct that one because it will be less obvious!
        // fix the x overlapping
        // move the players x position so the no longer hit the block
        // we also fix the dx so that we are no longer changing that
        if (overlapX < overlapY) {
            // which side am I on?
            // on the right side
            if (player.x <= blocks[position].x) {
                player.x = blocks[position].x - player.width;
            } else {
                player.x = blocks[position].x + blocks[position].width;
            }
            dx = 0; // not moving left or right any more :)
        } else {
            // fixing the y overlap in the same way
            // the difference this time is we have to deal with the dy and not dx

            // above the block
            if (player.y <= blocks[position].y) {
                // no more y collision
                player.y = blocks[position].y - player.height;
                // I'm on the block so not in the air!
                inAir = false;
            } else {
                // im under the block, just fix the overlap
                player.y = blocks[position].y + blocks[position].height;
            }
            dy = 0; // not moving up or down anymore :)
        }
    }

}
