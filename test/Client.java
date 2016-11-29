package test;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
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
				//Modifier ca pour implanter le MinMax
				 for (String myMove : moves) {
					 setHeuristique(myMove,false);
				 }
				 
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
				//Modifier ca pour implanter le MinMax
				 for (String myMove : moves) {
					 setHeuristique(myMove,false);
				 }
				 
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
	
	//Retourne tout les coups possibles dans le tour actuel
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
	
	public static double setHeuristique(String move, boolean isWhite){
		
		double heuristique = 0;
		double dist = 0;
		int myPawn;
		int ennemyPawn;
		
		if (isWhite) {
			myPawn = 4; // White
			ennemyPawn = 2;
		} else {
			myPawn = 2; // Black
			ennemyPawn = 4;
		}
		
		//trransformer le mouvement d'arrêt en valeur numérique
		//le A majuscule = 65 en ascii donc je réduit la valeur recu a 0 pour pouvoir être bien positionné dans le tableau
		//source : https://www.cs.cmu.edu/~pattis/15-1XX/common/handouts/ascii.html
		
		int posX = Character.getNumericValue(move.charAt(0)) - 65;
		int posY =  Character.getNumericValue(move.charAt(1));
		int posX2 = Character.getNumericValue(move.charAt(2)) - 65;
		int posY2 =  Character.getNumericValue(move.charAt(3));
		heuristique = calculerHeuristique(posX2,posY2,isWhite) - calculerHeuristique(posX,posY,isWhite);
		
		if(heuristique < 0){
			heuristique = 0;
		}
		System.out.println("Mouvement" + move);
		System.out.println(heuristique);
		return heuristique;
	}
	
	//VALEUR PROBABLEMENT À CHANGER
	//calcul l'heuristique pour un mouvement
	//+2 si distance est environ à 0
	//+1 si elle est a une ou 2 case du centre
	//+0.5 si 2 c'est a 2 cases et plus
	//+2 pour chacun de nos pions qui touche à la diagonale
	//+1 pour chacun de nos pions qui touche à l,horizontale verticale
	//+0.5 si on bouffe un pion
	
	public static double calculerHeuristique(int posX, int posY, boolean isWhite)
	{
		double heuristique = 0;
		double dist = 0;
		int myPawn;
		int ennemyPawn;
		
		if (isWhite) {
			myPawn = 4; // White
			ennemyPawn = 2;
		} else {
			myPawn = 2; // Black
			ennemyPawn = 4;
		}
		
		//Étape 1 :calcul de la distance par rapport au centre je sous entend que  4X4 est le centre
		
		//si la fin du mouvement est en bas à gauche je vise 3X3
		if(posX <= 3 && posY <= 3){
			dist =  Math.sqrt(Math.pow(3 - posX,2) + Math.pow(3 - posY,2));
		}
		
		//si la fin du mouvement est en haut à droite je vise 4X4
		else if(posX > 4 && posY > 4){
			dist =  Math.sqrt(Math.pow(4 - posX,2) + Math.pow(4 - posY,2));
		}
		
		//si le mouvement est en bas à droite je vise 4X3
		else if(posX > 4 && posY > 4){
			dist =  Math.sqrt(Math.pow(4 - posX,2) + Math.pow(3 - posY,2));
		}
		//sinon c'est en haut à gauche 3X4
		else{
			dist =  Math.sqrt(Math.pow(3 - posX,2) + Math.pow(4 - posY,2));
		}
		
		if(dist >= 0 || dist <=0.5){
			heuristique+=2;	
		}else if(dist > 0.5 || dist <=2){
			heuristique+=1;
		}else{
			heuristique+=0.5;
		}
		
		//Étape 2 regarder s'il y a des pièces de la même couleur autour du moment d'arrivé
		posY = Math.abs(posY - 8);
	
		//diagonale en haut a droite premier
		if(posX < 7 && posY < 7){
			if(board[posX + 1][posY + 1] == myPawn){
				heuristique+=2;
			}
		}
		
		//diagonale en haut à gauche
		if(posX > 0 && posY < 7){
			if(board[posX - 1][posY + 1] == myPawn){
				heuristique+=2;
			}
		}
		
		//diagonale en bas a droite
		if(posX < 7 && posY > 0){
			if(board[posX + 1][posY - 1] == myPawn){
				heuristique+=2;
			}	
		}
		if(posX < 7 && posY > 0){
			//diagonale en bas à gauche
			if(board[posX + 1][posY - 1] == myPawn){
				heuristique+=2;
			}	
		}
		
		if(posX < 7){
			//étape 3 regarder à l'horizontale
			if(board[posX + 1][posY] == myPawn){
				heuristique+=0.5;
			}
		}
		
		if(posX > 0){
			if(board[posX - 1][posY] == myPawn){
				heuristique+=0.5;
			}
		}
		
		//verticale
		if(posY < 7){
			if(board[posX][posY + 1] == myPawn){
				heuristique+=0.5;
			}	
		}
		
		if(posY > 0){
			if(board[posX][posY - 1] == myPawn){
				heuristique+=0.5;
			}
		}
		
		//si on bouffe un ennemie
		if(board[posX][posY] == ennemyPawn){
			heuristique+=0.2;
		}
		
		return heuristique;
	}
}
