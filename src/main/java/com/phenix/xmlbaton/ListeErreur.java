package com.phenix.xmlbaton;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Répertorie la liste des erreurs Baton dans le fichier XML Baton.
 *
 * @author <a href="mailto:edouard128@hotmail.com">Edouard Jeanjean</a>
 */
public class ListeErreur {

    /**
     * Nom générique de l'erreur de conformance (drop render).
     */
    public static final String CONFORMANCE_ERROR = "Conformance";

    /**
     * Nom de l'erreur pour un defective pixel.
     */
    public static final String DEFECTIVE_PIXEL_ERROR = "Defective Pixel";

    /**
     * Nom de l'erreur pour les blacks bars.
     */
    public static final String BLACK_BARS_ERROR = "Black Bars";

    /**
     * Nom de l'erreur pour les dropouts.
     */
    public static final String VIDEO_DROPOUT_ERROR = "Video Dropout";

    /**
     * Nom de l'erreur pour les duplicate frame.
     */
    public static final String DUPLICATE_FRAMES_ERROR = "Duplicate Frames";

    /**
     * Nom de l'erreur pour les freeze frame.
     */
    public static final String FREEZE_FRAMES_ERROR = "Freeze Frames";

    /**
     * Nom de l'erreur pour les flashs (PSE).
     */
    public static final String FLASHY_VIDEO_ERROR = "Flashy Video";

    /**
     * Document XML.
     */
    private Document document;

    /**
     * Racine du document XML.
     */
    private Element racine;

    /**
     * Les noeuds dans la racine.
     */
    private NodeList racineNoeuds;

    /**
     * Les noeuds "streamnode".
     */
    private NodeList streamnode;

    /**
     * La liste d'erreur.
     */
    private final ArrayList<ErreurBaton> liste_erreur_baton;

    /**
     * Le codec de la vidéo utilisé pour la vérification Baton.
     */
    private final int codec;

    /**
     * Construit une liste d'erreur à partir d'une URL d'un rapport XML Baton et
     * du codec du fichier vidéo de base.
     *
     * @param fichier URL du fichier XML Baton.
     * @param codec Codec du fichier vidéo utilisé pour la vérification Baton.
     */
    public ListeErreur(File fichier, int codec) {
        this.codec = codec;
        this.init(fichier);

        this.liste_erreur_baton = new ArrayList<ErreurBaton>();

        NodeList errors = findNodeListByName(streamnode, "errors");
        {
            NodeList customchecks = findNodeListByName(errors, "customchecks");
            // Ici se trouve la liste des erreurs!!
            NodeList decodedvideochecks = findNodeListByName(customchecks, "decodedvideochecks");
            this.addList(decodedvideochecks);
        }
        // Erreur type conformance :
        {
            NodeList customchecks = findNodeListByName(errors, "conformancechecks");
            this.addListConformance(customchecks);
        }
    }

    /**
     * Retourne les erreurs se trouvant dans le rapport XML Baton.
     *
     * @param fichier Le fichier XML Baton.
     */
    private void init(File fichier) {

        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fichier);

            racine = document.getDocumentElement();
            racineNoeuds = racine.getChildNodes();

            for (int i = 0; i < racineNoeuds.getLength(); i++) {
                if (racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {

                    switch (racineNoeuds.item(i).getNodeName()) {

                        case "streamnode":
                            NodeList nl = (NodeList) racineNoeuds.item(i).getAttributes().getNamedItem("id");
                            // On ne prend que celui avec les infos images :
                            if ((nl + "").equals("id=\"1\"")) {
                                streamnode = (NodeList) racineNoeuds.item(i).getChildNodes();
                            }
                            break;
                    }
                }
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Retourne le node enfant via son nom.
     *
     * @param list Le node où on doit chercher.
     * @param name Nom du node à trouver.
     *
     * @return Retourne le node demandé, sinon retourne {@code null} s'il
     * ne trouve pas.
     */
    private NodeList findNodeListByName(NodeList list, String name) {
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeName().equals(name)) {
                return ((Element) list.item(i)).getChildNodes();
            }
        }

        return null;
    }

    /**
     * Ajouter les erreurs se trouvant dans ce node (du rapport XML Baton) à la
     * liste d'erreur.
     *
     * @param nodeList Le node contenant la liste des erreurs à ajouter aux
     * erreurs.
     */
    private void addListConformance(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE && nodeList.item(i).getNodeName().equals("error")) {
                String description = ((Element) nodeList.item(i)).getAttribute("description");
                String smptetimecode = ((Element) nodeList.item(i)).getAttribute("smptetimecode");
                this.liste_erreur_baton.add(new ErreurBaton(description, 1, smptetimecode, smptetimecode, CONFORMANCE_ERROR, this.codec));
            }
        }
    }

    /**
     * Ajouter les erreurs se trouvant dans ce node (du rapport XML Baton) à la
     * liste d'erreur.
     *
     * @param nodeList Le node contenant la liste des erreurs à ajouter aux
     * erreurs.
     */
    private void addList(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {

                switch (nodeList.item(i).getNodeName()) {

                    // Quand on tient une des erreurs, on l'ajoute à la liste :
                    case "error":

                        Element element = ((Element) nodeList.item(i));

                        int FrameDuration = element.hasAttribute("FrameDuration") ? Integer.parseInt(element.getAttribute("FrameDuration")) : -1;
                        String description = element.hasAttribute("description") ? element.getAttribute("description") : "";
                        String smptetimecode = element.hasAttribute("startsmptetimecode") ? element.getAttribute("startsmptetimecode") : ""; //smptetimecode
                        String endsmptetimecode = element.hasAttribute("endsmptetimecode") ? element.getAttribute("endsmptetimecode") : "";
                        String item = element.hasAttribute("item") ? element.getAttribute("item") : "";

                        if (item.equals("Defective Pixel")) {
                            ArrayList<Pixel> liste_pixel = new ArrayList<Pixel>();

                            // Récupère les positions du/des pixel(s) :
                            Node param = ((Element) element.getElementsByTagName("Params").item(0)).getElementsByTagName("Param").item(0);
                            String coordonnees = ((Element) param).getAttribute("Value"); // Récupère le 1er car il n'y en a qu'un.

                            String[] liste = coordonnees.split("\\D");

                            String liste_nombre = "";

                            for (int j = 0; j < liste.length; j++) {
                                liste_nombre += " " + liste[j];
                            }

                            int nb_pixel = (liste_nombre.split("\\s+").length - 1) / 2;

                            Scanner sc = new Scanner(liste_nombre);

                            for (int nombre = 0; nombre < nb_pixel; nombre++) {
                                liste_pixel.add(new Pixel(sc.nextInt(), sc.nextInt()));
                            }

                            sc.close();
                            this.liste_erreur_baton.add(new ErreurBatonDefectivePixel(description, FrameDuration, smptetimecode, endsmptetimecode, item, this.codec, liste_pixel));
                        } else {
                            // On ajoute les erreurs que si elles ont une durée (les remarques générales ne nous intéresse pas).
                            if (FrameDuration != -1) {
                                this.liste_erreur_baton.add(new ErreurBaton(description, FrameDuration, smptetimecode, endsmptetimecode, item, this.codec));
                            }
                        }

                        break;
                }
            }
        }
    }

    /**
     * Retourne le codec.
     *
     * @return Le codec.
     */
    public int getCodec() {
        return this.codec;
    }

    /**
     * Retourne la liste d'erreur.
     *
     * @return Liste d'erreur.
     */
    public ArrayList<ErreurBaton> getList() {
        return this.liste_erreur_baton;
    }

    /**
     * Retourne la liste d'erreur avec un seul type d'erreur, reçu en
     * paramètre.
     *
     * @param type_erreur Type d'erreur qu'on veut.
     * @return La liste d'erreur.
     */
    public ArrayList<ErreurBaton> getList(String type_erreur) {
        ArrayList<ErreurBaton> liste_erreur = new ArrayList<ErreurBaton>();

        for (int i = 0; i < this.liste_erreur_baton.size(); i++) {
            if (this.liste_erreur_baton.get(i).getItem().equals(type_erreur)) {
                liste_erreur.add(this.liste_erreur_baton.get(i));
            }
        }

        // On tri la liste avant de la retourner.
        liste_erreur.sort((erreur1, erreur12) -> {
            return erreur1.getTcStart().compareTo(erreur12.getTcStart());
        });

        return liste_erreur;
    }

    /**
     * Retourne l'ensemble des types d'erreurs se trouvant dans le rapport.
     *
     * @return Liste des types d'erreur.
     */
    public ArrayList<String> getListTypeErreur() {
        ArrayList<String> liste_erreur = new ArrayList<String>();

        for (int i = 0; i < this.liste_erreur_baton.size(); i++) {
            if (!liste_erreur.contains(this.liste_erreur_baton.get(i).getItem())) {
                liste_erreur.add(this.liste_erreur_baton.get(i).getItem());
            }
        }

        return liste_erreur;
    }
}
