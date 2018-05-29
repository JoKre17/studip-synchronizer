package de.luh.kriegel.studip.synchronizer.application.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXToggleButton;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;

public class SettingsController implements Initializable{

	private final static Logger log = LogManager.getLogger(SettingsController.class);
	
	@FXML
	private TabPane tabPane;

	// UI Elements //

	// Download
	@FXML
	private JFXToggleButton downloadEnabledToggleButton;

	@FXML
	private JFXRadioButton oncePerStartRadioButton;

	@FXML
	private JFXRadioButton fiveMinRadioButton;

	@FXML
	private JFXRadioButton fifteenMinRadioButton;

	@FXML
	private JFXRadioButton thirtyMinRadioButton;

	// Notification
	
	@FXML
	private JFXToggleButton notificationEnabledToggleButton;
	
	private List<JFXRadioButton> synchronizeIntervalRadioButtons;

	private final IntegerProperty synchronizeIntervalProperty = new SimpleIntegerProperty();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Download
		
		// Set UserData for Radio Buttons
		oncePerStartRadioButton.setUserData(new Integer(0));
		fiveMinRadioButton.setUserData(new Integer(5));
		fifteenMinRadioButton.setUserData(new Integer(15));
		thirtyMinRadioButton.setUserData(new Integer(30));
		
		synchronizeIntervalRadioButtons = Arrays.asList(oncePerStartRadioButton, fiveMinRadioButton, fifteenMinRadioButton, thirtyMinRadioButton);
		
		// Set Listener for Radio Buttons
		for(JFXRadioButton radioButton : synchronizeIntervalRadioButtons) {
			radioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if(newValue != null && newValue.booleanValue()) {
						Object userData = radioButton.getUserData();
						if(userData instanceof Integer) {
							synchronizeIntervalProperty.set((Integer) userData);
						}
					}
				}
			});
		}
		
		synchronizeIntervalProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				log.info(newValue);
			}
		});
	}
	
	public int getSynchronizeInterval() {
		return synchronizeIntervalProperty.getValue().intValue();
	}
	
	public IntegerProperty getSynchronizeIntervalProperty() {
		return synchronizeIntervalProperty;
	}

}
