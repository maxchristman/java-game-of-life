package a8;

import java.util.ArrayList;
import java.util.List;

class LifeModel {

	private int lowBirthThreshold;				// The minimum number of alive neighbors for a cell to be born
	private int highBirthThreshold;				// The maximum number of alive neighbors for a cell to be born
	private int lowSurvivalThreshold;			// The minimum number of alive neighbors for a cell to survive
	private int highSurvivalThreshold;			// The maximum number of alive neighbors for a cell to survive

	private boolean torusMode;					// When true, the sides of the board are stitched together

	private boolean[][] board;					// Encapsulates alive spots in a boolean double array
	private int boardSize;						// The current size of the board
	private final int[][] directionVectors = 	// Each direction one can travel from a spot
			{{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};

	private List<LifeObserver> observers;		// Stores the controller, which is updated about changes to the model

	LifeModel() {
		// Sets initial values for thresholds and boolean options
		lowBirthThreshold = 3;
		highBirthThreshold = 3;
		lowSurvivalThreshold = 2;
		highSurvivalThreshold = 3;

		torusMode = false;

		boardSize = 10;
		observers = new ArrayList<>();

		resetBoard();
	}

	// Toggles the spot at the given coordinates
	void toggleSpot(int[] coords) {
		board[coords[0]][coords[1]] = !board[coords[0]][coords[1]];
		notifyObservers();
	}

	// Changes the size of the model board
	void setBoardSize(int newSize) {

		boardSize = newSize;
		resetBoard();
	}

	// Update the board state based on the given death and survival rules and if torus mode is activated or not
	void advance() {

		boolean[][] newBoard = new boolean[boardSize][boardSize];

		for (int i = 0; i < boardSize; i++) {

			System.arraycopy(board[i], 0, newBoard[i], 0, boardSize);
		}

		for (int i = 0; i < boardSize; i++) {

			for (int j = 0; j < boardSize; j++) {

				boolean alive = board[i][j];
				int liveNeighbors = 0;

				if (!torusMode) {

					// For the step in every direction from a current spot
					for (int[] directionVector : directionVectors) {

						// If the spot in that direction is not in bounds, continue to the next spot
						if (i + directionVector[0] >= boardSize
								|| j + directionVector[1] >= boardSize
								|| i + directionVector[0] < 0
								|| j + directionVector[1] < 0) {

							continue;
						}

						// If the spot in that direction is alive, add one to liveNeighbors
						if (board[i + directionVector[0]][j + directionVector[1]]) {

							liveNeighbors++;
						}
					}
				} else {
					// For each possible direction from a particular spot
					for (int[] directionVector : directionVectors) {

						int[] oneStepCoords = new int[]{i + directionVector[0], j + directionVector[1]};

						// If the original spot is on the right edge
						if (oneStepCoords[0] >= boardSize) {
							oneStepCoords[0] = 0;

							// If the original spot is on the left edge
						} if (oneStepCoords[0] < 0) {
							oneStepCoords[0] = boardSize - 1;

							// If the original spot is on the bottom edge
						} if (oneStepCoords[1] >= boardSize) {
							oneStepCoords[1] = 0;

							// If the original spot is on the top edge
						} if (oneStepCoords[1] < 0) {
							oneStepCoords[1] = boardSize - 1;
						}

						if (board[oneStepCoords[0]][oneStepCoords[1]]) {

							liveNeighbors++;
						}
					}
				}

				if (alive) {

					// If there is overpopulation or underpopulation, make the alive cell dead on the new board
					if (liveNeighbors < lowSurvivalThreshold || liveNeighbors > highSurvivalThreshold) {

						newBoard[i][j] = false;
					}
				} else {
					// If within the thresholds, make the dead cell alive on the new board
					if (liveNeighbors >= lowBirthThreshold && liveNeighbors <= highBirthThreshold) {

						newBoard[i][j] = true;
					}
				}
			}
		}

		board = newBoard;
		notifyObservers();
	}

	// Makes a new board of size boardSize and makes every cell dead
	void resetBoard() {

		board = new boolean[boardSize][boardSize];

		for (int i = 0; i < boardSize; i++) {

			for (int j = 0; j < boardSize; j++) {

				board[i][j] = false;
			}
		}
		notifyObservers();
	}

	// Randomly fills cells as alive or dead
	void randomizeBoard() {

		for (int i = 0; i < boardSize; i++) {

			for (int j = 0; j < boardSize; j++) {

				board[i][j] = (Math.random() >= 0.5);
			}
		}
		notifyObservers();
	}

	// Sets the threshold values; run upon advancing the game
	void setThresholds(int[] newThresholds) {

		lowBirthThreshold = newThresholds[0];
		highBirthThreshold = newThresholds[1];
		lowSurvivalThreshold = newThresholds[2];
		highSurvivalThreshold = newThresholds[3];
	}

	void toggleTorusMode() {

		torusMode = !torusMode;
		notifyObservers();
	}

	void addObserver(LifeObserver o) {

		observers.add(o);
	}

	private void notifyObservers() {

		for (LifeObserver o : observers) {

			o.update(board);
		}
	}
}
