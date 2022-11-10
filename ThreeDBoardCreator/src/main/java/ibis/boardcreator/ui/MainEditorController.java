package ibis.boardcreator.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ibis.boardcreator.datamodel.Grid;
import ibis.boardcreator.datamodel.GridIO;
import ibis.boardcreator.datamodel.Tile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainEditorController {

	private final double TILE_SIZE = 50;
	
	private Tile currentTileModified = new Tile(-1, -1, 0);

	@FXML
	private Canvas canvasGrid;

	@FXML
	private Slider elevationSlider;
    @FXML
    private ToggleButton lowerElevationButton;

    @FXML
    private ToggleButton raiseElevationButton;

    @FXML
    private ToggleGroup toolButtonsGroup;

	@FXML
	private Menu aboutScreen;

	@FXML
	private void initialize() {

		drawGrid();
		canvasGrid.setOnMousePressed(evt -> handleCanvasMousePress(evt));
		canvasGrid.setOnMouseDragged(evt -> handleCanvasMouseDrag(evt));
	}

	Alert alert = new Alert(Alert.AlertType.WARNING);

	private void drawGrid() {
		Grid grid = App.getGrid();
		GraphicsContext gc = canvasGrid.getGraphicsContext2D();

		for (int r = 0; r < grid.getNumRows(); r++) {
			for (int c = 0; c < grid.getNumColumns(); c++) {
				gc.setStroke(Color.CYAN);
				double x = c * TILE_SIZE;
				double y = r * TILE_SIZE;
				gc.strokeRect(x, y, TILE_SIZE, TILE_SIZE);
				Tile tile = grid.getTileAt(r, c);

				// tile.setElevation(elevationSlider.getValue());
				double elev = tile.getElevation();
				double grayVal = 1 - elev / 10;
				Color color = new Color(grayVal, grayVal, grayVal, 1);
				gc.setFill(color);
				gc.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);

			}
		}
	}


	private void handleCanvasMousePress(MouseEvent evt) {
		int c = (int) (evt.getX() / TILE_SIZE);
		int r = (int) (evt.getY() / TILE_SIZE);
		Tile clickedTile = App.getGrid().getTileAt(r, c);
		adjustTileHeight(clickedTile);

	}
	
	private void adjustTileHeight(Tile tile) {
		double sliderValue = elevationSlider.getValue();
		double newElevation = -1;
		if (toolButtonsGroup.getSelectedToggle() == raiseElevationButton) {
			newElevation = tile.getElevation() + sliderValue;
		}else if(toolButtonsGroup.getSelectedToggle() == lowerElevationButton) {
			newElevation = tile.getElevation() - sliderValue;
		}
		if (newElevation >= 10) {
			newElevation = 10;
		}else if (newElevation <= 0) {
			newElevation = 0;
		}
		tile.setElevation(newElevation);
		drawGrid();
		currentTileModified = tile;
		
		
	}
	private void handleCanvasMouseDrag(MouseEvent evt) {
		int c = (int) (evt.getX() / TILE_SIZE);
		int r = (int) (evt.getY() / TILE_SIZE);
		Tile dragTile = App.getGrid().getTileAt(r, c);
		if (dragTile != currentTileModified) {
			adjustTileHeight(dragTile);
		}

		}
	

	@FXML
	void openFileAction(ActionEvent event) {
		FileChooser openChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Grids (*.OBJ)", "*.OBJ");
		openChooser.getExtensionFilters().add(extFilter);
		File inputFile = openChooser.showOpenDialog(App.getMainWindow());
		if (inputFile != null) {
			try {
				Grid grid = GridIO.load2dMapFromJSONFile(inputFile);
				App.setGrid(grid);
				drawGrid();
			} catch (FileNotFoundException ex) {
				new Alert(AlertType.ERROR, "The file you tried to open could not be found.").showAndWait();
			} catch (IOException ex) {
				new Alert(AlertType.ERROR,
						"Error opening file.  Did you choose a valid .movieList file (which uses JSON format?)").show();
			}
		}
	}

	@FXML
	void saveFileAsAction(ActionEvent event) {
		FileChooser saveChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Grids (*.OBJ)", "*.OBJ");
		saveChooser.getExtensionFilters().add(extFilter);
		File outputFile = saveChooser.showSaveDialog(App.getMainWindow());
		if (outputFile != null) {
			Grid grid = App.getGrid();
			try {
				GridIO.save2dMapAsJSONFile(grid, outputFile);
			} catch (IOException ex) {
				new Alert(AlertType.ERROR, "An I/O error occurred while trying to save this file.").showAndWait();
			}
		}
	}

	@FXML
	void saveFileAction(ActionEvent event) {

	}

	@FXML
	private void switchToThreeDPreview() throws IOException {
		App.setRoot("Three_D_Preview");

	}

	@FXML
	private void switchToAboutScreen() throws IOException {
		App.setRoot("AboutScreen");

	}

}
