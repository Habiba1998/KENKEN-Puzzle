package sample;

import java.util.ArrayList;
import java.util.Comparator;

public class Cell {
    private ArrayList<Integer> domain = new ArrayList<Integer>();               // list of available values in the cell
    private ArrayList<Constraint> constraints = new ArrayList<Constraint>();       // list of the constraints involving the cell
    private int domain_length;                                            // the number of values in the domain
    private int value;                                                    // the value currently assigned to cell unassigned = -1
    private int domain_index;                                             // index of current value assigned to the domain
    private int constraint_number;                                        // the number of constraints the cell involved in
    int row;
    int column;


    public Cell(int grid_length, int r, int c)                                                // Constructor
    {
        this.initialize_domain(grid_length);
        // Set the domain_length to the grid_length
        this.domain_length = grid_length;
        this.value = -1;
        this.constraint_number = 0;
        this.row = r;
        this.column = c;
    }

    public void initialize_domain(int grid_length)
    {
        // Initialize the domain of the cell to the grid length. eg: grid 3 x 3, the domain of each cell is 1 to 3
        this.domain.clear();
        for (int i = 1; i <= grid_length; i++)
        {
            this.domain.add(i);
        }
        this.domain_length = grid_length;
    }
    public void removeDomain (int index)                                  // remove element from domain given its index
    {
        this.domain.remove(index);
        this.domain_length -- ;

    }

    public void addConstraint(Constraint c)                              // add a constraint involving cell to constraints list
    {
        this.constraints.add(c);
        this.constraint_number ++;
    }

    public boolean verify_cell_constraint()
    {
        boolean result;
        for (int i = 0; i < constraint_number; i++)
        {
            result = constraints.get(i).verifyConstraint();
            if (result == false)
            {
                return false;
            }
        }
        return true;
    }

    // Comparators and sorting functions
    // for more details refer to https://www.geeksforgeeks.org/how-to-sort-an-arraylist-of-objects-by-property-in-java/
    public static Comparator<Cell> mostconstrained = new Comparator<Cell>(){
        public int compare(Cell c1, Cell c2)
        {
            return c1.domain_length - c2.domain_length;                     // sort from most to least constrained
        }
    };

    public static Comparator<Cell> mostconstraining = new Comparator<Cell>(){
        public int compare(Cell c1, Cell c2)
        {
            return c2.constraint_number - c1.constraint_number;           // sort from most to least constraining
        }
    };


    // Setters and Getters
    public ArrayList<Integer> getDomain() {
        return domain;
    }

    public void setDomain(ArrayList<Integer> domain) {
        this.domain = domain;
        this.domain_length = domain.size();
    }

    public int getDomain_index() {
        return domain_index;
    }

    public void setDomain_index(int domain_index) {
        this.domain_index = domain_index;
        this.value = domain.get(domain_index);
    }

    public ArrayList<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(ArrayList<Constraint> constraints) {
        this.constraints = constraints;
    }

    public int getDomain_length() {
        return domain_length;
    }

    public void setDomain_length(int domain_length) {
        this.domain_length = domain_length;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getConstraint_number() {
        return constraint_number;
    }

    public void setConstraint_number(int constraint_number) {
        this.constraint_number = constraint_number;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
