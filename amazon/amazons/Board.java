package amazons;

import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Collections;


import static amazons.Piece.*;

/** The state of an Amazons Game.
 *  @author Yuhan Xie
 */
class Board {

    /**
     * The number of squares on a side of the board.
     */
    static final int SIZE = 10;
    /**
     * 2D array to represent board.
     */
    private int[][] board;
    /**
     * white queens on the board is 1.
     */
    static final int WHITE_QUEEN = 1;
    /**
     * black queens on the board is 2.
     */
    static final int BLACK_QUEEN = 2;
    /**
     * arrows on the board is 3.
     */
    static final int ARROW = 3;
    /**
     * empty on the board is 0.
     */
    static final int EMP = 0;
    /**
     * Set that stores the positions of each elements.
     */
    private Set<Square> whiteQueens, blackQueens, spears;
    /**
     * array that stores all moves.
     */
    private ArrayList<Move> moves;

    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    Board() {
        init();
    }

    /**
     * get whitequeens helper.
     * @return return whitequeens
     */
    Set<Square> getwhite(){
        return whiteQueens;
    }

    /**
     * getblackqueens helper.
     * @return return blackqueens
     */
    Set<Square> getblack() {
        return blackQueens;
    }

    /**
     * get spears helper.
     * @return return spears.
     */
    Set<Square> getspears(){
        return spears;
    }

    /**
     * Initializes a copy of MODEL.
     */
    Board(Board model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        board = new int[SIZE][SIZE];
        whiteQueens = model.whiteQueens;
        blackQueens = model.blackQueens;
        spears = model.spears;
        moves = model.moves;

        _turn = model._turn;
        _winner = model.winner();

        for (int m = 0; m < SIZE; m++) {
            for (int n = 0; n < SIZE; n++) {
                this.board[m][n] = model.board[m][n];
            }
        }
    }

    /**
     * Clears the board to the initial position.
     */
    void init() {
        board = new int[SIZE][SIZE];

        whiteQueens = new HashSet<>();
        blackQueens = new HashSet<>();
        spears = new HashSet<>();
        moves = new ArrayList<>();


        put(WHITE, Square.sq(0, 3));
        put(WHITE, Square.sq(3, 0));
        put(WHITE, Square.sq(6, 0));
        put(WHITE, Square.sq(9, 3));
        put(BLACK, Square.sq(0, 6));
        put(BLACK, Square.sq(3, 9));
        put(BLACK, Square.sq(6, 9));
        put(BLACK, Square.sq(9, 6));

        _turn = WHITE;
        _winner = EMPTY;
    }

    /**
     * Return the Piece whose move it is (WHITE or BLACK).
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return the number of moves (that have not been undone) for this
     * board.
     */
    int numMoves() {
        int count = 0;
        for (int m = 0; m < SIZE; m++) {
            for (int n = 0; n < SIZE; n++) {
                if (this.board[m][n] == ARROW) {
                    count += 1;
                }
            }
        }
        return count;
    }

    /**
     * Return the winner in the current position, or null if the game is
     * not yet finished.
     */
    Piece winner() {
        if (hasNoMove(_turn)) {
            return _turn.opponent();
        }
        return null;
    }

    /**
     * helper function to find if any queen has legal moves.
     * @param p Piece p
     * @return return boolean
     */

    boolean hasNoMove(Piece p) {
        if (p == WHITE) {
            for (Square s : whiteQueens) {
                for (Square neighbor : s.neighbors()) {
                    if (board[neighbor.col()][neighbor.row()] == 0) {
                        return false;
                    }
                }
            }
            return true;
        }
        for (Square s : blackQueens) {
            for (Square neighbor : s.neighbors()) {
                if (board[neighbor.col()][neighbor.row()] == 0) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Return the contents the square at S.
     */
    final Piece get(Square s) {
        if (s == null) {
            return null;
        }
        return get(s.col(), s.row());
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW <= 9.
     */
    final Piece get(int col, int row) {
        if (!Square.exists(col, row)) {
            return null;
        }
        if (board[col][row] == WHITE_QUEEN) {
            return WHITE;
        }
        if (board[col][row] == BLACK_QUEEN) {
            return BLACK;
        }
        if (board[col][row] == ARROW) {
            return SPEAR;
        }
        return EMPTY;
    }

    /**
     * Return the contents of the square at COL ROW.
     */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /**
     * Set square S to P.
     */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /**
     * Set square (COL, ROW) to P.
     */
    final void put(Piece p, int col, int row) {
        Square square = Square.sq(col, row);
        if (p.toString().equals("W")) {
            this.board[col][row] = WHITE_QUEEN;
            whiteQueens.add(square);
        } else if (p.toString().equals("B")) {
            this.board[col][row] = BLACK_QUEEN;
            blackQueens.add(square);
        } else if (p.toString().equals("S")) {
            this.board[col][row] = ARROW;
            spears.add(square);
        } else {
            this.board[col][row] = EMP;
            whiteQueens.remove(square);
            blackQueens.remove(square);
        }
    }

    /**
     * Set square COL ROW to P.
     */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /**
     * Return true iff FROM - TO is an unblocked queen move on the current
     * board, ignoring the contents of ASEMPTY, if it is encountered.
     * For this to be true, FROM-TO must be a queen move and the
     * squares along it, other than FROM and ASEMPTY, must be
     * empty. ASEMPTY may be null, in which case it has no effect.
     */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (to == null) {
            return false;
        }
        if (!from.isQueenMove(to)) {
            throw new UnknownError("invalid move");
        }
        int limit = from.dis(to);
        for (int i = 1; i <= limit; i++) {
            Square path = from.queenMove(from.direction(to), i);
            if (get(path) != EMPTY && path != asEmpty) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true iff FROM is a valid starting square for a move.
     */
    boolean isLegal(Square from) {
        Piece cur = get(from);
        return cur != null && cur == _turn;
    }

    /**
     * Return true iff FROM-TO is a valid first part of move, ignoring
     * spear throwing.
     */
    boolean isLegal(Square from, Square to) {
        assert isLegal(from);
        assert isLegal(to);
        return isUnblockedMove(from, to, null);
    }

    /**
     * Return true iff FROM-TO(SPEAR) is a legal move in the current
     *      * position.
     */
    boolean isLegal(Square from, Square to, Square spear) {
        return isLegal(from) && isUnblockedMove(from, to, null)
                && isUnblockedMove(to, spear, from);
    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /**
     * Move FROM-TO(SPEAR), assuming this is a legal move.
     */
    void makeMove(Square from, Square to, Square spear) {
        if (!isLegal(from, to, spear)) {
            throw new UnknownError("Bad Move");
        }
        Piece f = get(from);
        put(f, to);
        put(EMPTY, from);
        put(SPEAR, spear);
        spears.add(spear);
        moves.add(Move.mv(from, to, spear));
        _winner = winner();
        if (_turn != _winner) {
            _turn = _turn.opponent();
        }
    }


    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    void makeMove(Move move) {
        assert isLegal(move);
        makeMove(move.from(), move.to(), move.spear());
    }

    /**
     * Undo one move.  Has no effect on the initial board.
     */
    void undo() {
        Move lastmove = moves.get(moves.size() - 1);
        Square s = lastmove.spear();
        Square from = lastmove.from();
        Square to = lastmove.to();
        Piece toobject = get(to);
        put(EMPTY, s);
        put(toobject, from);
        put(EMPTY, to);
        moves.remove(moves.size() - 1);
        spears.remove(s);
        _turn = _turn.opponent();
    }


    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
     *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    public class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = -1;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            Square ans = _from.queenMove(_dir, _steps);
            toNext();
            return ans;
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
            _steps += 1;
            Square target = _from.queenMove(_dir, _steps);
            while (_dir < 8 && ((get(target) != EMPTY && target != _asEmpty)
                    || target == null)) {
                _dir += 1;
                _steps = 1;
                target = _from.queenMove(_dir, _steps);
            }
        }

        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;

    }

    /** An iterator used by legalMoves. */
    class LegalMoveIterator implements Iterator<Move> {

        /** All legal moves for SIDE (WHITE or BLACK). */
        LegalMoveIterator(Piece side) {
            if (side == BLACK) {
                _startingSquares = new HashSet<>(blackQueens).iterator();
            } else {
                _startingSquares = new HashSet<>(whiteQueens).iterator();
            }
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _startingSquares.hasNext()
                    || _pieceMoves.hasNext() || _spearThrows.hasNext();
        }

        @Override
        public Move next() {
            Move ans = Move.mv(_start, _nextSquare, _spearThrows.next());
            toNext();
            return ans;
        }

        /** Advance so that the next valid Move is
         *  _start-_nextSquare(sp), where sp is the next value of
         *  _spearThrows. */
        private void toNext() {
            while (!_spearThrows.hasNext()) {
                if (_pieceMoves.hasNext()) {
                    _nextSquare = _pieceMoves.next();
                    _spearThrows = reachableFrom(_nextSquare, _start);
                } else if (_startingSquares.hasNext()) {
                    _start = _startingSquares.next();
                    _pieceMoves = reachableFrom(_start, null);
                    if (_pieceMoves.hasNext()) {
                        _nextSquare = _pieceMoves.next();
                        _spearThrows = reachableFrom(_nextSquare, _start);
                    }
                } else {
                    return;
                }
            }

        }

        /** Current starting square. */
        private Square _start;
        /** Remaining starting squares to consider. */
        private Iterator<Square> _startingSquares;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
    }

    @Override
    public String toString() {
        String ret = "";
        for (int row = SIZE - 1; row >= 0; row--) {
            ret += "   ";
            for (int col = 0; col < SIZE - 1; col++) {
                ret += get(col, row).toString() + " ";
            }
            ret += get(SIZE - 1, row).toString() + "\n";
        }
        return ret;
    }


    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
            Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
}
