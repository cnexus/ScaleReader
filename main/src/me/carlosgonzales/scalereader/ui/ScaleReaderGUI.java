package me.carlosgonzales.scalereader.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Carlos on 1/23/2015.
 */
public class ScaleReaderGUI extends JFrame {
	private static final String NAME = "ScaleReader";

	private ScaleReaderGUI(String name){
		super(name);
		this.setJMenuBar(new ActionBar(this));
		this.setSize(500, 400);
		this.setResizable(false);
	}

	public ScaleReaderGUI(){
		this(NAME);
	}

	public static void main(String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI(){
		try {
			Font f = new Font("Tahoma", Font.PLAIN, 18);
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			UIManager.getLookAndFeelDefaults()
					.put("defaultFont", f);

			ScaleReaderGUI main = new ScaleReaderGUI();
			main.setVisible(true);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
}
