package stringflow.flowtimer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import stringflow.flowtimer.audio.BeepSound;

import java.util.Arrays;
import java.util.List;

public class SettingsWindow {
	
	public static SettingsWindow instance = null;
	
	public ToggleGroup audioEngineGroup;
	
	public InputField startInputField1;
	public InputField startInputField2;
	public InputField resetInputField1;
	public InputField resetInputField2;
	public InputField upInputField1;
	public InputField upInputField2;
	public InputField downInputField1;
	public InputField downInputField2;
	
	public InputField focuedField;
	
	@FXML
	public TextField startInputField1Internal;
	public TextField startInputField2Internal;
	public TextField resetInputField1Internal;
	public TextField resetInputField2Internal;
	public TextField upInputField1Internal;
	public TextField upInputField2Internal;
	public TextField downInputField1Internal;
	public TextField downInputField2Internal;
	public CheckBox globalStartReset;
	public CheckBox globalUpDown;
	public RadioButton javaxAudioEngine;
	public RadioButton tinySoundAudioEngine;
	public ChoiceBox<String> audioFile;
	public Slider volumeSlider;
	public ChoiceBox<String> choiceBox;
	
	private List<InputField> inputFields;
	
	public SettingsWindow() {
		instance = this;
	}
	
	public void initialize() {
		choiceBox.getItems().add("Audio");
		choiceBox.getItems().add("Visual");
		choiceBox.getItems().add("Audio + Visual");
		choiceBox.valueProperty().setValue(choiceBox.getItems().get(0));
		choiceBox.valueProperty().addListener(new VisualChangeListener());
		audioEngineGroup = new ToggleGroup();
		javaxAudioEngine.setToggleGroup(audioEngineGroup);
		tinySoundAudioEngine.setToggleGroup(audioEngineGroup);
		inputFields = Arrays.asList(startInputField1 = new InputField(startInputField1Internal), startInputField2 = new InputField(startInputField2Internal), resetInputField1 = new InputField(resetInputField1Internal), resetInputField2 = new InputField(resetInputField2Internal), upInputField1 = new InputField(upInputField1Internal), upInputField2 = new InputField(upInputField2Internal), downInputField1 = new InputField(downInputField1Internal), downInputField2 = new InputField(downInputField2Internal));
		for(BeepSound beepSound : BeepSound.loadedBeepSounds) {
			audioFile.getItems().add(beepSound.getName());
		}
		for(InputField inputField : inputFields) {
			inputField.getParentField().addEventFilter(KeyEvent.KEY_PRESSED, e -> inputField.getParentField().setText(e.getCode().getName()));
			inputField.getParentField().addEventFilter(KeyEvent.KEY_TYPED, e -> e.consume());
			inputField.getParentField().focusedProperty().addListener((arg0, arg1, newValue) -> focuedField = newValue ? inputField : null);
		}
		volumeSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(oldValue && !newValue) {
					FlowTimer.audioEngine.setVolume((float)volumeSlider.getValue() / 100.0f);
					FlowTimer.currentBeep.play();
				}
			}
		});
	}
	
	public void setUpListeners() {
		audioFile.valueProperty().addListener(new AudioFileListener());
		javaxAudioEngine.selectedProperty().addListener(new AudioEngineListener());
		tinySoundAudioEngine.selectedProperty().addListener(new AudioEngineListener());
	}
	
	@FXML
	public void onStartClearPressed() {
		if(startInputField2.isBound()) {
			startInputField2.clear();
		} else {
			startInputField1.clear();
		}
	}
	
	@FXML
	public void onResetClearPressed() {
		if(resetInputField2.isBound()) {
			resetInputField2.clear();
		} else {
			resetInputField1.clear();
		}
	}
	
	@FXML
	public void onUpClearPressed() {
		if(upInputField2.isBound()) {
			upInputField2.clear();
		} else {
			upInputField1.clear();
		}
	}
	
	@FXML
	public void onDownClearPressed() {
		if(downInputField2.isBound()) {
			downInputField2.clear();
		} else {
			downInputField1.clear();
		}
	}
	
	private class AudioEngineListener implements ChangeListener<Boolean> {
		
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			if(javaxAudioEngine.isSelected() || tinySoundAudioEngine.isSelected()) {
				if(newValue) {
					AlertBox.showAlert(Alert.AlertType.INFORMATION, "FlowTimer", "Please restart FlowTimer for this change to take effect.");
				}
			}
		}
	}
	
	private class AudioFileListener implements ChangeListener<String> {
		
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			BeepSound sound = BeepSound.fromString(newValue);
			FlowTimer.currentBeep = sound;
			FlowTimer.currentBeep.play();
		}
	}
	
	private class VisualChangeListener implements ChangeListener<String> {
		
		
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			FlowTimer.audioCue = newValue.toLowerCase().contains("audio");
			FlowTimer.visualCue = newValue.toLowerCase().contains("visual");
		}
	}
}
