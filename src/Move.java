
public class Move {
	private String startX;
	private String startY;
	private String endX;
	private String endY;
	private int[] coords;
	private int score;
	
	public Move(String startX, int startY, String endX, int endY) {
		this.startX = startX;
		this.startY = intToChar(startY);
		this.endX = endX;
		this.endY = intToChar(endY);
		this.coords = new int[] {charToInt(startX), startY, charToInt(endX), endY};
	}
	
	public Move(String startX, String startY, String endX, String endY) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.coords = new int[] {charToInt(startX), charToInt(startY), charToInt(endX), charToInt(endY)};
	}
	
	public String getMoveCommand() {
		return this.startX + this.startY + this.endX + this.endY; 
	}
	
	public String getReverseMoveCommand() {
		return this.endX + this.endY + this.startX + this.startY; 
	}
	
	public int getScore() {
		return this.score;
	}
	
	public int[] getCoords() {
		return this.coords;
	}
	
	private String intToChar(int i) {
		String [] chars = new String[] {"A", "B", "C", "D", "E", "F", "G", "H" };
		return chars[i];
	}
	
	private int charToInt(String c) {
		String [] chars = new String[] {"A", "B", "C", "D", "E", "F", "G", "H" };
		for (int i = 0; i < chars.length; i++) {
			if (c.equalsIgnoreCase(chars[i]))
				return i;
		}
		return -1;
	}
}
