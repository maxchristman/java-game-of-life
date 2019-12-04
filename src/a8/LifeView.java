package a8;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class LifeView extends JPanel implements ActionListener, SpotListener, ChangeListener, Runnable{

	private JSpotBoard board;					// A JSpotBoard storing all of the cells
	private JLabel sizeLabel;					// A JLabel with the current size
	private List<LifeViewListener> listeners;	// The list of listeners of the view, which is just the controller
	private JTextField lowBirthTextField;
	private JTextField highBirthTextField;
	private JTextField lowSurviveTextField;
	private JTextField highSurviveTextField;

	private boolean running = false;
	private int sleepInterval = 10;

	LifeView() {
		setLayout(new BorderLayout());
		// Create spot board and associated user interface elements
		board = new JSpotBoard(10, 10);
		JLabel message = new JLabel("Welcome to Conway's Game of Life.");

		add(board, BorderLayout.CENTER);

		// Creates the bottom panel, with size slider
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());

		JPanel sizePanel = new JPanel();
		sizePanel.setLayout(new BorderLayout());

		sizeLabel = new JLabel("10");
		JLabel sliderNameLabel = new JLabel("Board Size: ");
		JSlider sizeSlider = new JSlider(10, 500, 10);
		sizeSlider.addChangeListener(this);
		sizePanel.add(sizeLabel, BorderLayout.EAST);
		sizePanel.add(sizeSlider, BorderLayout.CENTER);
		sizePanel.add(sliderNameLabel, BorderLayout.WEST);
		bottomPanel.add(sizePanel, BorderLayout.WEST);
		bottomPanel.add(message, BorderLayout.EAST);

		add(bottomPanel, BorderLayout.SOUTH);

		// Creates the side panel, with random/torus controls, custom probabilities, the advance button and the start/stop button
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new BorderLayout());

		JPanel booleanPanel = new JPanel();
		booleanPanel.setLayout(new BorderLayout());

		JButton randomButton = new JButton("Random");
		JButton torusButton = new JButton("Toggle Torus Mode");
		booleanPanel.add(randomButton, BorderLayout.NORTH);
		booleanPanel.add(torusButton, BorderLayout.SOUTH);
		sidePanel.add(booleanPanel, BorderLayout.NORTH);

		JPanel thresholdPanel = new JPanel();
		thresholdPanel.setLayout(new GridLayout(4, 0));

		JPanel lowBirthPanel = new JPanel();
		lowBirthPanel.setLayout(new GridLayout(1, 2));
		JLabel lowBirthLabel = new JLabel("Low birth threshold");
		lowBirthTextField = new JTextField("3");
		lowBirthTextField.addActionListener(this);
		lowBirthTextField.setActionCommand("Set Thresholds");
		lowBirthPanel.add(lowBirthLabel);
		lowBirthPanel.add(lowBirthTextField);

		JPanel highBirthPanel = new JPanel();
		highBirthPanel.setLayout(new GridLayout(1, 2));
		JLabel highBirthLabel = new JLabel("High birth threshold");
		highBirthTextField = new JTextField("3");
		highBirthTextField.addActionListener(this);
		highBirthTextField.setActionCommand("Set Thresholds");
		highBirthPanel.add(highBirthLabel);
		highBirthPanel.add(highBirthTextField);

		JPanel lowSurvivePanel = new JPanel();
		lowSurvivePanel.setLayout(new GridLayout(1, 2));
		JLabel lowSurviveLabel = new JLabel("Low survive threshold");
		lowSurviveTextField = new JTextField("2");
		lowSurviveTextField.addActionListener(this);
		lowSurviveTextField.setActionCommand("Set Thresholds");
		lowSurvivePanel.add(lowSurviveLabel);
		lowSurvivePanel.add(lowSurviveTextField);

		JPanel highSurvivePanel = new JPanel();
		highSurvivePanel.setLayout(new GridLayout(1, 2));
		JLabel highSurviveLabel = new JLabel("High survive threshold");
		highSurviveTextField = new JTextField("3");
		highSurviveTextField.addActionListener(this);
		highSurviveTextField.setActionCommand("Set Thresholds");
		highSurvivePanel.add(highSurviveLabel);
		highSurvivePanel.add(highSurviveTextField);

		thresholdPanel.add(lowBirthPanel);
		thresholdPanel.add(highBirthPanel);
		thresholdPanel.add(lowSurvivePanel);
		thresholdPanel.add(highSurvivePanel);
		sidePanel.add(thresholdPanel, BorderLayout.CENTER);

		JPanel timePanel = new JPanel();
		timePanel.setLayout(new GridLayout(4, 1));

		JButton advanceButton = new JButton("Advance");
		JButton toggleTimeButton = new JButton("Start / Stop");
		JButton restartButton = new JButton("Restart");
		JPanel delayPanel = new JPanel();
		delayPanel.setLayout(new GridLayout(2, 0));
		JLabel delayLabel = new JLabel("Delay");
		JSlider delaySlider = new JSlider(10, 1000, 10);
		delayPanel.add(delayLabel);
		delayPanel.add(delaySlider);
		delaySlider.addChangeListener(this);

		timePanel.add(advanceButton);
		timePanel.add(toggleTimeButton);
		timePanel.add(restartButton, BorderLayout.EAST);
		timePanel.add(delayPanel);

		sidePanel.add(timePanel, BorderLayout.SOUTH);

		add(sidePanel, BorderLayout.EAST);

		board.addSpotListener(this);

		randomButton.addActionListener(this);
		torusButton.addActionListener(this);
		advanceButton.addActionListener(this);
		toggleTimeButton.addActionListener(this);
		restartButton.addActionListener(this);

		for (Spot spot : board) {

			spot.setBackground(Color.GRAY);
		}

		listeners = new ArrayList<>();
	}

	boolean getRunning() {

		return running;
	}

	void setRunning(boolean b) {

		running = b;
	}

	void setBoard(boolean[][] boolBoard) {

		// If setBoard was called after the board size was changed
		boolean mismatch = !(boolBoard.length == board.getSpotHeight());

		// Makes a new one of new size if boolBoard and board don't match
		if (mismatch) {

			// Removes the old SpotBoard
			this.remove(board);
			board = new JSpotBoard(boolBoard.length, boolBoard.length);
		}

		// Changes background colors accordingly
		for (Spot spot : board) {

			if (boolBoard[spot.getSpotX()][spot.getSpotY()]) {

				spot.setBackground(Color.YELLOW);
			} else {

				spot.setBackground(Color.GRAY);
			}
		}

		if (mismatch) {

			add(board, BorderLayout.CENTER);
			board.addSpotListener(this);
		}
	}

	void setSize(int newSize) {

		sizeLabel.setText("" + newSize);
	}

	@Override
	public void spotClicked(Spot spot) {

		int[] coords = new int[] {spot.getSpotX(), spot.getSpotY()};
		fireEvent(new SpotClickedEvent(coords));
	}

	@Override
	public void spotEntered(Spot spot) {

		spot.highlightSpot();
	}

	@Override
	public void spotExited(Spot spot) {

		spot.unhighlightSpot();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {

			case "Restart":
				fireEvent(new RestartEvent());
				break;
			case "Advance":
				updateThresholds();
				fireEvent(new AdvanceEvent());
				break;
			case "Start / Stop":
				if (!running) {

					running = true;
					(new Thread(this)).start();

				} else {

					running = false;
				}
				break;
			case "Random":
				fireEvent(new RandomEvent());
				break;
			case "Toggle Torus Mode":
				fireEvent(new TorusEvent());
				break;
		}
	}

	void setDelay(int newDelay) {

		sleepInterval = newDelay;
	}

	// Runs when start is clicked and it wasn't already running
	public void run() {

		while (running) {

			// Checks for changed threshold values and advanced every time step
			updateThresholds();
			fireEvent(new AdvanceEvent());

			try {
				Thread.sleep(sleepInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	void addLifeViewListener(LifeViewListener l) {

		listeners.add(l);
	}

	private void fireEvent(LifeViewEvent e) {

		for (LifeViewListener l : listeners) {

			l.handleLifeViewEvent(e);
		}
	}

	// Helper method to update the thresholds
	private void updateThresholds() {

		boolean invalidInput = false;

		String[] stringThresholds = new String[]{
				lowBirthTextField.getText(),
				highBirthTextField.getText(),
				lowSurviveTextField.getText(),
				highSurviveTextField.getText()};

		// Prevents thresholds from updating when the box is empty or is not a number
		for (String s : stringThresholds) {

			try {
				Integer.parseInt(s);
			} catch (NumberFormatException e) {
				invalidInput = true;
			}
		}

		if (!invalidInput) {

			int[] thresholds = new int[]{Integer.parseInt(lowBirthTextField.getText()),
					Integer.parseInt(highBirthTextField.getText()),
					Integer.parseInt(lowSurviveTextField.getText()),
					Integer.parseInt(highSurviveTextField.getText())};

			fireEvent(new ThresholdSetEvent(thresholds));
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {

		JSlider slider = (JSlider) e.getSource();

		// Determines which slider was changed based on its maximum value
		int max = slider.getMaximum();
		// If you have stopped dragging a slider, fire a new SliderChangedEvent
		if (!slider.getValueIsAdjusting() && max == 500) {
			fireEvent(new SliderChangedEvent(slider.getValue(), true));
		} else if (!slider.getValueIsAdjusting() && max == 1000) {
			fireEvent(new SliderChangedEvent(slider.getValue(), false));
		}
	}
}
