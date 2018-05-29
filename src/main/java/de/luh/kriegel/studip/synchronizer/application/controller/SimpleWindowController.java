package de.luh.kriegel.studip.synchronizer.application.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class SimpleWindowController implements Initializable {

	private final static Logger log = LogManager.getLogger(SimpleWindowController.class);

	@FXML
	private AnchorPane root;

	@FXML
	private HBox titleBar;

	@FXML
	private HBox footerBar;

	@FXML
	private BorderPane windowPane;

	@FXML
	private StackPane contentPane;

	@FXML
	private ImageView windowIconView;

	@FXML
	private Label windowTitleLabel;

	@FXML
	private Button minButton, maxbutton, closeButton, resizeButton;

	@FXML
	private HBox statusBar;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private Label statusLabel;

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
		resizeButton.setVisible(false);

		if (windowPane.getStyleClass().contains("roundBorderWithShadow")) {
			windowPane.getStyleClass().remove("roundBorderWithShadow");
			titleBar.getStyleClass().remove("topBordered");
			footerBar.getStyleClass().remove("bottomBordered");

			AnchorPane.setTopAnchor(windowPane, 0.0);
			AnchorPane.setRightAnchor(windowPane, 0.0);
			AnchorPane.setBottomAnchor(windowPane, 0.0);
			AnchorPane.setLeftAnchor(windowPane, 0.0);
			log.info(windowPane.getStyle());
		}

	}

	// Will set the window to NORMAL size
	private void setMin() {
		stage.setMaximized(false);
		resizeButton.setVisible(true);

		if (!windowPane.getStyleClass().contains("roundBorderWithShadow")) {
			windowPane.getStyleClass().add("roundBorderWithShadow");
			titleBar.getStyleClass().add("topBordered");
			footerBar.getStyleClass().add("bottomBordered");

			AnchorPane.setTopAnchor(windowPane, 5.0);
			AnchorPane.setRightAnchor(windowPane, 5.0);
			AnchorPane.setBottomAnchor(windowPane, 5.0);
			AnchorPane.setLeftAnchor(windowPane, 5.0);
			log.info(windowPane.getStyle());
		}

	}

	public void setContent(Region region) {

		Platform.runLater(() -> {

			double requestedMinWidth = region.minWidth(0);
			double requestedMinHeight = region.minHeight(0);

			double allowedMinWidth = stage.getMinWidth();
			double allowedMinHeight = stage.getMinHeight() - (titleBar.getHeight() + footerBar.getHeight());

			if (requestedMinWidth > allowedMinWidth) {
				stage.setMinWidth(stage.getMinWidth() + requestedMinWidth - allowedMinWidth);
			}

			if (requestedMinHeight > allowedMinHeight) {
				stage.setMinHeight(stage.getMinHeight() + requestedMinHeight - allowedMinHeight);
			}
			contentPane.setMinWidth(region.minWidth(0));
			contentPane.setMinHeight(region.minHeight(0));

			contentPane.getChildren().clear();
			contentPane.getChildren().add(region);

			region.prefWidthProperty().bind(contentPane.widthProperty());
			region.prefHeightProperty().bind(contentPane.heightProperty());

		});

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Platform.runLater(() -> {

			// For Mouse Clipping at round borders
			Rectangle globalClipRect = (Rectangle) root.getClip();

			if (globalClipRect == null) {
				globalClipRect = new Rectangle();
				root.setClip(globalClipRect);
			}

			globalClipRect.widthProperty().bind(stage.widthProperty());
			globalClipRect.heightProperty().bind(stage.heightProperty());
			globalClipRect.setArcHeight(10.0);
			globalClipRect.setArcWidth(10.0);

			// Window Title set by stage
			windowTitleLabel.textProperty().bindBidirectional(stage.titleProperty());

			progressBar.setVisible(false);
			statusLabel.setVisible(false);
		});
	}

	public void setStatus(String status) {
		if (status == null || status.isEmpty()) {
			return;
		}

		Platform.runLater(() -> {
			progressBar.setVisible(false);
			if (statusBar.getChildren().contains(progressBar)) {
				statusBar.getChildren().remove(progressBar);
			}

			statusLabel.setVisible(true);
			statusLabel.setText(status);
		});
	}

	public void setStatus(String status, double progress) {
		if (status == null || status.isEmpty()) {
			return;
		}

		Platform.runLater(() -> {
			progressBar.setVisible(true);
			progressBar.setProgress(progress);
			if (!statusBar.getChildren().contains(progressBar)) {
				statusBar.getChildren().add(0, progressBar);
			}

			statusLabel.setVisible(true);
			statusLabel.setText(status);
		});
	}

	public void clearStatus() {
		Platform.runLater(() -> {
			progressBar.setVisible(false);
			statusLabel.setVisible(false);

			progressBar.setProgress(0.0);
			statusLabel.setText("");
		});
	}

}
