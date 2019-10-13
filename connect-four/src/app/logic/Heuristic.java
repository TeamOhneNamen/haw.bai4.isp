package app.logic;

//Based on this idea: https://cs.stackexchange.com/a/13455
public class Heuristic {

    public static double determineScore(String playerColor){
        return determineHorizontalScore(playerColor);
        //TODO: vertikale Heuristik
        //TODO: Diagonale Heuristik
    }

    protected static double determineHorizontalScore(String playerColor) {
        int emptyLeftNeighbors = 0;
        int emptyRightNeighbors = 0;
        int inARow = 0;
        for (int i = 0; i < Board.rowLength(); i++) {
            int j = 0;
            int column;
            while (j < Board.columnLength()) {
                column = j;
                j += 1;
                // 1 in a row
                if (playerColor.equals(Board.get(i, column))) {
                    inARow++;
                    // space to the left
                    if(Board.NO_COLOR.equals(Board.get(i, column - 1))){
                        emptyLeftNeighbors++;
                        if(Board.NO_COLOR.equals(Board.get(i, column - 2))){
                            emptyLeftNeighbors++;
                            if(Board.NO_COLOR.equals(Board.get(i, column - 3))){
                                emptyLeftNeighbors++;
                            }
                        }
                    }
                    j += 2;
                    // 2 in a row
                    if (playerColor.equals(Board.get(i, column + 1))) {
                        inARow++;
                        j += 3;
                        // 3 in a row
                        if (playerColor.equals(Board.get(i, column + 2))) {
                            inARow++;
                            // 3 in a row with space to the right
                            if (Board.NO_COLOR.equals(Board.get(i, column + 3))) {
                                emptyRightNeighbors++;
                            }
                            System.out.println("3 in a row with space to the right");
                            j += 4;
                            // 2 in a row with 1 space to the right
                        } else if (Board.NO_COLOR.equals(Board.get(i, column+3))) {
                            emptyRightNeighbors++;
                            j += 4;
                            // 2 in a row with 2 space to the right
                            if (Board.NO_COLOR.equals(Board.get(i, column + 4))) {
                                emptyRightNeighbors++;
                                j += 5;
                            }
                        }
                        // 1 in a row with 1 space to the right
                    } else if (Board.NO_COLOR.equals(Board.get(i, column+2))) {
                        emptyRightNeighbors++;
                        j += 3;
                        // 1 in a row with 2 space to the right
                        if (Board.NO_COLOR.equals(Board.get(i, column + 3))) {
                            emptyRightNeighbors++;
                            j += 4;
                            // 1 in a row with 2 space to the right
                            if (Board.NO_COLOR.equals(Board.get(i, column + 4))) {
                                emptyRightNeighbors++;
                                j += 5;

                            }
                        }

                    }
                }

            }
        }
        return evaluate(emptyLeftNeighbors, emptyRightNeighbors, inARow);
    }

    private static double evaluate(int emptyLeftNeighbors, int emptyRightNeighbors, int inARow) {
        System.out.println("Left: "+ emptyLeftNeighbors + " Right: "+ emptyRightNeighbors + " In A Row: " + inARow);
        double score = 0.0;
        if(emptyLeftNeighbors+emptyRightNeighbors+inARow >3 ){
            score = inARow;
            if(emptyRightNeighbors > 0){
                score += 0.5;
            }
            if(emptyRightNeighbors > 0){
                score += 0.5;
            }
        }
        return score;
    }
}
