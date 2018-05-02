package de.luh.kriegel.studip.synchronizer.application.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class MainPane extends StackPane {

	private static final Logger log = LogManager.getLogger(MainPane.class);

	public MainPane() {

		buildUI();
	}

	private void buildUI() {
		log.info("Build UI of StudIP Synchronizer");

		VBox verticalWrapper = new VBox();
		verticalWrapper.prefHeightProperty().bind(this.heightProperty());
		verticalWrapper.prefWidthProperty().bind(this.widthProperty());

		HBox titleHBox = new HBox();

		Label title = buildLabel("StudIP Synchronizer");
		title.setFont(Font.font(25));

		titleHBox.maxHeightProperty().bind(title.heightProperty());
		titleHBox.setAlignment(Pos.CENTER);
		titleHBox.getChildren().add(title);

		List<HBox> fieldHBoxes = buildInputFields();

		verticalWrapper.getChildren().add(titleHBox);
		verticalWrapper.getChildren().addAll(fieldHBoxes);

		verticalWrapper.getChildren().forEach(child -> VBox.setVgrow(child, Priority.ALWAYS));

		this.getChildren().addAll(verticalWrapper);

	}

	private List<HBox> buildInputFields() {

		List<HBox> horizontalFieldBoxes = new ArrayList<>();

		HBox urlInputBox = new HBox();

		Label urlInputLabel = buildLabel("URL");
		TextField urlInputTextField = new TextField();

		GridPane urlInputGridPane = new GridPane();

		ColumnConstraints cc = new ColumnConstraints();
		cc.setPercentWidth(50);
		urlInputGridPane.getColumnConstraints().add(cc);

		urlInputGridPane.add(urlInputLabel, 0, 0);
		urlInputGridPane.add(urlInputTextField, 1, 0);

		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				log.info(urlInputGridPane.getWidth());
				log.info(urlInputLabel.getWidth());
				log.info(urlInputTextField.getWidth());
			}
		}.start();

		urlInputBox.getChildren().add(urlInputGridPane);

		horizontalFieldBoxes.add(urlInputBox);
		HBox.setHgrow(urlInputGridPane, Priority.ALWAYS);

		return horizontalFieldBoxes;
	}

	private Label buildLabel(String text) {
		Label label = new Label(text);
		label.setTextAlignment(TextAlignment.RIGHT);
		label.setPadding(new Insets(5, 5, 5, 5));

		return label;
	}

}
