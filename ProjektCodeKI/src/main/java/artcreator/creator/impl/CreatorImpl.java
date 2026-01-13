package artcreator.creator.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import artcreator.domain.port.Domain;
import artcreator.statemachine.port.StateMachine;

import artcreator.domain.Bild;
import artcreator.domain.Gem;
import artcreator.domain.Palette;
import artcreator.domain.Vorlage;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatorImpl {

	private final StateMachine stateMachine;
	private final Domain domain;

	public CreatorImpl(StateMachine stateMachine, Domain domain) {
		this.stateMachine = stateMachine;
		this.domain = domain;
	}

	public void sysop(String str) {
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.log(Level.INFO, str);
	}

	public void loadImage(String path) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (img != null) {
			int width = img.getWidth();
			int height = img.getHeight();
			int[][] pixelDaten = new int[height][width];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					pixelDaten[y][x] = img.getRGB(x, y);
				}
			}
			Bild bild = new Bild(path, width, height, pixelDaten);
			this.domain.setOriginalImage(bild);
			
			// Check if config is set, otherwise set default parameters
			artcreator.domain.Config config = this.domain.getConfig();
			if (config == null) {
				int defaultFarbtiefe = 8;
				int defaultAufloesung = 1000;
				java.util.List<String> defaultFilter = new java.util.ArrayList<>();
				config = new artcreator.domain.Config(defaultFarbtiefe, defaultAufloesung, defaultFilter);
				this.domain.setConfig(config);
			}
		}
	}

	public void applyDefaultParameters() {
		int defaultFarbtiefe = 6;
		int defaultAufloesung = 64;
		java.util.List<String> defaultFilter = new java.util.ArrayList<>();
		artcreator.domain.Config config = new artcreator.domain.Config(defaultFarbtiefe, defaultAufloesung, defaultFilter);
		this.domain.setConfig(config);
		artcreator.domain.Palette palette = new artcreator.domain.Palette(64);
		// Palette könnte ggf. auch in Domain gespeichert werden, je nach Modell
	}

	public Object generatePreview(Object parameters) {
		if (parameters instanceof artcreator.domain.Config) {
			this.domain.setConfig((artcreator.domain.Config) parameters);
		}

		artcreator.domain.Bild bild = this.domain.getOriginalImage();
		artcreator.domain.Config config = this.domain.getConfig();
		if (bild == null || config == null) return null;
		
		int ziel = config.getAufloesung();
		int[][] orig = bild.getPixelDaten();
		int origW = bild.getBreite();
		int origH = bild.getHoehe();
		int[][] scaled = new int[ziel][ziel];
		
		// 1. Scaling with Average (Box Filter) instead of Nearest Neighbor
		double stepX = (double) origW / ziel;
		double stepY = (double) origH / ziel;

		for (int y = 0; y < ziel; y++) {
			for (int x = 0; x < ziel; x++) {
				int startX = (int) (x * stepX);
				int startY = (int) (y * stepY);
				int endX = (int) Math.min((x + 1) * stepX, origW);
				int endY = (int) Math.min((y + 1) * stepY, origH);
				
				// Ensure valid range
				if (endX <= startX) endX = startX + 1;
				if (endY <= startY) endY = startY + 1;
				if (endX > origW) endX = origW;
				if (endY > origH) endY = origH;

				long sumR = 0, sumG = 0, sumB = 0;
				int count = 0;

				for (int py = startY; py < endY; py++) {
					for (int px = startX; px < endX; px++) {
						int pixel = orig[py][px];
						sumR += (pixel >> 16) & 0xFF;
						sumG += (pixel >> 8) & 0xFF;
						sumB += (pixel) & 0xFF;
						count++;
					}
				}
				
				if (count > 0) {
					scaled[y][x] = (0xFF << 24) | ((int)(sumR/count) << 16) | ((int)(sumG/count) << 8) | (int)(sumB/count);
				} else {
					scaled[y][x] = orig[Math.min((int)(y*stepY), origH-1)][Math.min((int)(x*stepX), origW-1)];
				}
			}
		}

		// 2. Color Reduction (Quantization)
		int maxColors = config.getFarbtiefe();
		if (maxColors > 0) {
			scaled = reduceColors(scaled, maxColors);
		}

		// 3. Gem Painting Integration: Create Palette and Gems
		Map<Integer, Gem> colorMap = new HashMap<>();
		List<Gem> gemList = new ArrayList<>();
		String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*()_+";
		int symbolIndex = 0;

		for (int y = 0; y < ziel; y++) {
			for (int x = 0; x < ziel; x++) {
				int rgb = scaled[y][x];
				int color = rgb & 0xFFFFFF; 
				
				if (!colorMap.containsKey(color)) {
					if (symbolIndex < symbols.length()) {
						String hex = String.format("#%06X", color);
						Gem gem = new Gem(hex, symbols.charAt(symbolIndex++));
						colorMap.put(color, gem);
						gemList.add(gem);
					} else {
						if (!gemList.isEmpty()) {
							 colorMap.put(color, gemList.get(0));
						}
					}
				}
			}
		}

		Palette palette = new Palette(gemList.size());
		Vorlage vorlage = new Vorlage(scaled, gemList, palette);
		this.domain.setCurrentVorlage(vorlage);

		return scaled;
	}

	private int[][] reduceColors(int[][] image, int maxColors) {
		int h = image.length;
		int w = image[0].length;

		// 1. Sammle alle Pixel in eine Liste (für einfacheren Zugriff)
		List<Integer> allPixels = new ArrayList<>();
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				allPixels.add(image[y][x] & 0xFFFFFF); // Alpha ignorieren
			}
		}

		// Wenn wir weniger Farben haben als maxColors, sind wir fertig
		Map<Integer, Integer> uniqueColors = new HashMap<>();
		for (int p : allPixels) uniqueColors.put(p, 1);
		if (uniqueColors.size() <= maxColors) return image;

		// 2. K-MEANS INITIALISIERUNG
		// Wähle zufällige Start-Zentroiden aus den vorhandenen Pixeln
		List<Integer> centroids = new ArrayList<>();
		java.util.Random rand = new java.util.Random();
		for (int i = 0; i < maxColors; i++) {
			centroids.add(allPixels.get(rand.nextInt(allPixels.size())));
		}

		// 3. K-MEANS ITERATION (z.B. 10 Runden reichen meistens für Previews)
		for (int i = 0; i < 10; i++) {
			Map<Integer, List<Integer>> clusters = new HashMap<>();
			for (int k = 0; k < maxColors; k++) clusters.put(k, new ArrayList<>());

			// A. Jeden Pixel dem nächsten Zentroid zuweisen
			for (Integer pixel : allPixels) {
				int nearestIndex = 0;
				int minDistance = Integer.MAX_VALUE;
				
				for (int k = 0; k < centroids.size(); k++) {
					int dist = getColorDistance(pixel, centroids.get(k));
					if (dist < minDistance) {
						minDistance = dist;
						nearestIndex = k;
					}
				}
				clusters.get(nearestIndex).add(pixel);
			}

			// B. Zentroide neu berechnen (Durchschnitt der Cluster)
			boolean changed = false;
			for (int k = 0; k < maxColors; k++) {
				List<Integer> clusterPixels = clusters.get(k);
				if (clusterPixels.isEmpty()) {
					// Falls Cluster leer, weise zufälligen neuen Startpunkt zu
					centroids.set(k, allPixels.get(rand.nextInt(allPixels.size())));
					continue;
				}

				long sumR = 0, sumG = 0, sumB = 0;
				for (int p : clusterPixels) {
					sumR += (p >> 16) & 0xFF;
					sumG += (p >> 8) & 0xFF;
					sumB += p & 0xFF;
				}
				int newR = (int) (sumR / clusterPixels.size());
				int newG = (int) (sumG / clusterPixels.size());
				int newB = (int) (sumB / clusterPixels.size());
				int newColor = (newR << 16) | (newG << 8) | newB;

				if (!centroids.get(k).equals(newColor)) {
					centroids.set(k, newColor);
					changed = true;
				}
			}
			if (!changed) break; // Früher Abbruch wenn stabil
		}

		// 4. Das Bild neu mappen basierend auf der finalen Palette
		int[][] reduced = new int[h][w];
		Map<Integer, Integer> cache = new HashMap<>(); // Performance Cache

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int original = image[y][x] & 0xFFFFFF;
				if (!cache.containsKey(original)) {
					cache.put(original, findClosestColor(original, centroids));
				}
				reduced[y][x] = (0xFF << 24) | cache.get(original);
			}
		}
		return reduced;
	}

	// Hilfsmethode für Distanz (Quadratisch ist schneller, da keine Wurzel nötig)
	private int getColorDistance(int c1, int c2) {
		int r = ((c1 >> 16) & 0xFF) - ((c2 >> 16) & 0xFF);
		int g = ((c1 >> 8) & 0xFF) - ((c2 >> 8) & 0xFF);
		int b = (c1 & 0xFF) - (c2 & 0xFF);
		return r*r + g*g + b*b;
	}

	private int findClosestColor(int target, List<Integer> palette) {
		int minDist = Integer.MAX_VALUE;
		int closest = 0;

		for (int color : palette) {
			int dist = getColorDistance(target, color);
			if (dist < minDist) {
				minDist = dist;
				closest = color;
			}
		}
		return closest;
	}

	public void setParameters(Object parameters) {
		if (parameters instanceof artcreator.domain.Config) {
			this.domain.setConfig((artcreator.domain.Config) parameters);
		}
	}

	public void confirmProcessing() {
		artcreator.domain.Vorlage vorlage = this.domain.getCurrentVorlage();
		if (vorlage != null) {
			vorlage.setFinalized(true);
			this.domain.setCurrentVorlage(vorlage);
		}
	}

	// Hilfsfunktionen
	// (z.B. Bild laden, Farbtiefe reduzieren, etc.)

}
