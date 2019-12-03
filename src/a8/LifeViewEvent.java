package a8;

/*
Contains the major types of button and slider type events:
1. RandomEvent
2. TorusEvent
3. AdvanceEvent
4. TimeToggleEvent
5. RestartEvent
6. SliderChangeEvent
7. SpotClickedEvent
 */
abstract class LifeViewEvent {

	public boolean isRandomEvent() {

		return false;
	}

	public boolean isTorusEvent() {

		return false;
	}

	public boolean isThresholdSetEvent() {

		return false;
	}

	public boolean isAdvanceEvent() {

		return false;
	}

	public boolean isTimeToggleEvent() {

		return false;
	}

	public boolean isRestartEvent() {

		return false;
	}

	public boolean isSliderChangedEvent() {

		return false;
	}

	public boolean isSpotClickedEvent() {

		return false;
	}
}

class RandomEvent extends LifeViewEvent {

	public boolean isRandomEvent() {

		return true;
	}
}

class TorusEvent extends LifeViewEvent {

	public boolean isTorusEvent() {

		return true;
	}
}

class ThresholdSetEvent extends LifeViewEvent {

	public boolean isThresholdSetEvent() {

		return true;
	}
}

class AdvanceEvent extends LifeViewEvent {

	public boolean isAdvanceEvent() {

		return true;
	}
}

class TimeToggleEvent extends LifeViewEvent {

	public boolean isTimeToggleEvent() {

		return true;
	}
}

class RestartEvent extends LifeViewEvent {

	public boolean isRestartEvent() {

		return true;
	}
}

class SliderChangedEvent extends LifeViewEvent {

	private int sliderValue;

	SliderChangedEvent(int sliderValue) {

		this.sliderValue = sliderValue;
	}

	public int getSliderValue() {

		return sliderValue;
	}

	public boolean isSliderChangedEvent() {

		return true;
	}
}

class SpotClickedEvent extends LifeViewEvent {

	private int[] coordsClicked;

	SpotClickedEvent(int[] coordsClicked) {

		this.coordsClicked = coordsClicked;
	}

	public int[] getCoordsClicked() {

		return coordsClicked;
	}

	public boolean isSpotClickedEvent() {

		return true;
	}
}