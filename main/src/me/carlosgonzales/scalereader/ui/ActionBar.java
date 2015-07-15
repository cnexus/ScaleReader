package me.carlosgonzales.scalereader.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Carlos on 1/23/2015.
 */
public class ActionBar extends JMenuBar{
	private static final String[] TITLES = {" Label Printing "};

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

			final JCheckBoxMenuItem printBox = new JCheckBoxMenuItem("Print label on <PASS>");
			printBox.setSelected(true);
			parent.setPrint(true);

			printBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					parent.setPrint(printBox.getState());
				}
			});

			menus[i].add(printBox);
		}
	}
}
