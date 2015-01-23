package me.carlosgonzales.scalereader.ui;

import javax.swing.*;

/**
 * Created by Carlos on 1/23/2015.
 */
public class ActionBar extends JMenuBar{
	private static final String[] titleBars = {" File ", " Save "};

	private ScaleReaderGUI parent;
	private JMenuItem[] items;

	public ActionBar(ScaleReaderGUI parent){
		this.parent = parent;
		this.setBorderPainted(true);


	}
}
