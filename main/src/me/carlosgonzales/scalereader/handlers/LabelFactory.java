package me.carlosgonzales.scalereader.handlers;

/**
 * Created by Carlos on 1/26/2015.
 */
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedString;


public class LabelFactory {
	private static final Point SIZE = new Point(335, 175);
	private static final int VSPACE = 30;
	private static int VGAP;

	private LabelFactory(){
	}

	public static File createLabel(String... params){
		String ext = "jpg";
		File imageFile = null;
		try {
			imageFile = File.createTempFile("label", "." + ext);
			BufferedImage image = new BufferedImage(SIZE.x, SIZE.y, BufferedImage.TYPE_INT_RGB);

			Graphics2D g = image.createGraphics();
			Font plain = new Font("Verdana", Font.PLAIN, 16);
			Font emph = new Font("Verdana", Font.BOLD, 16);

			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
					RenderingHints.VALUE_FRACTIONALMETRICS_ON);

			g.setFont(plain);
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, SIZE.x, SIZE.y);
			g.setColor(Color.BLACK);

			VGAP = (int) (SIZE.y - (params.length * VSPACE/2 + g.getFontMetrics().getHeight())) / 2;

			int availX = SIZE.x;
			int start = VGAP;

			for(int i = 0; i < params.length; i++){
				int x = (availX - (int)g.getFontMetrics().getStringBounds(params[i], g).getWidth()) / 2;
				int y = start + i * VSPACE;
				AttributedString as = new AttributedString(params[i]);
				as.addAttribute(TextAttribute.FONT, plain);

				if(params[i].indexOf(":") != -1 && params[i].indexOf(":") == params[i].lastIndexOf(":"))
					as.addAttribute(TextAttribute.FONT, emph, 0, params[i].indexOf(":") + 1);

				g.drawString(as.getIterator(), x, y);
			}

			ImageIO.write(image, ext, imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(imageFile.getAbsolutePath()), null);

		return imageFile;
	}

	public static void main(String[] args) throws IOException {
		File f = LabelFactory.createLabel("Carton: 5", "Weight: 25.78 kg",  "Status: PASS", "2013-05-12 18:09:45");
		java.awt.Desktop.getDesktop().open(f);
	}
}
