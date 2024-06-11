package com.phenix.xmlbaton;

import com.phenix.timecode.Timecode;
import com.phenix.xmlbaton.exception.NotCompatibleFileException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Gère l'XML venant du logiciel Baton.
 *
 * @author <a href="mailto:edouard128@hotmail.com">Edouard Jeanjean</a>
 */
public class XMLBaton {

    /**
     * Sert pour décoder l'XML.
     */
    private Document document;

    /**
     * Sert pour décoder l'XML.
     */
    private Element racine;

    /**
     * Sert pour décoder l'XML.
     */
    private NodeList racineNoeuds;

    /**
     * Sert pour décoder l'XML.
     */
    private NodeList streamnode;

    /**
     * Nom du fichier vidéo analysé par Baton.
     */
    private String nom_fichier;

    /**
     * Hauteur de la vidéo analysée.
     */
    private int hauteur;

    /**
     * Largeur de la vidéo analysée.
     */
    private int largeur;

    /**
     * Timecode de début de la vidéo analysé.
     */
    private String tcstart;

    /**
     * Durée de la vidéo analysée.
     */
    private Timecode duree;

    /**
     * Framerate de la vidéo analysée.
     */
    private int framerate;

    /**
     * Contient la liste des erreurs du rapport Baton.
     */
    private final ListeErreur liste_erreur;

    /**
     * L'extension du fichier XML Baton.
     */
    public static final String[] LISTE_EXTENSION = new String[]{".xml", ".bvr"};

    /**
     * Nom du test plan utilisé pour le rapport.
     */
    private String test_plan;

    /**
     * Récupérer des informations d'un rapport Baton formaté en XML.
     *
     * @param fichier Le fichier Baton à traiter.
     *
     * @throws NotCompatibleFileException Le fichier donné n'est pas compatible.
     */
    public XMLBaton(File fichier) throws NotCompatibleFileException {
        try {
            // Le fichier à analyser.
            this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fichier);

            // La racine du document (taskReport).
            this.racine = this.document.getDocumentElement();

            // On récupère directement les enfants.
            this.racineNoeuds = this.racine.getChildNodes();

            NodeList nodelist_taskinfo = this.racine.getElementsByTagName("taskinfo");
            // Si on est dans un BVR (XML), on a les informations sur le test plan.
            if (nodelist_taskinfo.getLength() > 0) {
                this.test_plan = ((Element) nodelist_taskinfo.item(0)).getAttribute("testPlan");
            }

            // On parcourt les nodes :
            for (int i = 0; i < this.racineNoeuds.getLength(); i++) {

                // Si c'est effectivement un node, on regarde :
                if (this.racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {

                    // En fonction de son nom, on fait un traitement :
                    switch (this.racineNoeuds.item(i).getNodeName()) {

                        // On récupère depuis le node "toplevelinfo" l'information du nom de fichier vidéo analysée :
                        case "toplevelinfo":
                            this.nom_fichier = ((Element) this.racineNoeuds.item(i)).getAttribute("Filepath");
                            break;

                        // Si c'est streamnode (il y a en a 2 dans l'XML).
                        case "streamnode":
                            NodeList nl = (NodeList) this.racineNoeuds.item(i).getAttributes().getNamedItem("id");

                            // On ne prend que celui avec les infos images :
                            if ((nl + "").equals("id=\"1\"")) {

                                // Récupérer le timecode de début :
                                int b = (Integer.parseInt(((Element) this.racineNoeuds.item(i)).getAttribute("StartTimecode")));

                                this.tcstart = b + "";

                                this.streamnode = (NodeList) this.racineNoeuds.item(i).getChildNodes();
                            }
                            break;
                    }
                }
            }
        } catch (IOException | NumberFormatException | ParserConfigurationException | SAXException exception) {
            throw new NotCompatibleFileException(fichier.getName());
        }

        // S'il est null c'est qu'on ne peut pas parser le fichier.
        if (this.streamnode == null) {
            throw new NotCompatibleFileException(fichier.getName());
        }

        // On cherche le node "info":
        NodeList field = findNodeListByName(this.streamnode, "info");

        // Ici se trouve la liste des fields!!
        for (int i = 0; i < field.getLength(); i++) {

            // On ne prend que les nodes :
            if (field.item(i).getNodeType() == Node.ELEMENT_NODE) {

                // En fonction du nom du node :
                switch (field.item(i).getNodeName()) {
                    // Quand on tient une des erreurs, on l'ajoute à la liste :
                    case "field":

                        if (((Element) field.item(i)).getAttribute("name").equals("Resolution")) {
                            // Récupérer la résolution (hauteur x largeur) :
                            String a = ((Element) field.item(i)).getAttribute("value");
                            Scanner sc = new Scanner(a);
                            sc.useDelimiter("x");
                            this.largeur = sc.nextInt();
                            this.hauteur = sc.nextInt();
                            sc.close();
                        } else if (((Element) field.item(i)).getAttribute("name").equals("Frame Rate")) {
                            this.framerate = Integer.parseInt(((Element) field.item(i)).getAttribute("value"));

                            // Seulement maintenant, on peut définir le TC start (car il faut connaitre le framerate) :
                            this.tcstart = new Timecode((int) (Integer.parseInt(this.tcstart) / 1000D * this.framerate), this.framerate).toString();
                        } else if (((Element) field.item(i)).getAttribute("name").equals("Duration")) {
                            NodeList nl_duree = findNodeListByName(field.item(i).getChildNodes(), "DurationSMPTE");

                            // Vérifie qu'une durée est renseignée :
                            if (!((Element) field.item(i)).getAttribute("desc").equals("Info not found")) {
                                this.duree = new Timecode(((Element) nl_duree).getAttribute("value"));
                            } else {
                                this.duree = new Timecode(1);
                            }
                        }

                        break;
                }
            }
        }

        this.liste_erreur = new ListeErreur(fichier, ErreurBaton.CODEC_PR4444);
    }

    /**
     * Transforme la valeur sur 2 digits.
     *
     * @param valeur La valeur.
     * @return La valeur sous forme de 2 digits.
     */
    private String digit(int valeur) {
        return (valeur > 9) ? "" + valeur : "0" + valeur;
    }

    /**
     * Retourne le node enfant via son nom.
     *
     * @param list Le node où on doit chercher.
     * @param name Nom du node à trouver.
     * @return Retourne le node demandé, sinon retourne {@code null} s'il ne
     * trouve pas.
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
     * Retourne la durée de la vidéo analysée.
     *
     * @return Durée.
     */
    public Timecode getDuree() {
        this.duree.setFramerate(this.framerate);
        return this.duree;
    }

    /**
     * Retourne le framerate de la vidéo analysée.
     *
     * @return Le framerate.
     */
    public int getFramerate() {
        return this.framerate;
    }

    /**
     * Retourne la hauteur de la vidéo analysé.
     *
     * @return La hauteur.
     */
    public int getHauteur() {
        return this.hauteur;
    }

    /**
     * Retourne la largeur de la vidéo analysée.
     *
     * @return La largeur.
     */
    public int getLargeur() {
        return this.largeur;
    }

    /**
     * Retourne la liste de toutes les erreurs.
     *
     * @return Liste d'erreur de Baton
     */
    public ListeErreur getListeErreur() {
        return this.liste_erreur;
    }

    /**
     * Retourne le nom de la vidéo analysé.
     *
     * @return Retourne le nom du fichier vidéo analysé.
     */
    public String getNomFichier() {
        return this.nom_fichier;
    }

    /**
     * Retourne le timecode de début de la vidéo analysé.
     *
     * @return Timecode début.
     */
    public String getStartTC() {
        return this.tcstart;
    }

    /**
     * Retourne le nom du test plan utilisé.<br>
     * Note : ne peut être récupéré que d'un BVR.
     *
     * @return Le nom du test plan.
     */
    public String getTestPlan() {
        return this.test_plan;
    }
}
