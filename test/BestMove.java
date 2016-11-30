package test;

import java.util.Iterator;

public class BestMove {
	public String move;
	public double score;
	
	public BestMove() {
        move = null;
        score = 0;
    }
	
	public BestMove(String move, double score) {
        this.move = move;
        this.score = score;
    }

////M�thode temp trouv� sur internet
////Source : http://stackoverflow.com/questions/27527090/finding-the-best-move-using-minmax-with-alpha-beta-pruning
//protected BestMove MinMax(int alpha, int beta, int maxDepth,int playerColor) {
//	/*if (!canContinue()) {
//        return new BestMove();
//    } */
//	
//    //ArrayList<Move> moves = sortMoves(generateLegalMoves(player)); Implanter le générateur de mouvement ici
//	//Transformer en it�rateur
//    Iterator<Move> movesIterator = moves.iterator();
//    
//    
//    BestMove bestMove = new bestMove();
//    //Si le joueur est celui que notre algorithme controle , on maximise / Sinon On Minimise
//    boolean isMaximizer = (player.equals(playerType));
//    
//    //Pas compris cette partie la
//    if (maxDepth == 0 || board.isGameOver()) {            
//        moveValue.value = evaluateBoard();
//        return moveValue;
//    }
//    //-----------------------------------------
//    //Boucle sur les coups possibles -> c'est la qu'on va devoir set les heuristiques "La strat�gie".
//    while (movesIterator.hasNext()) {
//    	
//    }
//        Move currentMove = movesIterator.next();
//        board.applyMove(currentMove);
//        moveValue = minMax(alpha, beta, maxDepth - 1, player.opponent());
//        board.undoLastMove();
//        if (isMaximizer) {
//            if (moveValue.value > alpha) {
//                selectedMove = currentMove;
//                alpha = moveValue.value;
//            }
//        } else {
//            if (moveValue.value < beta) {
//                beta = moveValue.value;
//                selectedMove = currentMove;
//            }
//        }
//        if (alpha >= beta) {
//            break;
//        }
//    }
//    return (isMaximizer) ? new MoveValue(selectedMove, alpha) : new MoveValue(selectedMove, beta);
//}


/* Version plus optimal semble-t-il
 * protected MoveValue minMax(double alpha, double beta, int maxDepth, MarbleType player) {       
    if (!canContinue()) {
        return new MoveValue();
    }        
    ArrayList<Move> moves = sortMoves(generateLegalMoves(player));
    Iterator<Move> movesIterator = moves.iterator();
    double value = 0;
    boolean isMaximizer = (player.equals(playerType)); 
    if (maxDepth == 0 || board.isGameOver()) {            
        value = evaluateBoard();            
        return new MoveValue(value);
    }
    MoveValue returnMove;
    MoveValue bestMove = null;
    if (isMaximizer) {           
        while (movesIterator.hasNext()) {
            Move currentMove = movesIterator.next();
            board.applyMove(currentMove);
            returnMove = minMax(alpha, beta, maxDepth - 1, player.opponent());
            board.undoLastMove();
            if ((bestMove == null) || (bestMove.returnValue < returnMove.returnValue)) {
                bestMove = returnMove;
                bestMove.returnMove = currentMove;
            }
            if (returnMove.returnValue > alpha) {
                alpha = returnMove.returnValue;
                bestMove = returnMove;
            }
            if (beta <= alpha) {
                bestMove.returnValue = beta;
                bestMove.returnMove = null;
                return bestMove; // pruning
            }
        }
        return bestMove;
    } else {
        while (movesIterator.hasNext()) {
            Move currentMove = movesIterator.next();
            board.applyMove(currentMove);
            returnMove = minMax(alpha, beta, maxDepth - 1, player.opponent());
            board.undoLastMove();
            if ((bestMove == null) || (bestMove.returnValue > returnMove.returnValue)) {
                bestMove = returnMove;
                bestMove.returnMove = currentMove;
            }
            if (returnMove.returnValue < beta) {
                beta = returnMove.returnValue;
                bestMove = returnMove;
            }
            if (beta <= alpha) {
                bestMove.returnValue = alpha;
                bestMove.returnMove = null;
                return bestMove; // pruning
            }
        }
        return bestMove;
    }   
}*/
}