import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Color;
class Pair { //Pair class, which stores the velocities and positions of the objects
    double x;
    double y;
    public Pair (double X, double Y){this.x = X;this.y = Y;}
    public Pair add(Pair toAdd){return new Pair(x + toAdd.x, y + toAdd.y);} //Allows for mathematical operations with Pairs
    public Pair times(double multiplier){return new Pair(x * multiplier, y * multiplier);} //Allows for mathematical operations with Pairs
    public Pair divide(double denominator){return new Pair(x / denominator, y / denominator);} //Allows for mathematical operations with Pairs
    public void flipX(){this.x *= -1;} //Used when ball bounces
    public void flipY(){this.y *= -1;} //Used when ball bounces
}
class Paddle{ //Creates parent Paddle class
    Pair velocity;
    int width;
    int height;
    Color color;
    boolean bounced;
    public Paddle(Pair V, int width, int height, Color C){ //Constructor
        this.velocity = V;
        this.width = width;
        this.height = height;
        this.color = C;
        this.bounced = false;
    }
    public void setVelocity(Pair v){velocity = v;}
    public void updateSize(int counter){ 
        if (counter % 20 == 0 && height > 50){ //Reduces size as game is played until paddles are half the size
            height -=  1;
        }
    } //Size is reset after every point (Create a new paddle after ball leaves screen in World class (Lines 292, 293, 298, 299))
}
class PlayerPaddle extends Paddle{ //Child of Paddle class
    Pair position;
    public PlayerPaddle(double X, double Y, Pair V, int width, int height, Color C){ //Constructor
        super(V, width, height, C); //Calls constructor of parent class
        position = new Pair(X, Y); 
    }

    public void update (double time, int worldHeight, int counter){
        updateSize(counter);
        reachTop(); //Checks if paddle reaches top of screen
        reachBottom(worldHeight); //Checks if paddle reaches bottom of screen
        position = position.add(velocity.times(time)); //Velocity functon
    }

    public void reachTop(){
        if (position.y < 0 + height / 2){ //Checks if the paddles position.y is outisde of screen
            position.y = 0 + height / 2; //Resets the paddles position
            setVelocity(new Pair(0,0)); //Passes a new velocity pair with values 0, which stops the paddle
        }
    }
    public void reachBottom(int worldHeight){
        if (position.y > worldHeight - height + height / 2){ //Checks if the paddles position.y is outisde of screen
            position.y = worldHeight - height + height / 2; //Resets the paddles position
            setVelocity(new Pair(0,0)); //Passes a new velocity pair with values 0, which stops the paddle
        }
    }
    public void draw(Graphics g){ //Draws the paddle, such that they are equally spaced
        g.setColor(color);
        g.fillRect((int)position.x, (int)(position.y - (height / 2)), width, height);
    }
}
class MiddlePaddle extends Paddle{ //Child of Paddle class, intializes the non-player controlled, middle paddles, ie the "obstacles"
    Pair position;
    public MiddlePaddle(double X, double Y, Pair V, int width, int height, Color C){ //Constructor
        super(V, width, height, C); //Calls constructor of parent class
        position = new Pair(X, Y);
    }
    public void update (double time, int worldHeight){
        reachTop(); //Checks if paddle reaches top of screen
        reachBottom(worldHeight); //Checks if paddle reaches bottom of screen
        position = position.add(velocity.times(time)); //Velocity function
    }
    public void reachTop(){
        if (position.y < 0){ //Checks if the paddles position.y is outisde of screen
            position.y = 0; //Resets the paddles position
            setVelocity(new Pair(0,100)); //Passes a new velocity pair with values 0, which stops the paddle
        }
    }
    public void reachBottom(int worldHeight){
        if (position.y > worldHeight - height){ //Checks if the paddles position.y is outisde of screen
            position.y = worldHeight - height; //Resets the paddles position
            setVelocity(new Pair(0,-100)); //Passes a new velocity pair with values 0, which stops the paddle
        }
    }
    public void draw(Graphics g){ //Draws paddles
        g.setColor(color);
        g.fillRect((int)position.x, (int)position.y, width, height);
    }
}
class Ball{
    Pair position;
    Pair velocity;
    double diameter;
    double radius;
    int ballVel;
    Color color;
    public Ball(Pair P, Pair V, double DIAMETER, Color C, int ballVel){ //Constructor
        this.position = P;
        this.velocity = V;
        this.diameter = DIAMETER;
        this.radius = diameter / 2;
        this.ballVel = ballVel;
        this.color = C;
    }
    public void update(double time, World w, PlayerPaddle lP, PlayerPaddle rP, MiddlePaddle lMP, MiddlePaddle rMP){
        position = position.add(velocity.times(time)); //Velocoty function
        bounce(w, lP, rP, lMP, rMP); //Checks if the ball has bounced
        
    }
    public void bounce(World w, PlayerPaddle lP, PlayerPaddle rP, MiddlePaddle lMP, MiddlePaddle rMP){
        boolean bounceLeft = false; //Initializes a boolean to check if ball bounces on left paddle
        boolean bounceRight = false; //Initializes a boolean to check if ball bounces on right paddle
        //Bounce off top or bottom
        if (position.y < 0 + radius){ //If the edge of the ball goes past the top of the screen, reverses the Y direction
            velocity.flipY();
        } else if (position.y > w.height - radius){ //If the ball goes past the bottom of the screen, reverses the Y direction
            velocity.flipY();
        }
        //Bounce off Left Paddle
        if (position.x - radius < lP.position.x + lP.width && position.x + radius > lP.position.x &&
            position.y - radius < lP.position.y + lP.height / 2 && position.y + radius > lP.position.y - lP.height / 2) { //Checks to see any edge of the ball is within the bounds of the left paddle, through a series of inequlaities
            velocity.flipX(); //Flips the ball's X direction
            bounceLeft = true; //Sets boolean true
            rP.bounced = false; //If bounces off the left paddle sets right paddle bounced to false
            lP.bounced = true; //and left paddle to be true
        }
        //Bounce off right paddle
        if (position.x + radius > rP.position.x - rP.width && position.x - radius < rP.position.x && 
            position.y - radius < rP.position.y + rP.height / 2 && position.y + radius > rP.position.y - rP.height / 2) { //Checks to see any edge of the ball is within the bounds of the right paddle, through a series of inequlaities
            velocity.flipX(); //Flips the ball's X direction
            bounceRight = true; //Sets the boolean true
            rP.bounced = true; //If bounces off the left paddle sets right paddle bounced to true
            lP.bounced = false; //and left paddle to be true
        }
        if (bounceRight){ //If the ball bounces on the right paddle, then changes the ball's velocities
            changeRightVelocity(w, rP); //Calls method to change ball's velocities and passes the right paddle
        }
        if (bounceLeft){ //If the ball bounces on the right paddle, then changes the ball's velocities
            changeLeftVelocity(w, lP); //Calls method to change ball's velocities and passes the right paddle
        }
        //Bounce off Left Middle Paddle
        if (position.x + radius > lMP.position.x && position.x - radius < lMP.position.x + lMP.width && 
            position.y - radius < lMP.position.y + lMP.height && 
            position.y + radius > lMP.position.y){ //Checks to see if any part of the ball is within the bounds of the left middle paddle
            if (velocity.x < 0){ //If the ball bounced on right side of paddle, resests position accordingly
                position.x = lMP.position.x + lMP.width + radius;
            } else if (velocity.x > 0){ //If the ball bounced on left side of paddle, resests position accordingly
                position.x = lMP.position.x - radius;
            }
            velocity.flipX(); //Reverses the x velocity of the ball
        }
        //Bounce off Right Middle Paddle
        if (position.x + radius > rMP.position.x && position.x - radius < rMP.position.x + rMP.width && 
            position.y - radius < rMP.position.y + rMP.height && 
            position.y + radius > rMP.position.y){ //Checks to see if any part of the ball is within the bouncd of the right middle paddle
            if (velocity.x < 0){ //If the ball bounced on right side of paddle, resests position accordingly
                position.x = rMP.position.x + rMP.width + radius;
            } else if (velocity.x > 0){ //If the ball bounced on left side of paddle, resests position accordingly
                position.x = rMP.position.x - radius;
            }
            velocity.flipX(); //Reverses the x velocity of the ball
        }
    }
    public void changeLeftVelocity(World w, PlayerPaddle lP){
       
        double bounceLocation = position.y - lP.position.y; //Calculates the place at which the ball contacts the paddle

        double standardizedBounceLocation = bounceLocation / (lP.height / 2); //Standardizes the place of contact such that the new var is between about -1 and 1
        //Sets the ball velocity equal to a new pair, such that the Y velocity is multiplied by the standardized locaction between -1 and 1, meaning the higher or lower on the paddle, the more Y velocity
        velocity = new Pair(Math.abs(velocity.x), Math.sin(standardizedBounceLocation) * w.ballVel);
        //Prints values to check
        System.out.println("Standardized Bounce Location: " + standardizedBounceLocation);
        System.out.println("X Velocity: " + velocity.x);
        System.out.println("Y Velocity: " + velocity.y);
    }
    public void changeRightVelocity(World w, PlayerPaddle rP){
        double bounceLocation = position.y - rP.position.y; //Calculates the place at which the ball contact the paddle
        double standardizedBounceLocation = bounceLocation / (rP.height / 2); //Standardizes the place of contact such that the new var is between about -1 and 1
        
        //Sets the ball velocity equal to a new pair, such that the Y velocity is multiplied by the standardized locaction between -1 and 1, meaning the higher or lower on the paddle, the more Y velocity
        velocity = new Pair(-Math.abs(velocity.x), Math.sin(standardizedBounceLocation) * w.ballVel);
        //Prints values to check
        System.out.println("Standardized Bounce Location: " + standardizedBounceLocation);
        System.out.println("X Velocity: " + velocity.x);
        System.out.println("Y Velocity: " + velocity.y);
    }
    public boolean offLeftScreen(World w){
        //Checks if the ball goes past the paddle and off screen, if yes, returns true
        if (position.x - radius < 0){
            return true;
        }
        return false;
    }
    public boolean offRightScreen(World w){
        //Checks if the ball goes past the paddle and off screen, if yes, returns true
        if (position.x - radius > w.width){
            return true;
        }
        return false;
    }
    public void setVelocity(Pair v){velocity = v;}
    public void draw(Graphics g){
        g.setColor(color);
        g.fillOval((int)(position.x - radius), (int)(position.y - radius), (int)diameter, (int)diameter);
    }
}
class Powerup{
    Pair position;
    Pair velocity;
    Color color;
    double diameter;
    double radius;
    public Powerup(Pair position, Pair velocity, Color color, double diameter){ //Constructor
        this.position = position;
        this.velocity = velocity;
        this.color = color;
        this.diameter = diameter;
        this.radius = diameter / 2;
    }
    public void draw(Graphics g){
        g.setColor(color);
        g.fillOval((int)(position.x - radius), (int)(position.y - radius), (int)diameter, (int)diameter);
    }
    public boolean collision(Ball b){ //Calculates distance between ball and powerups and checks if its less than the distacne betwene their centers
        double changeX = Math.pow(position.x - b.position.x, 2);
        double changeY = Math.pow(position.y - b.position.y, 2);
        double distance = Math.sqrt(changeX + changeY); 
        if (distance < radius + b.radius){
            return true;
        }
        return false;
    }
}
class World{
    int height;
    int width;
    PlayerPaddle lP;
    PlayerPaddle rP;
    MiddlePaddle lMP;
    MiddlePaddle rMP;
    Ball ball;
    Powerup upperPowerup;
    Powerup lowerPowerup;
    int rightPaddleVel;
    int leftPaddleVel;
    int ballVel;
    int leftScore;
    int rightScore;
    int counter; //Allows for slowly decreasing the paddle size
    public World(int initHeight, int initWidth){ //Constructor, initializes all objects with their various parameters
        this.height = initHeight;
        this.width = initWidth;
        rightPaddleVel = 300;
        leftPaddleVel = 300;
        ballVel = 350;
        lP = new PlayerPaddle(50, height / 2 , new Pair (0, 0), 6, 100, new Color(255,0,0));
        rP = new PlayerPaddle(974, height / 2 , new Pair (0, 0), 6, 100, new Color(255,0,0));
        lMP = new MiddlePaddle(width / 2 - 35, (height / 2) - 150, new Pair(0,0), 6, 80, new Color(255,255,255));
        rMP = new MiddlePaddle(width / 2 + 30, (height / 2) + 60, new Pair(0,0), 6, 80, new Color(255,255,255));
        ball = new Ball(new Pair(width / 2, height / 2), new Pair(0, 0),30, new Color(0,0,255), ballVel);
        lowerPowerup = new Powerup(new Pair(width / 2, 3 * (height / 4)), new Pair(0,0), new Color (127,255,100), 45);
        upperPowerup = new Powerup(new Pair(width / 2, (height / 4)), new Pair(0,0), new Color (127,255,100), 45);
    }
    public void updateBall(double time, World w){ball.update(time, w, lP, rP, lMP, rMP);
        if (ball.offLeftScreen(this)){ //If ball leaves the screen reinitializes the ball and/or the paddles, creating a new "game"
            rightScore++; //Increments score
            lP = new PlayerPaddle(50, lP.position.y, new Pair (0, 0), 6, 100, new Color(255,0,0));
            rP = new PlayerPaddle(974, rP.position.y, new Pair (0, 0), 6, 100, new Color(255,0,0));
            ball = new Ball(new Pair(width / 2, height / 2), new Pair(ballVel, 0),30, new Color(0,0,255), ballVel);
            lowerPowerup = new Powerup(new Pair(width / 2, 3 * (height / 4)), new Pair(0,0), new Color (127,255,100), 45);
            upperPowerup = new Powerup(new Pair(width / 2, (height / 4)), new Pair(0,0), new Color (127,255,100), 45);
            rightPaddleVel = 300;
            leftPaddleVel = 300;
        }
        if (ball.offRightScreen(this)){ //If ball leaves the screen reinitializes the paddles and balls, creating a new "game"
            leftScore++; //Increments score
            lP = new PlayerPaddle(50, lP.position.y, new Pair (0, 0), 6, 100, new Color(255,0,0));
            rP = new PlayerPaddle(974, rP.position.y, new Pair (0, 0), 6, 100, new Color(255,0,0));
            ball = new Ball(new Pair(width / 2, height / 2), new Pair(-ballVel, 0),30, new Color(0,0,255), ballVel);
            lowerPowerup = new Powerup(new Pair(width / 2, 3 * (height / 4)), new Pair(0,0), new Color (127,255,100), 45);
            upperPowerup = new Powerup(new Pair(width / 2, (height / 4)), new Pair(0,0), new Color (127,255,100), 45);
            rightPaddleVel = 300;
            leftPaddleVel = 300;
        }
    }
    public void updatePaddles(double time){lP.update(time, height, counter);rP.update(time, height, counter);lMP.update(time, height);rMP.update(time, height);counter ++;
        if(upperPowerup != null){ //Checks if powerup exists
            if (upperPowerup.collision(ball)){ //Checks if ball has collided with powerup
                if (rP.bounced ){ //If it bounced off the right paddle, gives powerup to right paddle
                    upperPowerup = null;
                    rightPaddleVel *= 1.5;
                }
                if (lP.bounced){ //If it bounced off the left paddle, gives the powerup to the left paddle
                    upperPowerup = null;
                    leftPaddleVel *= 1.5;
                }
            }
        }
        if (lowerPowerup != null){ //Checks if powerup exists
            if (lowerPowerup.collision(ball)){ //Checks if ball has collided with powerup
                if (rP.bounced){ //If it bounced off the right paddle, gives powerup to right paddle
                    lowerPowerup = null;
                    rightPaddleVel *= 1.5;
                }
                if (lP.bounced){ //If it bounced off the left paddle, gives the powerup to the left paddle
                    lowerPowerup = null;
                    leftPaddleVel *= 1.5;
                }
            }
        }
    } 
    public void drawBall(Graphics g){
        ball.draw(g);
        if (upperPowerup != null){ //Checks if powerup exists
            upperPowerup.draw(g);
        }
        if (lowerPowerup != null) { //Checks if powerup exists
            lowerPowerup.draw(g);
        }
    }    
    public void drawPaddles(Graphics g){lP.draw(g);rP.draw(g);lMP.draw(g);rMP.draw(g);}
}
public class Pong extends JPanel implements KeyListener {
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int FPS = 60;
    World world;

    class Runner implements Runnable {
        public void run() {
            while (true) {
                world.updateBall(1.0 / (double)FPS, world);
                world.updatePaddles(1.0 / (double)FPS);
                repaint();
                try {
                    Thread.sleep(1000 / FPS);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //char c = e.getKeyChar();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();

        if (c == 'r'){ //Move left paddle up
            world.lP.setVelocity(new Pair(0, -world.leftPaddleVel)); //When key is pressed passes new velocity Pair, with new Y velocity corresponidng to the desired direction
        }
        if (c == 'f'){ //Stop left paddle
            world.lP.setVelocity(new Pair(0,0)); //When key is pressed passes new velocity Pair, with velocities 0, stopping the paddle
        }
        if (c == 'v'){ //Move left paddle down
            world.lP.setVelocity(new Pair(0, world.leftPaddleVel)); //When key is pressed passes new velocity Pair, with new Y velocity corresponidng to the desired direction
        }

        if (c == 'u'){ //Move right paddle up
            world.rP.setVelocity(new Pair(0, -world.rightPaddleVel)); //When key is pressed passes new velocity Pair, with new Y velocity corresponidng to the desired direction
        }
        if (c == 'j'){ //Stop right paddle
            world.rP.setVelocity(new Pair(0,0)); //When key is pressed passes new velocity Pair, with velocities 0, stopping the paddle
        }
        if (c == 'n'){ //Move right paddle down
            world.rP.setVelocity(new Pair(0, world.rightPaddleVel)); //When key is pressed passes new velocity Pair, with new Y velocity corresponidng to the desired direction
        }

        if (c == 'b'){
            int rand = ((int)(Math.random() * 2)); //Random number 0 or 1
            //Sets var direction equal depeningd on what rand generates
            int direction = 1;
            if (rand == 0){
                direction = -1;
            }
            if (world.ball.velocity.x == 0 && world.ball.velocity.y == 0){
                world.ball.setVelocity(new Pair((direction * world.ballVel), 0)); //When key is pressed, starts moving the ball
                world.lMP.setVelocity(new Pair(0, -100));
                world.rMP.setVelocity(new Pair(0, 100));
            }
        }

        System.out.println("You pressed down: " + c);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //char c = e.getKeyChar();
    }

    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    public Pong() {
        world = new World(HEIGHT, WIDTH);
        addKeyListener(this);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        Thread mainThread = new Thread(new Runner());
        mainThread.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Pong mainInstance = new Pong();
        frame.setContentPane(mainInstance);
        frame.pack();
        frame.setVisible(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.white);
        
        //When called following will draw series of lines to help with spacing

        g.setColor(Color.white);
        g.setFont(new Font("TimesRoman", Font.PLAIN, world.width / 6));
        g.drawString(world.leftScore + "", (world.width / 4) - (world.width / 15) + 45, 160); //Draws the score of the left side
        g.drawString(world.rightScore + "", 3 * (world.width / 4) - (world.width / 15) + 45, 160); //Draws the score of the right side
        if (world.ball.velocity.x == 0){ //Draws beginning instructions 
            g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
            g.drawString("Press 'b' to begin", (world.width / 2) - 65, 30);
        }

        world.drawPaddles(g); //Draws the paddles
        world.drawBall(g); //Draws the ball
        
    }
}