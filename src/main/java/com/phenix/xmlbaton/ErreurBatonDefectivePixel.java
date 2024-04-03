package com.phenix.xmlbaton;

import java.util.ArrayList;

/**
 * Erreur Baton de catégorie "defective pixel".
 *
 * @author <a href="mailto:edouard128@hotmail.com">Edouard Jeanjean</a>
 */
public class ErreurBatonDefectivePixel extends ErreurBaton {

    /**
     * Liste des positions (x, y) des pixels morts.
     */
    private final ArrayList<Pixel> liste_pixel;

    /**
     * Définit une erreur Baton de catégorie "defective pixel".
     *
     * @param description Description.
     * @param duree Durée.
     * @param tcStart Timecode début.
     * @param tcEnd Timecode de fin.
     * @param item Type d'erreur Baton.
     * @param idCodec ID du codec.
     * @param liste_pixel Liste des pixels.
     */
    public ErreurBatonDefectivePixel(String description, int duree, String tcStart, String tcEnd, String item, int idCodec, ArrayList<Pixel> liste_pixel) {
        super(description, duree, tcStart, tcEnd, item, idCodec);
        this.liste_pixel = liste_pixel;
    }

    /**
     * Ajoute un defective pixel à cette erreur Baton.
     *
     * @param x Coordonnées x.
     * @param y Coordonnées y.
     */
    public void addPixel(int x, int y) {
        this.liste_pixel.add(new Pixel(x, y));
    }

    /**
     * Retourne le nombre de pixels défectueux dans l'image (erreur).
     *
     * @return Nombre de defective pixel.
     */
    public int getNombreDefectivePixel() {
        return this.liste_pixel.size();
    }

    /**
     * Retourne un {@code Pixel} à l'index renseigné.
     *
     * @param index L'index.
     * @return Le {@code Pixel}.
     */
    public Pixel getPixel(int index) {
        return this.liste_pixel.get(index);
    }
}
