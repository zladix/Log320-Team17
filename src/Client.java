import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;


class Client {
	private static int[][] tempBoard;
	static int[][] board = new int[8][8];
	private static int mark[][] = new int[8][8];
	
	public static void main(String[] args) {
         
	Socket MyClient;
	BufferedInputStream input;
	BufferedOutputStream output;
    
	try {
		MyClient = new Socket("localhost", 8888);
	   	input    = new BufferedInputStream(MyClient.getInputStream());
		output   = new BufferedOutputStream(MyClient.getOutputStream());
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));  
	   	while(true){
			char cmd = 0;
		   	
            cmd = (char)input.read();
            		
            // Début de la partie en joueur blanc
            if(cmd == '1'){
                byte[] aBuffer = new byte[1024];
				int size = input.available();
				//System.out.println("size " + size);
				input.read(aBuffer,0,size);
                String s = new String(aBuffer).trim();
                System.out.println(s);
                String[] boardValues;
                boardValues = s.split(" ");
                int x=0,y=0;
                for(int i=0; i<boardValues.length;i++){
                    board[x][y] = Integer.parseInt(boardValues[i]);
                    x++;
                    if(x == 8){
                        x = 0;
                        y++;
                    }
                }

                System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
                String move = null;
                move = console.readLine();
                
                tempBoard = board;
                board = updateBoard(move);
                
                printBoard();
                
				output.write(move.getBytes(),0,move.length());
				output.flush();
            }
            // Début de la partie en joueur Noir
            if(cmd == '2'){
                System.out.println("Nouvelle partie! Vous jouez noir, attendez le coup des blancs");
                byte[] aBuffer = new byte[1024];
				
				int size = input.available();
				//System.out.println("size " + size);
				input.read(aBuffer,0,size);
                String s = new String(aBuffer).trim();
                System.out.println(s);
                
                String[] boardValues;
                boardValues = s.split(" ");
                int x=0,y=0;
                for(int i=0; i<boardValues.length;i++){
                    board[x][y] = Integer.parseInt(boardValues[i]);
                    x++;
                    if(x == 8){
                        x = 0;
                        y++;
                    }
                }
            }

			// Le serveur demande le prochain coup
			// Le message contient aussi le dernier coup joué.
			if(cmd == '3'){
				byte[] aBuffer = new byte[16];
				
				int size = input.available();
				//System.out.println("size " + size);
				input.read(aBuffer,0,size);
				
				String s = new String(aBuffer);
				System.out.println("Dernier coup : "+ s);
				tempBoard = board;
				board = updateBoard(s);
		       	//System.out.println("Entrez votre coup : ");
				String move = null;
				//move = console.readLine();
				String [] moves = validMoves(false);
				move = moves[randInt(moves.length)];
				System.out.println("Move : " + move);
				tempBoard = board;
				board = updateBoard(move);
				printBoard();
				output.write(move.getBytes(),0,move.length());
				output.flush();
				
			}
			// Le dernier coup est invalide
			if(cmd == '4'){
				board = tempBoard; // Annuler le coup pour l'affichage
				System.out.println("Coup invalide, entrez un nouveau coup : ");

				String move = null;
				//move = console.readLine();
				String [] moves = validMoves(false);
				move = moves[randInt(moves.length)];

				System.out.println("Move : " + move);
				
				tempBoard = board;
				board = updateBoard(move);
				printBoard();
				output.write(move.getBytes(),0,move.length());
				output.flush();
				
			}
        }
	}
	catch (IOException e) {
   		System.out.println(e);
	}
	
    }
	
	private static void printBoard() {
		String[] icons = {" ", "•", "o"};
		System.out.println();
		for (int i = 7; i >= 0; i--) {
			for (int j = 0; j < board[i].length; j++) {
				int index = (board[j][i] == 0) ? 0 : board[j][i]/2;
				System.out.print("["+icons[index]+"]");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	private static String[] validMoves(boolean isWhitePlayer) {
		ArrayList<String> moves = new ArrayList<String>();
		int myPawn;
		int enemyPawn;
		boolean isValid = false;
		int moveLength = 0;
		
		if (isWhitePlayer) {
			myPawn = 4; // White
			enemyPawn = 2; // Black
		} else {
			myPawn = 2; // Black
			enemyPawn = 4; // White
		}


		//parcours le tableau en x y
		for (int x = 0; x != 8; ++x) {
			for (int y = 0; y != 8; ++y) {

				//si c'est un de nos pions
				if (board[x][y] == myPawn) {
					moveLength = 0;
					//parcours la ligne du pions et compte le nombres de pions
					for (int i = 0; i != 8; ++i) {
						if (board[i][y] != 0) {
							++moveLength;
						}
					}
					
					isValid = true;
					//Si la case en x est plus petite que la longeur du mouvement possible alors le mouvement nest pas valide
					if (x - moveLength < 0) {
						isValid = false;
					//sinon  si c'est un de nos pions qui est à la position du mouvement
					} else if (board[x - moveLength][y] == myPawn) {
						isValid = false;
					} else {
						//si on passe par dessus un pions adverse le mouvement est invalide
						for (int i = 1; i != moveLength; ++i) {
							if (board[x - i][y] == enemyPawn) {
								isValid = false;
							}
						}
					}

					//si le mouvement est valide on l'ajoute
					if (isValid) {
						moves.add(intToChar(x) + (y+1) + intToChar(x - moveLength) + (y+1));
					}
					
					isValid = true;
					//si on dépasse le côté droit du tableau le mouvement est pas valide
					if (x + moveLength >= 8) {
						isValid = false;
						//si y'as notre pions à la position d'arrivé notre pion n'est pas valide
					} else if (board[x + moveLength][y] == myPawn) {
						isValid = false;

					} else {
						//si y'as un pion advee à la droite du mouvement il est pas valide
						for (int i = 1; i != moveLength; ++i) {
							if (board[x + i][y] == enemyPawn) {
								isValid = false;
							}
						}
					}
					//ajout du mouvement
					if (isValid) {
						moves.add(intToChar(x) + (y+1) + intToChar(x + moveLength) + (y+1));
					}

					//même logique en y par en haut
					moveLength = 0;
					for (int i = 0; i != 8; ++i) {
						if (board[x][i] != 0) {
							++moveLength;
						}
					}
					
					isValid = true;
					if (y - moveLength < 0) {
						isValid = false;
					} else if (board[x][y - moveLength] == myPawn) {
						isValid = false;
					} else {
						for (int i = 1; i != moveLength; ++i) {
							if (board[x][y - i] == enemyPawn) {
								isValid = false;
							}
						}
					}
					
					if (isValid) {
						moves.add(intToChar(x) + (y+1) + intToChar(x) + ((y+1) - moveLength));
					}
					
					//mouvement vers le bas
					isValid = true;
					if (y + moveLength >= 8) {
						isValid = false;
					} else if (board[x][y + moveLength] == myPawn) {
						isValid = false;
					} else {
						for (int i = 1; i != moveLength; ++i) {
							if (board[x][y + i] == enemyPawn) {
								isValid = false;
							}
						}
					}
					
					if (isValid) {
						moves.add(intToChar(x) + (y+1) + intToChar(x) + ((y+1) + moveLength));
					}

					//diagonale nw se
					moveLength = 0;
					if (x >= y) {
						for (int i = 0; i != 8 - (x - y); ++i) {
							if (board[x - y + i][i] != 0) {
								++moveLength;
							}
						}
					} else {
						for (int i = 0; i != 8 - (y - x); ++i) {
							if (board[i][y - x + i] != 0) {
								++moveLength;
							}
						}
					}
					
					isValid = true;
					//diago si dépasse le tableau en xy
					if (y - moveLength < 0 || x - moveLength < 0) {
						isValid = false;
					//si c'est un de nos pions à la destination
					} else if (board[x - moveLength][y - moveLength] == myPawn) {
						isValid = false;
					//si on passe par dessus un pion adverse
					} else {
						for (int i = 1; i != moveLength; ++i) {
							if (board[x - i][y - i] == enemyPawn) {
								isValid = false;
							}
						}
					}
					
					if (isValid) {
						moves.add(intToChar(x)+ (y+1) + intToChar(x - moveLength)+ ((y+1) - moveLength));
					}
					// diagonale ne sw
					isValid = true;
					if (y + moveLength >= 8 || x + moveLength >= 8) {
						isValid = false;
					} else if (board[x + moveLength][y + moveLength] == myPawn) {
						isValid = false;
					} else {
						for (int i = 1; i != moveLength; ++i) {
							if (board[x + i][y + i] == enemyPawn) {
								isValid = false;
							}
						}
					}
					
					if (isValid) {
						moves.add(intToChar(x) + (y+1) + intToChar(x + moveLength) + ((y+1) + moveLength));
					}
					
					moveLength = 0;
					///regarder pas sur c'est quoi????
					if (x >= 8 - y - 1) {
						for (int i = 0; i != 8 - (x - (8 - y - 1)); ++i) {
							if (board[x - (8 - y - 1) + i][8 - i - 1] != 0) {
								++moveLength;
							}
						}
					} else {
						for (int i = 0; i != y + x + 1; ++i) {
							if (board[i][y + x - i] != 0) {
								++moveLength;
							}
						}
					}
					
					isValid = true;
					if (y + moveLength >= 8 || x - moveLength < 0) {
						isValid = false;
					} else if (board[x - moveLength][y + moveLength] == myPawn) {
						isValid = false;
					} else {
						for (int i = 1; i != moveLength; ++i) {
							if (board[x - i][y + i] == enemyPawn) {
								isValid = false;
							}
						}
					}
					
					if (isValid) {
						moves.add(intToChar(x) + (y+1) + intToChar(x - moveLength) + ((y+1) + moveLength));
					}
					
					isValid = true;
					if (y - moveLength < 0 || x + moveLength >= 8) {
						isValid = false;
					} else if (board[x + moveLength][y - moveLength] == myPawn) {
						isValid = false;
					} else {
						for (int i = 1; i != moveLength; ++i) {
							if (board[x + i][y - i] == enemyPawn) {
								isValid = false;
							}
						}
					}
					
					if (isValid) {
						moves.add(intToChar(x) + (y+1) + intToChar(x + moveLength) + ((y+1) - moveLength));
					}
				}
			}
		}
		
		return moves.toArray(new String[moves.size()]);
	}
	
	private static void placeHeuristics() {
		
	}
	
	// http://www.cis.upenn.edu/~cis110/14fa/hw/hw09/FloodFill.java
	private static void getTouchingNodes(int x, int y, int src, int target) {
		if (x < 0 || y < 0) return;
		if (x > 7 || y > 7) return;
		if (mark[x][y] == 1) return; // Si ces coordonnees ont deja ete vues
		if (board[x][y] != src) return;
		
		mark[x][y] = target;
		
		getTouchingNodes(x - 1, y, src, target);// w
		getTouchingNodes(x + 1, y, src, target);// e
		getTouchingNodes(x, y + 1, src, target);// n
		getTouchingNodes(x, y - 1, src, target);// s
		getTouchingNodes(x - 1, y - 1, src, target);// sw
		getTouchingNodes(x + 1, y + 1, src, target);// ne
		getTouchingNodes(x - 1, y + 1, src, target);// nw
		getTouchingNodes(x + 1, y - 1, src, target);// se
	}
	
	private final static String intToChar(int i) {
		String [] chars = new String[] {"A", "B", "C", "D", "E", "F", "G", "H" };
		return chars[i];
	}
	
	private static int[][] updateBoard(String move) {
		move = move.trim();
		int c1=0, c2=0, c3=0, c4=0;
		if (move.length() > 4) { // format A2 - C2
			c1 = getCharValue(move.charAt(0)) - 1;
			c2 = Character.getNumericValue(move.charAt(1)) - 1;
			c3 = getCharValue(move.charAt(5)) - 1;
			c4 = Character.getNumericValue(move.charAt(6)) - 1;
		} else if (move.length() == 4) { // format A2C2
			c1 = getCharValue(move.charAt(0)) - 1;
			c2 = Character.getNumericValue(move.charAt(1)) - 1;
			c3 = getCharValue(move.charAt(2)) - 1;
			c4 = Character.getNumericValue(move.charAt(3)) - 1;
		}
		board[c3][c4] = board[c1][c2];
		board[c1][c2] = 0;
		return board;
	}
	
	private static int getCharValue(char c) {
        int temp = (int)c;
        int temp_integer = 64; // Majuscules
        return (temp-temp_integer);
	}
	
	private static int randInt(int max) {
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max-1) + 1);

	    return randomNum;
	}
}
