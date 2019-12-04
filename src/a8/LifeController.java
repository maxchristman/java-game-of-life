package a8;

public class LifeController implements LifeObserver, LifeViewListener {

	private LifeModel model;
	private LifeView view;

	LifeController(LifeModel model, LifeView view) {

		this.model = model;
		this.view = view;

		view.addLifeViewListener(this);
		model.addObserver(this);
	}

	public void update(boolean[][] board) {

		view.setSize(board.length);
		view.setBoard(board);
	}

	@Override
	public void handleLifeViewEvent(LifeViewEvent e) {

		if (e.isRandomEvent()) {
			model.randomizeBoard();

		} else if (e.isTorusEvent()) {
			model.toggleTorusMode();

		} else if (e.isThresholdSetEvent()) {
			ThresholdSetEvent te = (ThresholdSetEvent) e;
			model.setThresholds(te.getThresholds());

		} else if (e.isAdvanceEvent()) {
			model.advance();

		} else if (e.isRestartEvent()) {

			if (view.getRunning()) {

				view.setRunning(false);
			}

			model.resetBoard();
		} else if (e.isSliderChangedEvent()) {

			SliderChangedEvent se = (SliderChangedEvent) e;

			if (se.getIsSizeSlider()) {
				model.setBoardSize(se.getSliderValue());
			} else {

				view.setDelay(se.getSliderValue());
			}

		} else if (e.isSpotClickedEvent()) {

			SpotClickedEvent se = (SpotClickedEvent) e;
			model.toggleSpot(se.getCoordsClicked());
		}
	}
}
