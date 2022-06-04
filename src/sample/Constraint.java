package sample;

enum Op {NotEqual, Plus, Minus, Times, Divided}
public class Constraint {
    private Cell [] cells;                                                          // list of cells involved in the constraint
    private Op operator;                                                            // constraint operator from Op enum
    private int result;                                                             // operation result in the constraint
    private int cell_number;                                                        // the number of cells involved in constraint

    public Constraint(Cell [] cells, Op operator, int result, int cell_number) {             // constructor
        this.cells = cells;
        this.operator = operator;
        this.result = result;
        this.cell_number = cell_number;
    }

    public boolean verifyConstraint()
    {
        int r;
        int assigned = cell_number;
        switch (operator)                           // check the constraint operator
        {
            case Plus:
                r = 0;
                for(int i = 0; i < cell_number; i++)
                {
                    int v = cells[i].getValue();
                    if(v == -1)                     // if a cell value is -1, then it's not assigned yet so the constraint can't

                        assigned --;                // decrease the number of signed cells by one
                    r += v;                         // get the sum of constrained cells values
                }
                // return true if result equals the required result while all cells are assigned or less the the required
                // result if not all cells are assigned
                return ((r == this.result) && (assigned == cell_number)) || ((r < this.result) && (assigned < cell_number));

            case Minus:
               // number of cells must be two
                int value_1 = cells[0].getValue();
                int value_2 = cells[1].getValue();
                if (value_1 == -1 || value_2 == -1)
                    return true;                    // return true if any of the two cells is unassigned
                r = (int) Math.max(value_1, value_2)- Math.min(value_1, value_2);       // if both are assigned check max - min
                return r == this.result;
            case Times:
                r = 1;
                for(int i = 0; i < cell_number; i++)
                {
                    int v = cells[i].getValue();
                    if(v == -1)
                        assigned --;                // decrease the number of signed cells by one
                    r *= v;                         // get the multiplication of constrained cells values
                }
                // return true if result equals the required result while all cells are assigned or less or equal the the required
                // result if not all cells are assigned
                return ((r == this.result) && (assigned == cell_number)) || (assigned < cell_number && !(r > this.result));
            case Divided:
                // number of cells must be two
                value_1 = cells[0].getValue();
                value_2 = cells[1].getValue();
                if (value_1 == -1 || value_2 == -1)
                    return true;                    // return true if any of the two cells is unassigned
                r = (int) Math.max(value_1, value_2)/ Math.min(value_1, value_2);
                return Math.abs(r - this.result) < 0.0001;          // handle float approximation
            case NotEqual:      // check that no repeated value occur in the same row or column

                for(int i = 0; i < cell_number -1; i++)
                {
                    int v1 = cells[i].getValue();
                    if(v1 == -1)
                        continue;                   // if a cell isn't assigned, pass it
                    for (int j = i + 1; j < cell_number; j++)
                    {
                        int v2 = cells[j].getValue();
                        if(v2 == -1)
                            continue;
                        if(v1 == v2)
                        {
                            return false;           // if two values are equal, return false
                        }
                    }
                }
                return true;

        }
        return true;
    }

    // Setters and Getters

    public Op getOperator() {
        return operator;
    }

    public void setOperator(Op operator) {
        this.operator = operator;
    }

    public int getResult() {
        return result;
    }

    public Cell[] getCells() {
        return cells;
    }

    public void setCells(Cell[] cells) {
        this.cells = cells;
    }

    public int getCell_number() {
        return cell_number;
    }

    public void setCell_number(int cell_number) {
        this.cell_number = cell_number;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
