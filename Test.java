
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Test {

	private static final int SIDE = 1000;

	public static void main(String args[]) {

		for (int i = 1; i <= 10; i++) {
			ImageGenerator imageGenerator = new ImageGenerator(SIDE);
			ArrayList<int[]> microscopeImage = imageGenerator.MicroscopeImageGenerator();
			ArrayList<int[]> dyeSensorImage = imageGenerator.DyeSensorImageGenerator();

			CancerChecker cancerChecker = new CancerChecker(SIDE);
			int[] arr = cancerChecker.check(microscopeImage, dyeSensorImage);
			
			double rate = (double) arr[0] / arr[1];
			System.out.println("The " + i + " th try: ");
			System.out.printf("    The rate of dye is: %.3f", rate);
			System.out.println();
			System.out.println("    Have Cancer?  " + (rate >= 0.1 ? "YES" : "NO"));

			showImage(microscopeImage, 1);
			showImage(dyeSensorImage, 2);
		}
		
	}

	private static void showImage(ArrayList<int[]> image, int num) {

		int[][] pixels = new int[SIDE][SIDE];
		for (int[] p : image) {
			pixels[p[1]][p[0]] = 255;
		}
		BufferedImage img = new BufferedImage(SIDE, SIDE, BufferedImage.TYPE_BYTE_BINARY);
		for (int y = 0; y < SIDE; y++) {
		    for (int x = 0; x < SIDE; x++) {
		        Color color = new Color(pixels[y][x], pixels[y][x], pixels[y][x]);
		        img.setRGB(x, y, color.getRGB());
		    }
		}
		try {
		    ImageIO.write(img, "png",  new File(num + ".png"));
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
	}
}