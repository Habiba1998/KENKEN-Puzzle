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


    void Randomize_solution(int length, int[][] board) { ////////////////////////////////////passing an array
        //initialize array of size given by user with zeroes eg.3*3       size=9

        ArrayList<Cell> a = new ArrayList<Cell>();
        int dimension = length;
        int size = length * length;
        int count = 0;
        int random_cageSize;
        int random_startPt;
        int random_op;
        int cells_count;
        int result = 0;
        int max = 0;
        int no1 = 0;
        int no2 = 0;
        int first = 0;
        int second = 0;
        int[] cage;
        int[] arr = new int[size];

        //arrays for constraint creation:


        //initialize array with zeros
        for (int i = 0; i < size; i++) {
            arr[i] = 0;
        }
        while (count != size) {
            cells_count = 0;
            if (dimension>5)
                random_cageSize = (int) ((Math.random() * 5) + 1); //1 2 3 4 5
            else
                random_cageSize = (int) ((Math.random() * dimension) + 1); //(Math.random() * (max - min)) + min);
            cage = new int[random_cageSize]; //a dynamic array to store cells of each cage
//            System.out.println("Cage Size is " + random_cageSize);
            random_startPt = (int) (Math.random() * size);          //0 1 3 ...15
            while (arr[random_startPt] != 0) {
                random_startPt = (int) (Math.random() * size);
            }
//            System.out.println("Start Pt is " + random_startPt);
            for (int i = 0; i < random_cageSize; i++) {

                arr[random_startPt] = 1;
                //dimension of  3x3 = 3
                //random start pt, index 8 in 1D, 8/3 > 2  row in 2D, 8 % 3 = 2 , column  = 2
                // index , result , operation = random_op, cellcount for constraint,

                cage[i] = board[random_startPt / dimension][random_startPt % dimension];
//                System.out.println("cage[i] " + cage[i]);

                a.add(grid_cells[random_startPt / dimension][random_startPt % dimension]);

//                System.out.println("#######INDEX#########");
//                System.out.println("Cells Index Row: " + random_startPt / dimension);
//                System.out.println("Cells Index Column: " + random_startPt % dimension);
//                System.out.println("#######INDEX#########");

                count++; //this is the condition if it is equal 16 , then we will not enter the while loop again
                cells_count += 1;
                if ((random_startPt + 1 >= 0 && random_startPt + 1 < size) && arr[random_startPt + 1] == 0 && ((random_startPt + 1) % dimension != 0)) { //give priority to direction right
                    random_startPt += 1;
                } else if ((random_startPt + dimension >= 0 && random_startPt + dimension < size) && arr[random_startPt + dimension] == 0) {
                    random_startPt += dimension;
                } else if ((random_startPt - 1 >= 0 && random_startPt - 1 < size) && arr[random_startPt - 1] == 0 && (random_startPt % dimension != 0)) {
                    random_startPt -= 1; //handles the case if i am in position 12 and want to go back
                } else if ((random_startPt - dimension >= 0 && random_startPt - dimension < size) && arr[random_startPt - dimension] == 0) {
                    random_startPt -= dimension;
                } else
                    break;

            } //endfor

            //


            //
            //System.out.println("number of cells " + cells_count);
            if (cells_count == 1)
                random_op = 0; //+
            else if (cells_count == 2) // + * - / can be used
                random_op = (int) (Math.random() * 4); //0 1 2 3
                //random_op=3;
            else
                random_op = (int) (Math.random() * 2); //0 1

            if (random_op == 0 || random_op == 2) { //+ or -
                result = 0;
            } else if (random_op == 1 || random_op == 3) {
                result = 1;
            }

//            for (int i = 0; i < cells_count; i++) {
//                System.out.print(cage[i] + " ");
//            }
//            System.out.println();

            for (int i = 0; i < cells_count; i++) {
                //System.out.println("random_op " + random_op);
                // 0:+ 1:*  2:-  3:/
                if (random_op == 0) { // +
                    result += cage[i];
                } else if (random_op == 1) { // *
                    result *= cage[i];
                } else if (random_op == 2) { // -
                    result -= cage[i];
                    if (result < 0) { //-ve while subtracting
                        result = Math.abs(result);
                    }
                } else if (random_op == 3) { // /      cells_count=2 for sure
                    if (i == 0)
                        no1 = cage[i];
                    else {
                        no2 = cage[i];
                        if ((no1 % no2 != 0) && (no2 % no1 != 0)) {
                            random_op = (int) (Math.random() * 3); //0 1 2   + * -
                            //System.out.println(random_op);
                            if (random_op == 0) {
                                result = no1 + no2;
                            } else if (random_op == 1) {
                                result = no1 * no2;
                            } else {
                                first = Math.max(no1, no2);
                                second = Math.min(no1, no2);
                                result = first - second;
                            }
                        } else {
                            first = Math.max(no1, no2);
                            second = Math.min(no1, no2);
                            result = first / second;
                        }

                    }
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
