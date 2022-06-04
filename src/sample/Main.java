package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.Arrays;


public class Main extends Application {

    Scene FirstScene;
    Grid g;
    int gridSize = 3; // default value

    Scene SecondScene;
    GridPane grid2;
    GridPane table; // to draw the specific grid
    StackPane [][] canvas;
    String str = "+-*/";
    char[] ops = str.toCharArray();


    Scene ThirdScene;
    GridPane grid3;
    Label titleLabel2;
    GridPane table2; // to draw the specific grid

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        // setting the title of the stage
        primaryStage.setTitle("KENKEN Solver");


//////////////
// FIRST SCENE
//////////////

        //identifying the grid
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);


        // choosing puzzle size
        Label sizeLabel = new Label("Puzzle Size:");
        GridPane.setConstraints(sizeLabel, 0,0 );

        TextField sizeText = new TextField();
        sizeText.setText("3");
        sizeText.setMinWidth(5);
        GridPane.setConstraints(sizeText, 1,0);

        // choosing algorithm from combobox
        Label algorithmLabel = new Label("Chosen Algorithm:");
        GridPane.setConstraints(algorithmLabel, 0, 1);

        ComboBox<String> choice= new ComboBox<>();
        choice.getItems().addAll("Backtracking", "Backtracking with Forward Checking", "Backtracking with Arc Consistency");
        choice.getSelectionModel().selectFirst();
        GridPane.setConstraints(choice, 1,1);

        // adding "generate algorithm button"
        Button GenerateButton = new Button("Generate Puzzle");
        GenerateButton.setOnAction(e->{GenerateButtonClicked(primaryStage, sizeText);});
        GridPane.setConstraints(GenerateButton, 2,2 );

        // adding "exit button"
        Button ExitButton = new Button("Exit");
        ExitButton.setOnAction(e->{Platform.exit();});
        GridPane.setConstraints(GenerateButton, 0,2);
        grid.getChildren().addAll(sizeLabel, sizeText, algorithmLabel, choice);

        //putting constraints on HBox and VBox
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10,10,10,10));
        hBox.setSpacing(210);
        hBox.getChildren().addAll(ExitButton, GenerateButton);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(grid, hBox);

        //showing first scene
        FirstScene = new Scene (vBox);
        primaryStage.setScene(FirstScene);
        primaryStage.show();

//////////////
// SECOND SCENE
//////////////

        //identifying the grid
        grid2 = new GridPane();
        grid2.setPadding(new Insets(10, 10, 10, 10));
        grid2.setVgap(8);
        grid2.setHgap(10);

        // label KENKEN Puzzle
        Label titleLabel = new Label("KENKEN Puzzle");
        GridPane.setConstraints(titleLabel, 1,0);

        // adding the gridpane where we will display the  grid itself in the middle of grid2
        table = new GridPane();
        table.setVgap(0);
        table.setHgap(0);
        table.setAlignment(Pos.CENTER);
        GridPane.setConstraints(table, 1, 1);

        // adding "solve button"
        Button solveButton = new Button("Solve");
        solveButton.setOnAction(e->{solveButtonClicked(primaryStage, choice);});
        GridPane.setConstraints(solveButton, 2, 2);

        grid2.getChildren().addAll(titleLabel, table, solveButton);

        VBox vBox2 = new VBox();
        vBox2.getChildren().addAll(grid2);
        SecondScene = new Scene (vBox2);

//////////////
// Third SCENE
//////////////

        //identifying the grid
        grid3 = new GridPane();
        grid3.setPadding(new Insets(10, 10, 10, 10));
        grid3.setVgap(8);
        grid3.setHgap(10);

        // label KENKEN Puzzle Solution
        titleLabel2 = new Label("KENKEN Puzzle Solution");
        GridPane.setConstraints(titleLabel2, 1,0);

        // adding the gridpane where we will display the  grid itself in the middle of grid3
        table2 = new GridPane();
        table2.setVgap(0);
        table2.setHgap(0);
        table2.setAlignment(Pos.CENTER);
        GridPane.setConstraints(table2, 1, 1);

        // adding "exit button"
        Button ExitButton2 = new Button("Exit");
        ExitButton2.setOnAction(e->{Platform.exit();});
        GridPane.setConstraints(ExitButton2, 0, 2);

        // adding "retry button" goes to firstscene and initializes everything
        Button RetryButton = new Button("Retry");
        RetryButton.setOnAction(e->{
            gridSize = 3;
            sizeText.setText("3");
            choice.getSelectionModel().selectFirst();
            table.getChildren().clear();
            table2.getChildren().clear();
            titleLabel2.setText("KENKEN Puzzle Solution");
            primaryStage.setScene(FirstScene);
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2); // to center the window
            primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);});
        GridPane.setConstraints(RetryButton, 2, 2);

        grid3.getChildren().addAll(titleLabel2, table2, ExitButton2, RetryButton);

        VBox vBox3 = new VBox();
        vBox3.getChildren().addAll(grid3);
        ThirdScene = new Scene (vBox3);

    }


    // function called when generate button clicked to generate the problem and display it with the constraints in secondscene
    public void GenerateButtonClicked(Stage primaryStage, TextField size) {

        // generating problem
        gridSize = Integer.parseInt(size.getText());
        KenKen_Generator k = new KenKen_Generator(Integer.parseInt(size.getText()));

        g = new Grid(gridSize);
        g = k.getG();


        // creating an array of StackPanes to have a pane that has borders after that, label for the constraint and text for the value of the cell
        canvas = new StackPane[g.getGrid_length()][g.getGrid_length()];
        for(int i = 0; i < g.getGrid_cells().length; i++){
            for (int j = 0; j < g.getGrid_cells().length; j++){
                StackPane r = new StackPane();
                r.setStyle("-fx-background-color: white;");
                r.setPrefSize(100,100);

                Pane rec = new Pane();
                rec.setPrefSize(98, 98);
                rec.setStyle("-fx-background-color: white;");

                Label lab = new Label();
                lab.setPadding(new Insets(5));
                StackPane.setAlignment(lab, Pos.TOP_LEFT);

                Text text = new Text();
                StackPane.setAlignment(text, Pos.CENTER);
                r.getChildren().addAll(rec, lab, text);
                canvas[i][j] = r; // adding them to the array to be easily accessible after that for each cell alone
                table.add(r, j, i);
            }
        }

        // drawing constraints
        ArrayList<Constraint> constraints = new ArrayList<Constraint>(g.getConstraints());
        for(int c = 0; c < constraints.size(); c++) // iterate on each constraint
        {
            Constraint constr = constraints.get(c);
            ArrayList<Cell> cells = new ArrayList<>(Arrays.asList(constr.getCells())); // get cells involved in this constraint
            if (constraints.get(c).getOperator() != Op.NotEqual){ // display constraints that are not row-column constraints

                // putting the label of the constraint
                Label l = (Label) canvas[constr.getCells()[0].getRow()][constr.getCells()[0].getColumn()].getChildren().get(1);
                if (constr.getCell_number() == 1){
                    l.setText(constr.getResult() + "");
                }
                else{
                    l.setText(constr.getResult() + " " + String.valueOf(ops[constr.getOperator().ordinal() - 1]));
                }
                l.setFont(Font.font("",FontWeight.BOLD, FontPosture.REGULAR, 15));

                // drawing the borders
                for (int i = 0; i < constr.getCell_number(); i++){
                    Cell cell = constr.getCells()[i];

                    // getting the neighbouring cells
                    Cell Top = (cell.getRow() > 0)? g.getGrid_cells()[cell.getRow() - 1][cell.getColumn()]: null;
                    Cell Bottom = (cell.getRow() < g.getGrid_length() - 1)? g.getGrid_cells()[cell.getRow() + 1][cell.getColumn()]: null;
                    Cell Left = (cell.getColumn() > 0)? g.getGrid_cells()[cell.getRow()][cell.getColumn() - 1]: null;
                    Cell Right = (cell.getColumn() < g.getGrid_length() - 1)? g.getGrid_cells()[cell.getRow()][cell.getColumn() + 1]: null;

                    // checking if these cells are contained in the constraint or not, if yes then thin border, if no then thick border
                    int borTop = (Top == null || !cells.contains(Top))? 3: 1;
                    int borBottom = (Bottom == null || !cells.contains(Bottom))? 3: 1;
                    int borLeft = (Left == null || !cells.contains(Left))? 3: 1;
                    int borRight = (Right == null || !cells.contains(Right))? 3: 1;

                    Pane rect = (Pane) canvas[cell.getRow()][cell.getColumn()].getChildren().get(0);
                    rect.setStyle("-fx-border-color:black; " +
                            "-fx-border-width: " + borTop + " " + borRight +
                            " " + borBottom + " " + borLeft + ";");
                }

            }
        }

        // displaying secondscene
        primaryStage.setScene(SecondScene);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
    }

    public void solveButtonClicked(Stage primaryStage, ComboBox choice){
        //Choosing the algorithm
        boolean result = false;
        if (choice.getValue().toString()=="Backtracking with Forward Checking"){
            result = g.csp_back_tracking(true, false);
        }
        else if(choice.getValue().toString()== "Backtracking with Arc Consistency"){
            result = g.csp_back_tracking(false, true);
        }
        else if(choice.getValue().toString()=="Backtracking"){
            result = g.csp_back_tracking(false, false);
        }
        
        if (result) {
            // display value of cells in the corresponding text of stackpanes
            for(int i = 0; i < g.getGrid_cells().length; i++){
                for (int j = 0; j < g.getGrid_cells().length; j++){
                    Text t = (Text) canvas[i][j].getChildren().get(2);
                    t.setText(Integer.toString(g.getGrid_cells()[i][j].getValue()));
                    t.setFont(Font.font("",FontWeight.SEMI_BOLD, FontPosture.REGULAR, 30));
                }
            }
            table2.getChildren().addAll(table.getChildren());
        } else {
            table2.getChildren().addAll(table.getChildren());
            titleLabel2.setText("No Solution Found!");
        }

        // displaying thirdscene
        primaryStage.setScene(ThirdScene);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
