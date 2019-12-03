package a8;

import javax.swing.*;

public class Main {

	public static void main(String[] args) {

		LifeModel lifeModel = new LifeModel();
		LifeView lifeView = new LifeView();
		LifeController lifeController = new LifeController(lifeModel, lifeView);

		JFrame mainFrame = new JFrame();
		mainFrame.setTitle("Conway's Game of Life");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainFrame.setContentPane(lifeView);

		mainFrame.pack();
		mainFrame.setVisible(true);
	}
}
