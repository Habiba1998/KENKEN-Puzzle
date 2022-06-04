package sample;

// function used to compare algorithms for the report
public class CompareAlgorithms {

    public void Compare() {
        System.out.println("#######KENKEN BOARD");
        long start_backtrack;
        long start_forward = System.currentTimeMillis();
        long start_arc = System.currentTimeMillis();
        long time_backtrack = 0;
        long time_forward = 0;
        long time_arc = 0;

        //int size = 5;
        for (int i = 0; i < 100; i++) {
            System.out.println("Loop Number: " + (i + 1));
            KenKen_Generator k = new KenKen_Generator(6);
            int[][] solution_board = k.getBoard();
            Grid g = k.getG();
            Cell[][] grid_cells = g.getGrid_cells();
            //FORWARD CHECKING
            start_backtrack = System.currentTimeMillis();
            boolean result_forward_checking = g.csp_back_tracking(false, false);
            time_backtrack += System.currentTimeMillis() - start_backtrack;
//        if (result_forward_checking) {
//            System.out.println("HERE IS THE SOLUTION of Algorithm:");
//            k.print(solution_board);
//            System.out.println("HERE IS THE SOLUTION");
//            g.printResult();
//        }
            if (!result_forward_checking) {
                System.out.println("HERE IS THE SOLUTION:");
                k.print(solution_board);
                //boolean z = g.csp_back_tracking(false, false);
                System.out.println("No solution");
            }

            for (int j = 0; j < grid_cells.length; j++) {
                for (int l = 0; l < grid_cells.length; l++) {
                    grid_cells[j][l].initialize_domain(g.getGrid_length());
                    grid_cells[j][l].setValue(-1);
                }
            }
            start_forward = System.currentTimeMillis();
            result_forward_checking = g.csp_back_tracking(true, false);
            time_forward += System.currentTimeMillis() - start_forward;
            if (!result_forward_checking) {
                System.out.println("HERE IS THE SOLUTION:");
                k.print(solution_board);
                //boolean z = g.csp_back_tracking(false, false);
                System.out.println("No solution Forward");
            }
            for (int j = 0; j < grid_cells.length; j++) {
                for (int l = 0; l < grid_cells.length; l++) {
                    grid_cells[j][l].initialize_domain(g.getGrid_length());
                    grid_cells[j][l].setValue(-1);
                }
            }

            start_arc = System.currentTimeMillis();
            result_forward_checking = g.csp_back_tracking(false, true);
            time_arc += System.currentTimeMillis() - start_arc;
            if (!result_forward_checking) {
                System.out.println("HERE IS THE SOLUTION:");
                k.print(solution_board);
                //boolean z = g.csp_back_tracking(false, false);
                System.out.println("No solution Arc");
            }
            for (int j = 0; j < grid_cells.length; j++) {
                for (int l = 0; l < grid_cells.length; l++) {
                    grid_cells[j][l].initialize_domain(g.getGrid_length());
                    grid_cells[j][l].setValue(-1);
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Backtracking takes " + time_backtrack + "ms");
        System.out.println("Backtracking with forward checking takes " + time_forward + "ms");
        System.out.println("Backtracking with arc consistency takes " + time_arc + "ms");
        System.out.println("######KENKEN BOARD");
    }
}