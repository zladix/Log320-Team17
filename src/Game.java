
public class Game {
	public static final int EMPTY = 0;
	public static final int BLACK = 2;
	public static final int WHITE = 4;
	private int[][] board;
	private Move lastMove;
	
	public Game() {
		board = new int[8][8];
	}

	public void makeMove(Move move) {
		this.lastMove = move;
		int[] coords = move.getCoords();
		board[coords[2]][coords[3]] = board[coords[0]][coords[1]];
		board[coords[0]][coords[1]] = 0;
	}
	
	public void rollbackMove() {
		int[] coords = this.lastMove.getCoords();
		// TODO: ajouter check si avait "mangé" une pièce
		// Sinon, revenir à la méthode où on stock le board au complet.
		board[coords[0]][coords[1]] = board[coords[2]][coords[3]];
		board[coords[2]][coords[3]] = 0;
	}
	
	public void printBoard() {
		String[] icons = {" ", "•", "o"};
		System.out.println();
		for (int i = 7; i >= 0; i--) {
			for (int j = 0; j < 8; j++) {
				int index = (board[j][i] == 0) ? 0 : board[j][i]/2;
				System.out.print("["+icons[index]+"]");
			}
			System.out.println();
		}
		System.out.println();
	}
	
}
