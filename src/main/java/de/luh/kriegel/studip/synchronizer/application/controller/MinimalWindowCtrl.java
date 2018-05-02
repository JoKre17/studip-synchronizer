package de.luh.kriegel.studip.synchronizer.application.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MinimalWindowCtrl extends BorderPane {

	// Values injected from the FXML
	@FXML
	private BorderPane root, mainWindow, contentArea;

	@FXML
	private Label lblTitle;

	@FXML
	private Button btnMax, btnResize;

	// Reference to the primaryStage
	final private Stage stage;

	// References to min/max width/height and the shadow effect
	final private int MINWIDTH, MINHEIGHT, SHADOWSPACE = 5;

	// Things for the resizing/moving window
	private double actualX, actualY;
	private boolean isMovable = true;

	public MinimalWindowCtrl(Stage stage, int minwidth, int minheight) {
		// First, take the reference to the stage
		this.stage = stage;

		// Taking the references to the window
		MINWIDTH = minwidth;
		MINHEIGHT = minheight;

		// Then load the window, setting the root and controller
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/MinimalWindow.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		// Try to load
		try {
			loader.load();
		} catch (Exception e) {
			e.printStackTrace();
			// TODO Show a message error
		}
	}

	public void setTitle(String s) {
		lblTitle.setText(s);
	}

	public void setContent(Node node) {
		contentArea.setCenter(node);
	}

	//
	// MIMIZIE | MAXIMIZE | CLOSE
	//

	// When pressed, will minimize the window to tray
	@FXML
	private void minimizeApp(MouseEvent e) {
		stage.setIconified(true);
	}

	// When pressed, check if it must maximize or restore the window
	@FXML
	private void maximizeApp(MouseEvent e) {
		if (stage.isMaximized()) {
			setMin();
			isMovable = true;
		}

		else {
			setMax();
			isMovable = false;
		}
	}

	// When pressed, will kill the window
	@FXML
	private void closeApp(MouseEvent e) {
		stage.close();
		System.exit(0);
	}

	//
	// WINDOW MOVING
	//

	// When i must update the XY of the click
	@FXML
	private void updateXY(MouseEvent e) {
		actualX = e.getScreenX() - stage.getX();
		actualY = e.getScreenY() - stage.getY();
	}

	// When pressing and dragging the mouse it will move the window
	@FXML
	private void windowDragging(MouseEvent e) {
		if (isMovable) {
			stage.setX(e.getScreenX() - actualX);
			stage.setY(e.getScreenY() - actualY);
		}

		else {
			setMin();
			stage.setX(e.getScreenX());
			stage.setY(e.getScreenY());
		}
	}

	// Update the status of the window from not movable to movable, after
	// "normalize" effect
	// from the dragging it when it's maximized
	@FXML
	private void updateStatus(MouseEvent e) {
		if (stage.isMaximized() == false) {
			isMovable = true;
		}
	}

	//
	// WINDOW RESIZING
	//

	/*
	 * onMouseEntered="#setMouseCursor" onMouseExited="#resetMouseCursor"
	 * onMouseDragged="#resizeWindow"
	 */

	@FXML
	private void setMouseCursor(MouseEvent e) {
		mainWindow.setCursor(Cursor.CROSSHAIR);
	}

	@FXML
	private void resetMouseCursor(MouseEvent e) {
		mainWindow.setCursor(Cursor.DEFAULT);
	}

	@FXML
	private void resizeWindow(MouseEvent e) {
		actualX = e.getScreenX() - stage.getX() + 13;
		actualY = e.getScreenY() - stage.getY() + 10;

		if (actualX % 5 == 0 || actualY % 5 == 0) {
			if (actualX > MINWIDTH) {
				stage.setWidth(actualX);
			} else {
				stage.setWidth(MINWIDTH);
			}

			if (actualY > MINHEIGHT) {
				stage.setHeight(actualY);
			} else {
				stage.setHeight(MINHEIGHT);
			}
		}
	}

	//
	// Internal methods
	//

	// Will set the window to MAXIMIZE size
	private void setMax() {
		stage.setMaximized(true);
		btnResize.setVisible(false);
		btnMax.setStyle("-fx-background-image: url('/images/window/dSquare.png');");
		mainWindow.setPadding(new Insets(0, 0, 0, 0));
	}

	// Will set the window to NORMAL size
	private void setMin() {
		stage.setMaximized(false);
		btnResize.setVisible(true);
		btnMax.setStyle("-fx-background-image: url('/images/window/square.png');");
		mainWindow.setPadding(new Insets(SHADOWSPACE, SHADOWSPACE, SHADOWSPACE, SHADOWSPACE));

	}
}
