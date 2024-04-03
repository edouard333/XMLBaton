package com.phenix.xmlbaton;

/**
 * Classe utilisée pour connaitre la position des pixels défectueux ou mort dans
 * l'image.
 *
 * @author <a href="mailto:edouard128@hotmail.com">Edouard Jeanjean</a>
 */
public class Pixel {

    /**
     * Coordonnée x.
     */
    private final int x;

    /**
     * Coordonnée y.
     */
    private final int y;

    /**
     * Construit un pixel sur base de ses coordonnées x, y.
     *
     * @param x Coordonnée x.
     * @param y Coordonnée y.
     */
    public Pixel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retourne la coordonnée x.
     *
     * @return Coordonnée x.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Retourne la coordonnée y.
     *
     * @return Coordonnée y.
     */
    public int getY() {
        return this.y;
    }
}
