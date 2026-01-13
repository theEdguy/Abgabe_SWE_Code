package artcreator.gui;

import java.util.TooManyListenersException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.WindowConstants;

import artcreator.creator.CreatorFactory;
import artcreator.creator.port.Creator;
import artcreator.statemachine.StateMachineFactory;
import artcreator.statemachine.port.Observer;
import artcreator.statemachine.port.State;
import artcreator.statemachine.port.Subject;

public class CreatorFrame extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;
	private transient Creator creator = CreatorFactory.FACTORY.creator();
	private transient Subject subject = StateMachineFactory.FACTORY.subject();
	private transient Controller controller;

	private static final int WIDTH = 900;
	private static final int HEIGHT = 500;

	private JButton btnLoad = new JButton("Bild laden");
	private JLabel imageLabel = new JLabel();
	private JLabel previewLabel = new JLabel();
	private JPanel panel = new JPanel(new GridBagLayout());
	private JTextField sizeField = new JTextField("64", 5);
	private JTextField colorField = new JTextField("64", 5);
	private JButton btnSetParams = new JButton("Parameter übernehmen");
	private JButton btnPreview = new JButton("Vorschau generieren");
	private JButton btnConfirm = new JButton("Verarbeitung bestätigen");
	private JButton btnExport = new JButton("Vorschau exportieren");
	private JLabel statusLabel = new JLabel("Status: Initialisiert");

	private java.awt.image.BufferedImage loadedImage = null;
	private java.awt.image.BufferedImage previewImage = null;

	public CreatorFrame() throws TooManyListenersException {
		super("ArtCreator");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);
		this.subject.attach(this);
		this.controller = new Controller(this, subject, creator);

		imageLabel.setPreferredSize(new java.awt.Dimension(350, 350));
		previewLabel.setPreferredSize(new java.awt.Dimension(350, 350));

		/* build view */
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
		this.panel.add(this.btnLoad, gbc);
		gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 3;
		this.panel.add(this.imageLabel, gbc);
		gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 3;
		this.panel.add(this.previewLabel, gbc);
		gbc.gridheight = 1;

		gbc.gridx = 0; gbc.gridy = 1;
		this.panel.add(new JLabel("Größe:"), gbc);
		gbc.gridx = 0; gbc.gridy = 2;
		this.panel.add(sizeField, gbc);
		gbc.gridx = 0; gbc.gridy = 3;
		this.panel.add(new JLabel("Farbe:"), gbc);
		gbc.gridx = 0; gbc.gridy = 4;
		this.panel.add(colorField, gbc);
		gbc.gridx = 0; gbc.gridy = 5;
		this.panel.add(btnSetParams, gbc);
		gbc.gridx = 0; gbc.gridy = 6;
		this.panel.add(btnPreview, gbc);
		gbc.gridx = 0; gbc.gridy = 7;
		this.panel.add(btnConfirm, gbc);
		gbc.gridx = 1; gbc.gridy = 7;
		this.panel.add(btnExport, gbc);
		gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
		this.panel.add(statusLabel, gbc);
		this.btnLoad.addActionListener(e -> onLoadImage());
		this.btnSetParams.addActionListener(e -> onSetParameters());
		this.btnPreview.addActionListener(e -> onGeneratePreview());
		this.btnConfirm.addActionListener(e -> onConfirmProcessing());
		this.btnExport.addActionListener(e -> onExportPreview());
		this.getContentPane().add(this.panel);
	}

	private void onLoadImage() {
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			java.io.File file = fileChooser.getSelectedFile();
			try {
				loadedImage = javax.imageio.ImageIO.read(file);
				if (loadedImage != null) {
					// Scale for display
					java.awt.Image scaled = loadedImage.getScaledInstance(350, 350, java.awt.Image.SCALE_SMOOTH);
					imageLabel.setIcon(new ImageIcon(scaled));
					imageLabel.setText("");
					
					// Call backend
					this.creator.loadImage(file.getAbsolutePath());
					this.creator.applyDefaultParameters();
				} else {
					imageLabel.setIcon(null);
					imageLabel.setText("Bildformat nicht unterstützt");
				}
			} catch (Exception ex) {
				imageLabel.setIcon(null);
				imageLabel.setText("Fehler beim Laden");
				ex.printStackTrace();
			}
			// Vorschau zurücksetzen
			previewImage = null;
			previewLabel.setIcon(null);
			previewLabel.setText("");
		}
	}

	private void onSetParameters() {
		try {
			int size = Integer.parseInt(sizeField.getText());
			int colors = Integer.parseInt(colorField.getText());
			artcreator.domain.Config config = new artcreator.domain.Config(colors, size, new java.util.ArrayList<>());
			this.creator.setParameters(config);
		} catch (NumberFormatException e) {
			javax.swing.JOptionPane.showMessageDialog(this, "Bitte gültige Zahlen eingeben", "Fehler", javax.swing.JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onGeneratePreview() {
		try {
			int size = Integer.parseInt(sizeField.getText());
			int colors = Integer.parseInt(colorField.getText());
			artcreator.domain.Config config = new artcreator.domain.Config(colors, size, new java.util.ArrayList<>());
			
			// Call backend
			Object result = this.creator.generatePreview(config);
			
			if (result instanceof int[][]) {
				int[][] pixels = (int[][]) result;
				int h = pixels.length;
				int w = pixels[0].length;
				
				previewImage = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
				for (int y = 0; y < h; y++) {
					for (int x = 0; x < w; x++) {
						previewImage.setRGB(x, y, pixels[y][x] | 0xFF000000);
					}
				}
				java.awt.Image scaledPreview = previewImage.getScaledInstance(350, 350, java.awt.Image.SCALE_DEFAULT);
				previewLabel.setIcon(new ImageIcon(scaledPreview));
				previewLabel.setText("");
			} else {
				previewLabel.setText("Keine Vorschau (Status prüfen)");
			}
		} catch (NumberFormatException e) {
			javax.swing.JOptionPane.showMessageDialog(this, "Bitte gültige Zahlen eingeben", "Fehler", javax.swing.JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onExportPreview() {
		// Exportiere das Vorschaubild als PNG
		if (previewImage != null) {
			try {
				javax.imageio.ImageIO.write(previewImage, "png", new java.io.File("export_preview.png"));
				javax.swing.JOptionPane.showMessageDialog(this, "Vorschau erfolgreich exportiert als export_preview.png", "Export", javax.swing.JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				javax.swing.JOptionPane.showMessageDialog(this, "Fehler beim Export: " + ex.getMessage(), "Export", javax.swing.JOptionPane.ERROR_MESSAGE);
			}
		} else {
			javax.swing.JOptionPane.showMessageDialog(this, "Keine Vorschau zum Exportieren vorhanden!", "Export", javax.swing.JOptionPane.WARNING_MESSAGE);
		}
	}

	private void onConfirmProcessing() {
		this.creator.confirmProcessing();
		javax.swing.JOptionPane.showMessageDialog(this, "Verarbeitung erfolgreich abgeschlossen!", "Erfolg", javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}

	public void update(State newState) {
		if (newState != null) {
			this.statusLabel.setText("Status: " + newState.toString());
		}
	}

}
