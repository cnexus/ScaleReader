package me.carlosgonzales.scalereader.ui;

import javax.swing.*;

/**
 * Created by Carlos on 1/23/2015.
 */
public class ActionBar extends JMenuBar{
	private static final String[] TITLES = {" File ", " Save "};

	private ScaleReaderGUI parent;
	private JMenu[] menus;

	public ActionBar(ScaleReaderGUI parent){
		this.parent = parent;
		initMenu();
	}

	private void initMenu(){
		setBorderPainted(true);
		menus = new JMenu[TITLES.length];

		for(int i = 0; i < TITLES.length; i++){
			menus[i] = new JMenu(TITLES[i]);
			this.add(menus[i]);
		}
	}
}
