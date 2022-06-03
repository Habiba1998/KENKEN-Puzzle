package sample;


import java.util.ArrayList;

class KenKen_Generator {
    int[][] board;
    private int size;
    Grid g;
    Cell[][] grid_cells;

    KenKen_Generator(int size) {
        assert size > 0;
        this.size = size;
        this.g = new Grid(size);
        this.grid_cells = g.getGrid_cells();
        fill();
        randomize();
        //print(board);
        Randomize_solution(size, board);
    }

    void fill() {
        board = new int[size][size];
        for (int c = 0; c < size; c++) {
            for (int r = 0; r < size; r++) {
                board[c][r] = c + r + 1;
                while (board[c][r] > size)
                    board[c][r] -= size;
            }
        }
    }

    private void randomize() {
        //swap between rows
        for (int i = 0; i < 2 * size; i++) {
            int i1 = (int) (Math.random() * size);
            int i2 = (int) (Math.random() * size);
            int[] tmp = board[i1];
            board[i1] = board[i2];
            board[i2] = tmp;
        }


        //swap between columns
        for (int i = 0; i < 2 * size; i++) {
            int i1 = (int) (Math.random() * size);
            int i2 = (int) (Math.random() * size);
            for (int c = 0; c < size; c++) {
                int tmp = board[c][i1];
                board[c][i1] = board[c][i2];
                board[c][i2] = tmp;
            }
        }
    }

    void print(int[][] matrix_to_print) {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                System.out.print(matrix_to_print[r][c] + "  ");
            }
            System.out.println();
        }
    }



            //random start pt, index 8 in 1D, 8/3 > 2  row in 2D, 8 % 3 = 2 , column  = 2
//            System.out.println("############### INFO NEEDED ############");
//
//            System.out.println("Number of Cells: " + cells_count);
//            System.out.println("Operation: " + random_op);
//            System.out.println("Result is " + result);
//            System.out.println("############## INFO NEEDED ############");
            Cell[] arr_to_be = new Cell[a.size()];
            arr_to_be = a.toArray(arr_to_be);

            if (random_op == 0) {
                Constraint c = new Constraint(arr_to_be, Op.Plus, result, cells_count);
                g.addConstraint(c);

            } else if (random_op == 1) {
                Constraint c = new Constraint(arr_to_be, Op.Times, result, cells_count);
                g.addConstraint(c);
            } else if (random_op == 2) {
                Constraint c = new Constraint(arr_to_be, Op.Minus, result, cells_count);
                g.addConstraint(c);
            } else {
                Constraint c = new Constraint(arr_to_be, Op.Divided, result, cells_count);
                g.addConstraint(c);
            }

            a = new ArrayList<Cell>();

//
//            for (int i = 0; i < size; i++) {
//                System.out.print(arr[i] + " ");
//            }
//            System.out.println();
//            System.out.println();


        }
        //endwhile

    }


    public Grid getG() {
        return g;
    }

    public int[][] getBoard() {
        return board;
    }
}
