package de.luh.kriegel.studip.synchronizer.application.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SimpleWindowController {

	private final static Logger log = LogManager.getLogger(SimpleWindowController.class);

	@FXML
	private StackPane root;

	@FXML
	private BorderPane windowPane;

	@FXML
	private StackPane contentPane;

	@FXML
	private ImageView iconWindow;

	@FXML
	private Label labelWindow;

	@FXML
	private Button buttonMax, buttonResize;

	private Stage stage;

	// Things for the resizing/moving window
	private double actualX, actualY;
	private boolean isMovable = true;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

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

	/*
	 * onMouseEntered="#setMouseCursor" onMouseExited="#resetMouseCursor"
	 * onMouseDragged="#resizeWindow"
	 */

	@FXML
	private void setMouseCursor(MouseEvent e) {
		windowPane.setCursor(Cursor.CROSSHAIR);
	}

	@FXML
	private void resetMouseCursor(MouseEvent e) {
		windowPane.setCursor(Cursor.DEFAULT);
	}

	@FXML
	private void resizeWindow(MouseEvent e) {
		actualX = e.getScreenX() - stage.getX();
		actualY = e.getScreenY() - stage.getY();

		if (actualX > stage.getMinWidth()) {
			stage.setWidth(actualX);
		} else {
			stage.setWidth(stage.getMinWidth());
		}

		if (actualY > stage.getMinHeight()) {
			stage.setHeight(actualY);
		} else {
			stage.setHeight(stage.getMinHeight());
		}
	}

	// Will set the window to MAXIMIZE size
	private void setMax() {
		stage.setMaximized(true);
		buttonResize.setVisible(false);
		// buttonMax.setStyle("-fx-background-image:
		// url('/images/window/dSquare.png');");
		// windowPane.setPadding(new Insets(0, 0, 0, 0));
	}

	// Will set the window to NORMAL size
	private void setMin() {
		stage.setMaximized(false);
		buttonResize.setVisible(true);
		// buttonMax.setStyle("-fx-background-image:
		// url('/images/window/square.png');");
		// windowPane.setPadding(new Insets(SHADOWSPACE, SHADOWSPACE, SHADOWSPACE,
		// SHADOWSPACE));

	}

	public void setContent(Node node) {
		contentPane.getChildren().clear();
		contentPane.getChildren().add(node);
	}

}
