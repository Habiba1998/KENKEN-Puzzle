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
