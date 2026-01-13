package artcreator.gui;

import java.util.logging.Logger;
import java.util.logging.Level;
import static artcreator.statemachine.port.State.S.*;

import java.awt.BorderLayout; 
import java.awt.GridLayout;
import java.awt.Image;
import java.util.TooManyListenersException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;     
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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

    private static final int WIDTH = 1400; // Etwas breiter gemacht für 2 Bilder
    private static final int HEIGHT = 1080;

    //Buttons und Pannel und label
    private JButton     bild_laden              = new JButton("Bild laden");
    private JButton     bild_verarbeiten        = new JButton("Bild verarbeiten");
    private JLabel      bild_label_roh          = new JLabel("kein RohBild",      javax.swing.SwingConstants.CENTER);
    private JLabel      bild_label_vorschau     = new JLabel("kein VorschauBild", javax.swing.SwingConstants.CENTER);
    private JTextField  parameter_breite        = new JTextField("100", 5);
    private JTextField  parameter_hoehe         = new JTextField("100", 5);
    private JTextField  parameter_farben        = new JTextField("16", 3); 
    private JTextField  parameter_hintergrund   = new JTextField("#FFFFFF", 7); 
    //
    private JPanel buttonPanel              = new JPanel();

    public CreatorFrame() throws TooManyListenersException {
        super("ArtCreator");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(WIDTH, HEIGHT);
        this.setLocationRelativeTo(null);
        this.subject.attach(this);
        this.controller = new Controller(this, subject, creator);

        // Layout, button config und komponenten dem Frame hinzufügen + Getter außerhalb
        this.setLayout(new BorderLayout());
        
        //oben (bzw. Mitte für die Bilder)
        JPanel bilderBereich = new JPanel(new GridLayout(1, 2, 10, 0)); // 2 Spalten für die Bilder

        JScrollPane scrollRoh = new JScrollPane(this.bild_label_roh);
        scrollRoh.setBorder(javax.swing.BorderFactory.createTitledBorder("Original"));
        
        JScrollPane scrollVorschau = new JScrollPane(this.bild_label_vorschau);
        scrollVorschau.setBorder(javax.swing.BorderFactory.createTitledBorder("Vorschau"));

        bilderBereich.add(scrollRoh);
        bilderBereich.add(scrollVorschau);
        this.add(bilderBereich, BorderLayout.CENTER);

        //unten
        this.bild_laden.addActionListener       (this.controller);
        this.bild_verarbeiten.addActionListener (this.controller);
        
        bild_laden.setActionCommand             ("befehl_bildladen");
        bild_verarbeiten.setActionCommand       ("befehl_verarbeiten");

        // Erst die Eingabefelder, dann die Buttons
        this.buttonPanel.add(new JLabel("Breite:"));
        this.buttonPanel.add(this.parameter_breite);
        this.buttonPanel.add(new JLabel("Höhe:"));
        this.buttonPanel.add(this.parameter_hoehe);
        this.buttonPanel.add(new JLabel("Farben:"));
        this.buttonPanel.add(this.parameter_farben);
        this.buttonPanel.add(new JLabel("BG (Hex):"));
        this.buttonPanel.add(this.parameter_hintergrund);
        
        this.buttonPanel.add(this.bild_laden);
        this.buttonPanel.add(this.bild_verarbeiten);
        this.add(this.buttonPanel, BorderLayout.SOUTH);
        //
    }

    // Getter
    public String getBreiteInput() {
        return this.parameter_breite.getText();}

    public String getHoeheInput() {
        return this.parameter_hoehe.getText();}

    public JLabel getBildLabelRoh() {
        return this.bild_label_roh;}
    
    public JLabel getBildLabelVorschau() {
        return this.bild_label_vorschau;
    }

    public String getFarbenInput() {
         return parameter_farben.getText();}

    public String getHintergrundInput() {
         return parameter_hintergrund.getText();}

    @Override
    public void update(State newState) {
        Logger logger = Logger.getLogger(this.getClass().getName());

        if (newState == kein_bild) {
            logger.log(Level.INFO, "[GUI-UPDATE] Zustand: Kein Bild vorhanden.");
            this.bild_laden.setEnabled(true);
            this.bild_verarbeiten.setEnabled(false);
            this.bild_label_roh.setText("Bitte wählen Sie ein Bild aus.");
        } 
        else if (newState == bild_geladen) {
            logger.log(Level.INFO, "[GUI-UPDATE] Zustand: Bild erfolgreich geladen.");
            
            this.bild_laden.setEnabled(true);
            this.bild_verarbeiten.setEnabled(true); 
            
            // Bild anzeigen
            java.awt.image.BufferedImage img = this.creator.get_mein_bild_roh();
            if (img != null) {
                this.bild_label_roh.setIcon(new ImageIcon(img));
                this.bild_label_roh.setText("");
            }
        } 
        else if (newState == vorlage_generiert) {
            logger.log(Level.INFO, "[GUI-UPDATE] Zustand: Vorschau wurde generiert.");
        
            java.awt.image.BufferedImage vorschau = this.creator.get_mein_bild_vorschau();
        
            if (vorschau != null) {
                Image scaled = scaleImageForDisplay(vorschau, 8002);
            this.bild_label_vorschau.setIcon(new ImageIcon(scaled));
            this.bild_label_vorschau.setText("");
            }
        }
    }

    private Image scaleImageForDisplay(java.awt.image.BufferedImage img, int targetWidth) {
    double ratio = (double) img.getHeight() / img.getWidth();
    int targetHeight = (int) (targetWidth * ratio);
    return img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
}
}