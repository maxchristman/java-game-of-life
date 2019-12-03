package a8;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class LifeView extends JPanel implements ActionListener, SpotListener, ChangeListener {

	private JSpotBoard board;					// A JSpotBoard storing all of the cells
	private JLabel sizeLabel;					// A JLabel with the current size
	private List<LifeViewListener> listeners;	// The list of listeners of the view, which is just the controller

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
		JSlider sizeSlider = new JSlider(10, 500, 10);
		sizeSlider.addChangeListener(this);
		sizePanel.add(sizeLabel, BorderLayout.EAST);
		sizePanel.add(sizeSlider, BorderLayout.WEST);
		bottomPanel.add(sizePanel, BorderLayout.WEST);
		bottomPanel.add(message, BorderLayout.EAST);

		add(bottomPanel, BorderLayout.SOUTH);

		// Creates the side panel, with random/torus controls, custom probabilities, the advance button and the start/stop button
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new BorderLayout());

		JPanel booleanPanel = new JPanel();
		booleanPanel.setLayout(new BorderLayout());

		JButton randomButton = new JButton("Random");
		JButton torusButton = new JButton("Torus Mode");
		booleanPanel.add(randomButton, BorderLayout.NORTH);
		booleanPanel.add(torusButton, BorderLayout.SOUTH);
		sidePanel.add(booleanPanel, BorderLayout.NORTH);

		JPanel thresholdPanel = new JPanel();
		thresholdPanel.setLayout(new GridLayout(4, 0));

		JPanel lowBirthPanel = new JPanel();
		lowBirthPanel.setLayout(new GridLayout(1, 2));
		JLabel lowBirthLabel = new JLabel("Low birth threshold");
		JTextField lowBirthTextField = new JTextField();
		lowBirthPanel.add(lowBirthLabel);
		lowBirthPanel.add(lowBirthTextField);

		JPanel highBirthPanel = new JPanel();
		highBirthPanel.setLayout(new GridLayout(1, 2));
		JLabel highBirthLabel = new JLabel("High birth threshold");
		JTextField highBirthTextField = new JTextField();
		highBirthPanel.add(highBirthLabel);
		highBirthPanel.add(highBirthTextField);

		JPanel lowSurvivePanel = new JPanel();
		lowSurvivePanel.setLayout(new GridLayout(1, 2));
		JLabel lowSurviveLabel = new JLabel("Low survive threshold");
		JTextField lowSurviveTextField = new JTextField();
		lowSurvivePanel.add(lowSurviveLabel);
		lowSurvivePanel.add(lowSurviveTextField);

		JPanel highSurvivePanel = new JPanel();
		highSurvivePanel.setLayout(new GridLayout(1, 2));
		JLabel highSurviveLabel = new JLabel("High survive threshold");
		JTextField highSurviveTextField = new JTextField();
		highSurvivePanel.add(highSurviveLabel);
		highSurvivePanel.add(highSurviveTextField);

		thresholdPanel.add(lowBirthPanel);
		thresholdPanel.add(highBirthPanel);
		thresholdPanel.add(lowSurvivePanel);
		thresholdPanel.add(highSurvivePanel);
		sidePanel.add(thresholdPanel, BorderLayout.CENTER);

		JPanel timePanel = new JPanel();
		timePanel.setLayout(new GridLayout(3, 1));

		JButton advanceButton = new JButton("Advance");
		JButton toggleTimeButton = new JButton("Start / Stop");
		JButton restartButton = new JButton("Restart");

		timePanel.add(advanceButton);
		timePanel.add(toggleTimeButton);
		timePanel.add(restartButton, BorderLayout.EAST);

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

	// WARNING: THIS METHOD IS CURSED
	void setBoard(boolean[][] boolBoard) {

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
				fireEvent(new AdvanceEvent());
				break;
			case "Start / Stop":
				fireEvent(new TimeToggleEvent());
				break;
			case "Random":
				fireEvent(new RandomEvent());
				break;
			case "Torus Mode":
				fireEvent(new TorusEvent());
				break;
		}
	}

	public void addLifeViewListener(LifeViewListener l) {

		listeners.add(l);
	}

	public void removeLifeViewListener(LifeViewListener l) {

		listeners.remove(l);
	}

	public void fireEvent(LifeViewEvent e) {

		for (LifeViewListener l : listeners) {

			l.handleLifeViewEvent(e);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {

		JSlider slider = (JSlider) e.getSource();
		// If you have stopped dragging a slider, fire a new SliderChangedEvent
		if (!slider.getValueIsAdjusting()) {
			fireEvent(new SliderChangedEvent(slider.getValue()));
		}
	}
}
