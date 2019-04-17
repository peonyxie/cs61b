package amazons;

import ucb.gui2.Pad;

import java.io.IOException;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import static amazons.Piece.*;
import static amazons.Square.sq;


/** A widget that displays an Amazons game.
 *  @author Yuhan Xie
 */
class BoardWidget extends Pad {

    /* Parameters controlling sizes, speeds, colors, and fonts. */

    /** Colors of empty squares and grid lines. */
    static final Color
        SPEAR_COLOR = new Color(64, 64, 64),
        LIGHT_SQUARE_COLOR = new Color(238, 207, 161),
        DARK_SQUARE_COLOR = new Color(205, 133, 63);

    /** Locations of images of white, black queens and spears */
    private static final String
        WHITE_QUEEN_IMAGE = "wq4.png",
        BLACK_QUEEN_IMAGE = "bq4.png",
        SPEAR_IMAGE = "spear.png";

    /** Size parameters. */
    private static final int
        SQUARE_SIDE = 30,
        BOARD_SIDE = SQUARE_SIDE * 10;


    /** A graphical representation of an Amazons board that sends commands
     *  derived from mouse clicks to COMMANDS.  */
    BoardWidget(ArrayBlockingQueue<String> commands) {
        _commands = commands;
        setMouseHandler("click", this::mouseClicked);
        setPreferredSize(BOARD_SIDE, BOARD_SIDE);

        try {
            _whiteQueen = ImageIO.read(Utils.getResource(WHITE_QUEEN_IMAGE));
            _blackQueen = ImageIO.read(Utils.getResource(BLACK_QUEEN_IMAGE));
            _spear = ImageIO.read(Utils.getResource(SPEAR_IMAGE));
        } catch (IOException excp) {
            System.err.println("Could not read queen images.");
            System.exit(1);
        }
        _acceptingMoves = false;
    }

    /** Draw the bare board G.  */
    private void drawGrid(Graphics2D g) {
        g.setColor(LIGHT_SQUARE_COLOR);
        g.fillRect(0, 0, BOARD_SIDE, BOARD_SIDE);
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        drawGrid(g);
        g.setColor(DARK_SQUARE_COLOR);
        for (int i = 0;  i < 10; i++) {
            if (i % 2 == 1) {
                for (int j = 0; j < BOARD_SIDE; j += SQUARE_SIDE * 2) {
                    g.fillRect(j * SQUARE_SIDE,j, SQUARE_SIDE, SQUARE_SIDE);
                }
            } else {
                    for (int k = SQUARE_SIDE; k < BOARD_SIDE; k += SQUARE_SIDE * 2) {
                        g.fillRect(i * SQUARE_SIDE, k, SQUARE_SIDE, SQUARE_SIDE);
                    }
                }
            }
            Set<Square> allblack = _board.getblack();
            Set<Square> allwhite = _board.getwhite();
            Set<Square> allspear = _board.getspears();
            for (Square i: allblack){
                drawQueen(g, i, BLACK);
            }
            for (Square i: allwhite) {
                drawQueen(g, i, WHITE);
            }
            for (Square i: allspear) {
                drawSpear(g, i);
            }
    }

    /** Draw a queen for side PIECE at square S on G.  */
    private void drawQueen(Graphics2D g, Square s, Piece piece) {
        g.drawImage(piece == WHITE ? _whiteQueen : _blackQueen,
                    cx(s.col()) + 2, cy(s.row()) + 4, null);
    }

    /**
     * Draw a spear at square S on G.
     */
    private void drawSpear(Graphics2D g, Square s) {
        g.drawImage(_spear,
                cx(s.col()) + 2, cy(s.row()) + 4, null);
    }


    /**
     * Click Counter CLICKCOUNT.
     */
    private static int clickcount = 0;

    /**
     * Click History CLICKHIS.
     */
    private static String clickhis = "";

    /**
     * Handle a click on S.
     */
    private void click(Square s) {
        if (clickcount == 0 && _board.get(s) == _board.turn()) {
            clickhis += s.toString();
            clickhis += " ";
            clickcount += 1;
        } else if (clickcount == 1) {
            clickhis += s.toString();
            clickhis += " ";
            clickcount += 1;
        } else if (clickcount == 2) {
            clickhis += s.toString();
            if (Move.mv(clickhis) != null
                    && _board.isLegal(Move.mv(clickhis))) {
                _commands.add(clickhis);
            }
            clickcount = 0;
            clickhis = "";
        }
        repaint();
    }

    /** Handle mouse click event E. */
    private synchronized void mouseClicked(String unused, MouseEvent e) {
        int xpos = e.getX(), ypos = e.getY();
        int x = xpos / SQUARE_SIDE,
            y = (BOARD_SIDE - ypos) / SQUARE_SIDE;
        if (_acceptingMoves
            && x >= 0 && x < Board.SIZE && y >= 0 && y < Board.SIZE) {
            click(sq(x, y));
        }
    }

    /** Revise the displayed board according to BOARD. */
    synchronized void update(Board board) {
        _board.copy(board);
        repaint();
    }

    /** Turn on move collection iff COLLECTING, and clear any current
     *  partial selection.   When move collection is off, ignore clicks on
     *  the board. */
    void setMoveCollection(boolean collecting) {
        _acceptingMoves = collecting;
        repaint();
    }

    /** Return x-pixel coordinate of the left corners of column X
     *  relative to the upper-left corner of the board. */
    private int cx(int x) {
        return x * SQUARE_SIDE;
    }

    /** Return y-pixel coordinate of the upper corners of row Y
     *  relative to the upper-left corner of the board. */
    private int cy(int y) {
        return (Board.SIZE - y - 1) * SQUARE_SIDE;
    }

    /** Return x-pixel coordinate of the left corner of S
     *  relative to the upper-left corner of the board. */
    private int cx(Square s) {
        return cx(s.col());
    }

    /** Return y-pixel coordinate of the upper corner of S
     *  relative to the upper-left corner of the board. */
    private int cy(Square s) {
        return cy(s.row());
    }

    /** Queue on which to post move commands (from mouse clicks). */
    private ArrayBlockingQueue<String> _commands;
    /** Board being displayed. */
    private final Board _board = new Board();

    /** Image of white queen. */
    private BufferedImage _whiteQueen;
    /** Image of black queen. */
    private BufferedImage _blackQueen;
    /**
     * Image of spear.
     */
    private BufferedImage _spear;

    /** True iff accepting moves from user. */
    private boolean _acceptingMoves;
}
