public class Replay {
    private String[][] table;

    Replay() {
        table = new String[3][3];
    }

    void replay(int player,int x, int y) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {

                //if (ParseXML.status[row][col] != null) {
                   if ((row == x) & (col == y)) {
                        table[x][y] = (player == 1 ? "x" : "o");
                    } else {
                       if (ParseXML.status[row][col]!=null) {
                           table[row][col] = ParseXML.status[row][col];
                       }else {
                           table[row][col] = "." ;
                       }
                    }

                    System.out.print(table[row][col] + " ");

                }
            System.out.println();
        }
    }
}
