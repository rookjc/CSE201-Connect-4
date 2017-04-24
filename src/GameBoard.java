import java.awt.Color;

// Represents the current state of the game board, with 42 Pieces and 69 4-in-a-row Groups
// TODO: make this inherit from Panel
public class GameBoard {
	private Piece[][] board;
	private Groups[] groups;
	private ComputerPlayer computer;
	
	// Constructor for creating an empty GameBoard, along with all its component Pieces
	public GameBoard () {
		// Create grid of empty Pieces
		this.board = new Piece[6][7];
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				board[row][col] = new Piece(row, col);
			}
		}
		
		this.groups = new Groups[69];
		int groupNum = 0;
		// Generate all possible groups into this.groups array
		for (int row = 0; row < 6; row++) {		// Horizontal
			for (int col = 0; col < 4; col++)
				this.groups[groupNum++] = new Groups(this, this.getPiece(row, col), Groups.HORIZONTAL);
		}
		for (int row = 0; row < 3; row++) {		// Vertical
			for (int col = 0; col < 7; col++)
				this.groups[groupNum++] = new Groups(this, this.getPiece(row, col), Groups.VERTICAL);
		}
		for (int row = 0; row < 3; row++) {		// Diagonal up
			for (int col = 0; col < 4; col++)
				this.groups[groupNum++] = new Groups(this, this.getPiece(row, col), Groups.DIAGONAL_UP);
		}
		for (int row = 3; row < 6; row++) {		// Diagonal down
			for (int col = 0; col < 4; col++)
				this.groups[groupNum++] = new Groups(this, this.getPiece(row, col), Groups.DIAGONAL_DOWN);
		}
		
		// Create computer player
		this.computer = new ComputerPlayer(this);
	}
	
	// Revert this GameBoard back to an empty grid
	public void clearBoard() {
		// Set each of the 42 pieces to empty
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				board[row][col].setColor(Piece.EMPTY);
			}
		}
		GameState.playerTurn = true;
	}
	
	// Get the Piece located at a particular row and column
	public Piece getPiece(int row, int col) {
		return board[row][col];
	}
	
	// Get the Piece located right below the Piece at a particular row and column (or null if none exists)
	public Piece getPieceBelow(int row, int col) {
		return row == 0 ? null : board[row-1][col];
	}
	
	// Place a piece of a given pieceColor into the column indexed by col
	public boolean makeMove(int col, Color pieceColor) {
		int row = getColumnHeight(col);
		if (row > 5)
			return false;	// Column is full; invalid move
		
		board[row][col].setColor(pieceColor);
		return true;
	}
	
	// Gives the height of the stack of pieces in column number col
	public int getColumnHeight(int col) {
		for (int row = 0; row < 6; row++) {
			if (board[row][col].isEmpty())
				return row;
		}
		return 6;
	}
	
	// Get the number of pieces that must be placed in a piece's column,
	// before that piece itself can be a valid move
	public int getBuildUp(Piece p) {
		return p.row - getColumnHeight(p.col);
	}
	
	// Check if either player has won, or the board is full, giving the proper GameState constant back
	public int checkGameOver() {
		// Check all potential groups for wins
		for (Groups s : groups) {
			Color winner = s.getWinningColor();
			if (winner.equals(Piece.RED)) {
				s.setAllColors(Piece.RED_WIN);	// Show where the win is
				GameState.addRedWin();
				return GameState.playerIsRed ? GameState.PLAYERWON : GameState.PLAYERLOST;
			} else if (winner.equals(Piece.YELLOW)) {
				s.setAllColors(Piece.YELLOW_WIN);	// Show where the win is
				GameState.addYellowWin();
				return GameState.playerIsRed ? GameState.PLAYERLOST : GameState.PLAYERWON;
			}
		}
		
		// If no one has won, is the board full?
		for (int col = 0; col < 7; col++) {
			if (getColumnHeight(col) < 6)
				return GameState.NORMAL;	// Not full, there is still a space left
		}
		// Board full, game is a draw
		GameState.addDraw();
		return GameState.DRAW;
	}

	// Process an attempted move from the player. Return a GameState constant indicating win/loss/draw
	public int playerClick(int col) {
		System.out.println("player moved in col: " + col);	// temporary
		if (GameState.playerTurn) {
			GameState.playerTurn = false;
			if (makeMove(col, GameState.playerIsRed ? Piece.RED : Piece.YELLOW)) {
				// Check if player wins
				int state = checkGameOver();
				if (state != GameState.NORMAL)
					return state;
								
				// Make computer move, and check if it wins
				computer.makeMove();
				state = checkGameOver();
				if (state != GameState.NORMAL)
					return state;
			} else {
				return GameState.INVALIDMOVE;
			}
		}
		GameState.playerTurn = true;
		return GameState.NORMAL;
	}
	
	// Getter for the slot array
	public Groups[] getGroups() {
		return this.groups;
	}
	
}