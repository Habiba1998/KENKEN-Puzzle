package sample;

import java.util.*;

public class Grid {
    private Cell [][] grid_cells;                                                   // array of grid_cells
    private ArrayList<Constraint> grid_constraints = new ArrayList<Constraint>();   // array of all constraints on the grid
    private int grid_length;                                                        // the grid length or width


    public Grid(int grid_length)                                                    // Constructor
    {
        this.grid_length = grid_length;
        grid_cells = new Cell[grid_length][grid_length];                            // create array of all cells of grid


        for(int i = 0; i < grid_length; i++)                                        // Initialize each cell in the grid
        {
            for (int j = 0; j < grid_length; j++)
            {
                grid_cells[i][j] = new Cell(grid_length, i, j);
            }
        }

        for (int i = 0; i < grid_length; i++)                       // get the initial constraints
        {
            Cell [] row_cells = new Cell [grid_length];             // array of cell with row constraint
            Cell [] column_cells = new Cell [grid_length];          // array of cells with column constraint
            for (int j = 0; j < grid_length; j++)                   // get all the cells involved in row constraint
            {                                                       // or column constraint
                row_cells[j] = grid_cells[i][j];
                column_cells[j] = grid_cells[j][i];
            }
            // create row and column constraint, result attribute isn't needed here
            Constraint row_constraint = new Constraint(row_cells,Op.NotEqual, -1, grid_length);
            Constraint column_constraint = new Constraint(column_cells, Op.NotEqual, -1, grid_length);
            grid_constraints.add(row_constraint);                   // add row constraint to grid constraints
            grid_constraints.add(column_constraint);                // add column constraint to grid constraint

            for (int j = 0; j < grid_length; j++)                   // add row and column constraint to each cell involving it
            {
                grid_cells[i][j].addConstraint(row_constraint);
                grid_cells[j][i].addConstraint(column_constraint);
            }


        }

    }

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
    
     public boolean verifyAllConstraints()
    {
        boolean verify_result;
        for (int i = 0; i < grid_constraints.size(); i++)
        {
            verify_result = grid_constraints.get(i).verifyConstraint();     // use verifyConstraint fn in the constraint class
            if(! verify_result)
                return false;
        }
        return true;
    }
     
     
     public void save_domain(ArrayList<ArrayList<Integer>> backup_domain)             // Save domain before forward checking
    {                                                                                // or arc consistency
        for(int i = 0; i < grid_length; i++)
        {
            for (int j = 0; j < grid_length; j++)
            {
                ArrayList <Integer> shallowCopy = new ArrayList<Integer>(grid_cells[i][j].getDomain());
                backup_domain.add(shallowCopy);
            }
        }
    }

    public void reset_domain(ArrayList<ArrayList<Integer>> backup_domain)       // Reset domain if Forward checking or
    {                                                                           // arc consistency fail
        for(int i = 0; i < grid_length; i++)
        {
            for (int j = 0; j < grid_length; j++)
            {
                grid_cells[i][j].setDomain(backup_domain.get(grid_length * i + j));
            }
        }

    }


      // Used in arc consistency
    public boolean check_domain()                               // Check if all unassigned variables have one value
    {                                                           // in their domain if so, a solution is found
        boolean result_found = true;
        for (int i = 0; i < grid_length; i++)
        {
            for (int j = 0; j < grid_length; j++)
            {
                if (grid_cells[i][j].getValue() == -1 && grid_cells[i][j].getDomain_length() > 1)
                    result_found = false;
            }
        }
        if (result_found)
        {
            for (int i = 0; i < grid_length; i++)
            {
                for (int j = 0; j < grid_length; j++)
                {
                    if (grid_cells[i][j].getValue() == -1)
                        grid_cells[i][j].setDomain_index(0);        // Assign that value to the variable

                }
            }
        }
        return result_found;
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
    
    
     //--------------------------------------Arc Consistency-------------------------------------------------------------
    //--------------------------------------Remove inconsistent---------------------------------------------------------
    public boolean remove_inconsistent(Cell x,Cell y,Constraint current_constraint) //remove the values from domain of cell x causing inconsistency in some constraints common with cell y
    {
        boolean removed =false;
        boolean flag =false;    //flag to test if the value from domain x satisfies the constraint with a value from domain y is set to true
        boolean x_assigned = x.getValue() != -1;
        boolean y_assigned = y.getValue() != -1;
        int yi=0;
        boolean satisfy = false;
        for(int i = 0; i < x.getDomain_length(); i++)   // outer loop to iterate on x domain values
        {
            flag = false;
            if (!x_assigned)                                                // if x variable is assigned a value
            {                                                               // check the consistency of this value only
                x.setDomain_index(i);
            }

            for(int j = 0; j < y.getDomain_length(); j++)  // inner loop to iterate on y domain values
            {
                if (!y_assigned)
                {                                                           // if y variable is assigned a value
                    y.setDomain_index(j);                                   // check the consistency of this value only
                }

                satisfy = current_constraint.verifyConstraint(); // calling verifyConstraint function to test if the current constraint is satisfied with the assigned value to x and with changing y values
                if (satisfy)
                {
                    flag=true;
                    break; //break from the inner loop if one value has satisfied
                }
                else if (y_assigned)
                {
                    break;
                }
            }

            if (!flag)
            {

                if (x_assigned) {
                    x.removeDomain(x.getDomain_index()); // remove the value from the x domain
                }
                else {
                    x.removeDomain(i);
                    i--;
                }
                removed=true; // indicate that there is value removed from the x domain
            }
            if(x_assigned)
                break;

        }
        if (!x_assigned)                        // if x isn't assigned, make its value = -1 --> Mark as unassigned again
        {x.setValue(-1);}
        if(!y_assigned)
        {y.setValue(-1);}                       // if y isn't assigned, make its value = -1 --> Mark as unassigned again
        return removed;
    }
    //--------------------------------------AC3 function----------------------------------------------------------
    public boolean AC3 () // Arc consistency algorithm without parameter works on all the grid constraints
    {
        Queue<Cell> qcell1 = new LinkedList<Cell>(); // queue of cells for the first element of the pair of cells in the constraint
        Queue<Cell> qcell2 =  new LinkedList<Cell>(); // queue of cells for the second element of the pair of cells in the constraint
        Queue<Constraint> qconstraint = new LinkedList<Constraint>(); // queue of constraints that the pair of cells involved in
        for (int i=0;i<grid_constraints.size(); i++) // for loop to iterate on all constraints
        {
            if (grid_constraints.get(i).getCell_number() == 1)          // for constraints involving only one cell
            {                                                           // Update the cell domain
                ArrayList<Integer> new_domain = new ArrayList<>(1);
                new_domain.add((int)grid_constraints.get(i).getResult());
                grid_constraints.get(i).getCells()[0].setDomain(new_domain);
                continue;
            }
            for(int j=0;j<grid_constraints.get(i).getCell_number();j++) // for loop to iterate on all cells involved in the constraint
            {
                //for(int k=grid_constraints.get(i).getCell_number()-1;k>=0;k--) // for loop to iterate on all cells involved in the constraint to get a pair of cells
                for(int k=0;k<grid_constraints.get(i).getCell_number();k++)
                {
                    if(k!=j) // to check that the cell1 and cell2 aren't the same
                    {
                        qcell1.add(grid_constraints.get(i).getCells()[j]);
                        qcell2.add(grid_constraints.get(i).getCells()[k]);
                        qconstraint.add(grid_constraints.get(i));
                    }
                }
            }
        }
        while ((!qconstraint.isEmpty()) && (!qcell1.isEmpty()) && (!qcell2.isEmpty())) // check queues aren't empty
        {
            Cell cell1=qcell1.remove();
            Cell cell2=qcell2.remove();
            Constraint constraint=qconstraint.remove();
            if (remove_inconsistent(cell1,cell2,constraint)) // call remove_inconsistent function on the first element of the queues
            {
                if(cell1.getDomain_length()==0 || cell1.getValue() !=-1) // check if the domain length of variable = 0
                {                                                       // or a value is removed from an assigned variable
                    return false;
                }
                ArrayList<Constraint>neighbourconstraints=cell1.getConstraints(); // the constraints cell1 involved in
                for(int i=0;i<cell1.getConstraint_number();i++)
                {
                    //if(neighbourconstraints.get(i)!=constraint)
                    //{
                        for(int j=0;j<neighbourconstraints.get(i).getCell_number();j++)
                        {
                            if(neighbourconstraints.get(i).getCells()[j]!=cell1
                            && !(neighbourconstraints.get(i).getCells()[j]==cell2 && neighbourconstraints.get(i) == constraint))
                            {
                                qcell1.add(neighbourconstraints.get(i).getCells()[j]);
                                qcell2.add(cell1);
                                qconstraint.add(neighbourconstraints.get(i));
                            }
                        }
                   // }
                }
            }
        }
        return true;
    }
    //--------------------------------------AC3 function for a cell----------------------------------------------------------

    public boolean AC3_cell (Cell cell)
    {
        Queue<Cell> qcell1 = new LinkedList<Cell>(); // queue of cells for the first element of the pair of cells in the constraint
        Queue<Cell> qcell2 = new LinkedList<Cell>(); // queue of cells for the second element of the pair of cells in the constraint
        Queue<Constraint> qconstraint= new LinkedList<Constraint>(); // queue of constraints that the pair of cells involved in
        ArrayList<Constraint>neighbourconstraints=cell.getConstraints(); // the constraints cell involved in
        for (int i=0;i<cell.getConstraint_number(); i++) // for loop to iterate on the neighbour constraints
        {

            for(int j=0;j<neighbourconstraints.get(i).getCell_number();j++) // for loop to iterate on all cells involved in the constraint
            {
                //for(int k=neighbourconstraints.get(i).getCell_number()-1;k>=0;k--) // for loop to iterate on all cells involved in the constraint to form a pair
                    for(int k=0;k<neighbourconstraints.get(i).getCell_number();k++)
                    {
                    if(k!=j) // to check that the cell1 and cell2 aren't the same
                    {
                        qcell1.add(neighbourconstraints.get(i).getCells()[j]);
                        qcell2.add(neighbourconstraints.get(i).getCells()[k]);
                        qconstraint.add(neighbourconstraints.get(i));
                    }
                }
            }
        }
        while ((!qconstraint.isEmpty()) && (!qcell1.isEmpty()) && (!qcell2.isEmpty()))  // check queues aren't empty
        {
            Cell cell1=qcell1.remove();
            Cell cell2=qcell2.remove();
            Constraint constraint=qconstraint.remove();
            if (remove_inconsistent(cell1,cell2,constraint))  // call remove_inconsistent function on the first element of the queues
            {
                if(cell1.getDomain_length()==0 || cell1.getValue() !=-1)// check if the domain length of variable = 0
                {                                                       // or a value is removed from an assigned variable
                    return false;
                }
                for(int i=0;i<cell1.getConstraint_number();i++)
                {
                    //if(neighbourconstraints.get(i)!=constraint)
                    //{
                        for(int j=0;j<neighbourconstraints.get(i).getCell_number();j++)
                        {
                            if(neighbourconstraints.get(i).getCells()[j]!=cell1 &&
                                    !(neighbourconstraints.get(i).getCells()[j]==cell2 && neighbourconstraints.get(i) == constraint))
                            {
                                qcell1.add(neighbourconstraints.get(i).getCells()[j]);
                                qcell2.add(cell1);
                                qconstraint.add(neighbourconstraints.get(i));
                            }
                        }
                    //}
                }
            }
        }
        return true;
    }

    //-------------------------------------- Heuristics ----------------------------------------------------------------
    public void sort_cells_heuristics(ArrayList<Cell> available_cells, int start)
    {
        if (start >= available_cells.size() - 1)                                // return if one element is to be sorted
            return;
        // sort according to most constrained
        Collections.sort(available_cells.subList(start, available_cells.size()), Cell.mostconstrained);
        // get all max equally constrained cells
        int equally_constrained = 1;
        // get the minimum domain
        int min_domain = available_cells.get(start).getDomain_length();
        while(min_domain == available_cells.get(equally_constrained + start).getDomain_length())
        {
            equally_constrained ++;
            if((equally_constrained + start) == grid_length * grid_length) {
                break;
            }

        }

        if(equally_constrained > 1)                                         // use most constraining to break the tie
        {
            Collections.sort(available_cells.subList(start, start + equally_constrained), Cell.mostconstraining);
        }
    }

    //-------------------------------------Back-tracking with AC and FC flags ------------------------------------------
    public boolean back_tracking(ArrayList<Cell> available_cells, int index, boolean forward_check, boolean arc_consistent)
    {
        //this.printResult(); //for debugging
        if (available_cells.size() == index)                              // base condition: assignment is complete and satisfied
            return true;
        boolean constraint_satisfied = true;
        Cell cell = available_cells.get(index);
        for(int i = 0 ; i < cell.getDomain_length(); i++)
        {
            ArrayList<ArrayList<Integer>> backup_domain = new ArrayList<ArrayList<Integer>>();
            cell.setDomain_index(i);
            //this.printResult(); //for debugging
            if(forward_check)
            {
                save_domain(backup_domain);

                if(! forward_checking(cell, available_cells, index + 1))
                {
                    reset_domain(backup_domain);                         // if a cell has empty domain then: solution is refused

                    continue;                                            // we need to reset domain of all unassigned cells
                }                                                        // as they have values removed

                sort_cells_heuristics(available_cells, index + 1);  // sort unassigned cells as domains changed
            }
            else if(arc_consistent)
            {
                save_domain(backup_domain);
                if(! AC3_cell(cell))
                {
                    reset_domain(backup_domain);                         // if a cell has empty domain then: solution is refused
                    continue;                                            // we need to reset domain of all unassigned cells
                }                                                        // as they have values removed
                boolean v = check_domain();
                if (v)
                {
                    return true;
                }
                sort_cells_heuristics(available_cells, index + 1);  // sort unassigned cells as domains changed
            }
            else        // Backtracking only
            {
                constraint_satisfied = cell.verify_cell_constraint();      // check that the cell constraints are satisfied

            }
            if(constraint_satisfied || forward_check || arc_consistent)
            {
                constraint_satisfied = this.back_tracking(available_cells, index + 1, forward_check, arc_consistent);
                if(constraint_satisfied)
                    return true;
                if (forward_check || arc_consistent)
                    reset_domain(backup_domain);
            }

        }
        // This code is executed if we fail to find a value satisfying the constraints so backtrack
        cell.setValue(-1);                                                // Mark the cell as unassigned before backtracking
        return false;
    }


    //------------------------------------- Interface function used in main --------------------------------------------
    public boolean csp_back_tracking(boolean forward_check, boolean arc_consistent)          // back_tracking wrapper fn
    {
        if(arc_consistent)
        {
            if(AC3())
            {
                if(check_domain())
                {
                    return true;
                }

            }
            else
            {
                return false;
            }
        }
        //check on value of arcconsistency if true call AC3
        //if AC3 returns false return false else call checkdomain function if it returns true return true else complete the rest of the code
        ArrayList<Cell> available_cells = new ArrayList<Cell>();
        for(int i = 0; i < grid_length; i++)                               // Initially all grid cells are unassigned so add
        {                                                                  // to available_cells
            for (int j = 0; j < grid_length; j++)
            {
                available_cells.add(grid_cells[i][j]);
            }
        }
        sort_cells_heuristics(available_cells, 0);
        return this.back_tracking(available_cells, 0, forward_check, arc_consistent);

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
