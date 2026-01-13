package artcreator.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import artcreator.creator.port.Creator;
import artcreator.creator.port.Nutzerinput;
import artcreator.statemachine.port.Observer;
import artcreator.statemachine.port.State;
import artcreator.statemachine.port.Subject;

public class Controller implements ActionListener, Observer {

    private CreatorFrame myView;
    private Creator myModel;
    private Subject subject;
    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

    public Controller(CreatorFrame view, Subject subject, Creator model) {
        this.myView = view;
        this.myModel = model;
        this.subject = subject;
        this.subject.attach(this);
    }

    
    @Override
    public synchronized void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        String text = ((JButton) e.getSource()).getText();
// die SwitchLogik implementiert
        switch (command) {
            case "befehl_bildladen":
                try {
                    JFileChooser dateiAuswahl = new JFileChooser();
                    int ergebnis = dateiAuswahl.showOpenDialog(this.myView);

                    if (ergebnis == JFileChooser.APPROVE_OPTION) {
                        File ausgewaehlteDatei = dateiAuswahl.getSelectedFile();
                        String pfad = ausgewaehlteDatei.getAbsolutePath();
                        
                        LOGGER.log(Level.INFO, "Datei ausgewählt: " + pfad);
                        CompletableFuture.runAsync(() ->
                        {
                            System.out.println("[DEBUG] Rufe lade_bild im Model auf...");
                            this.myModel.lade_bild(pfad);
                        })
                        .exceptionally(ex -> {
                            System.err.println("[FEHLER] Fehler im Hintergrund-Thread!");
                            ex.printStackTrace();return null;
                        });
                    } else {
                        LOGGER.log(Level.INFO, "Dateiauswahl wurde abgebrochen.");
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Fehler beim Laden der Datei.", ex);
                }
                break;

            case "befehl_verarbeiten":
                try {
                    LOGGER.log(Level.INFO, "Verarbeitung wird gestartet.");

                            int breite = Integer.parseInt(this.myView.getBreiteInput());
                            int hoehe  = Integer.parseInt(this.myView.getHoeheInput());
                            int farben = Integer.parseInt(this.myView.getFarbenInput()); 
                            String bg  = this.myView.getHintergrundInput();

                            Nutzerinput input = new Nutzerinput(breite, hoehe, farben, bg);

                            CompletableFuture.runAsync(() -> this.myModel.erstelle_vorschau(input));

                        } catch (Exception ex) {
                            LOGGER.log(Level.SEVERE, "Fehler: Ungültige Eingabe!", ex);
                        }
                break;
        }
    }

    @Override
    public void update(State newState) {
    }
}