package sample;

import java.util.*;

public class Grid {
    private Cell [][] grid_cells;                                                   // array of grid_cells
    private ArrayList<Constraint> grid_constraints = new ArrayList<Constraint>();   // array of all constraints on the grid
    private int grid_length;                                                        // the grid length or width



    // Setters and Getters
    public int getGrid_length() {
        return grid_length;
    }
    public ArrayList<Constraint> getConstraints(){return grid_constraints;}
    public Cell[][] getGrid_cells() {
        return grid_cells;
    }
    public void setGrid_cells(Cell[][] grid_cells) {
        this.grid_cells = grid_cells;
    }


    // ------------------------------------ Forward checking -----------------------------------------------------------
    public boolean forward_checking(Cell c, ArrayList<Cell> unassigned_cells, int start)
    {
        ArrayList<Constraint> constraints = c.getConstraints();
        for(int i = 0; i < c.getConstraint_number(); i++)           // loop over all constraints involving cell c
        {
            Constraint current_constraint = constraints.get(i);
            Cell [] cells = current_constraint.getCells();
            if (current_constraint.getCell_number() == 1)           // if constraint has only one variable
           {
                if (c.getValue() != current_constraint.getResult())
                    return false;
            }
            for (int j = 0; j < constraints.get(i).getCell_number(); j++) // for each cell in the constraint
            {
                if (unassigned_cells.indexOf(cells[j]) >= start)          // check that the cell is unassigned
                {
                    for (int k = 0; k < cells[j].getDomain_length(); k++) // loop over all values in the domain of the unassigned
                    {                                                     // cell
                        cells[j].setDomain_index(k);
                        boolean satisfy = current_constraint.verifyConstraint();
                        if (! satisfy)
                        {
                            cells[j].removeDomain(k);                   // remove values from the domain that doesn't satisfy the
                            k -- ;                                      // constraints
                        }

                    }
                    cells[j].setValue(-1);                              // this cell is unassigned so reset its value to -1
                }

                if (cells[j].getDomain_length() <= 0)                   // if a cell has empty domain return false
                    return false;
            }
        }

        return true;
    }

    //-------------------------------------- Printing the Grid content -------------------------------------------------
    public void printResult()                                                   // print grid values for debugging
    {
        for (int i = 0 ; i < grid_length; i++)
        {
            for (int j = 0 ; j < grid_length; j++)
            {
                System.out.print(grid_cells[i][j].getValue() + " ");
            }
            System.out.println();
        }
        System.out.println("");
    }
}
