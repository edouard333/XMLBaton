package com.phenix.xmlbaton;

/**
 * Contient les informations sur une erreur Baton.
 *
 * @author <a href="mailto:edouard128@hotmail.com">Edouard Jeanjean</a>
 */
public class ErreurBaton {

    /**
     * Description de l'erreur Baton.
     */
    private final String description;

    /**
     * Durée de l'erreur Baton.
     */
    private final int duree;

    /**
     * Timecode début.
     */
    private final String tcStart;

    /**
     * Timecode de fin.
     */
    private final String tcEnd;

    /**
     * Type d'erreur.
     */
    private final String item;

    /**
     * ID pour le codec "DPX".
     */
    public static final int CODEC_DPX_HD = 0;

    /**
     * ID pour le codec "Apple Pro Res 4444 XQ".
     */
    public static final int CODEC_PR4444XQ = 1;

    /**
     * ID pour le codec/résolution "Apple Pro Res 4444" UHD.
     */
    public static final int CODEC_PR4444_UHD = 2;

    /**
     * ID pour le codec "Apple Pro Res 4444".
     */
    public static final int CODEC_PR4444 = 3;

    /**
     * ID pour le codec "Apple Pro Res 422 HQ".
     */
    public static final int CODEC_PR422HQ = 4;

    /**
     * ID pour le codec "Apple Pro Res Proxy".
     */
    public static final int CODEC_PR422PROXY = 5;

    /**
     * Liste des codecs possibles pour les erreurs.
     */
    private final boolean[] codec = new boolean[6];

    /**
     * Construit une {@code ErreurBaton}.
     *
     * @param description Description.
     * @param duree Durée.
     * @param tcStart Timecode début.
     * @param tcEnd Timecode de fin.
     * @param item Type d'erreur.
     * @param id_codec ID du codec de la vidéo.
     */
    public ErreurBaton(String description, int duree, String tcStart, String tcEnd, String item, int id_codec) {
        this.description = description;
        this.duree = duree;
        this.tcStart = tcStart;
        this.tcEnd = tcEnd;
        this.item = item;

        for (int i = 0; i < this.codec.length; i++) {
            this.codec[i] = false;
        }

        this.codec[id_codec] = true;
    }

    /**
     * Ajoute le codec d'une autre erreur.
     *
     * @param erreur L'erreur dont on doit récupérer le codec.
     */
    public void addCodec(ErreurBaton erreur) {
        this.codec[CODEC_DPX_HD] = (erreur.getCodecB(CODEC_DPX_HD)) ? true : this.codec[CODEC_DPX_HD];
        this.codec[CODEC_PR4444XQ] = (erreur.getCodecB(CODEC_PR4444XQ)) ? true : this.codec[CODEC_PR4444XQ];
        this.codec[CODEC_PR4444_UHD] = (erreur.getCodecB(CODEC_PR4444_UHD)) ? true : this.codec[CODEC_PR4444_UHD];
        this.codec[CODEC_PR4444] = (erreur.getCodecB(CODEC_PR4444)) ? true : this.codec[CODEC_PR4444];
        this.codec[CODEC_PR422HQ] = (erreur.getCodecB(CODEC_PR422HQ)) ? true : this.codec[CODEC_PR422HQ];
        this.codec[CODEC_PR422PROXY] = (erreur.getCodecB(CODEC_PR422PROXY)) ? true : this.codec[CODEC_PR422PROXY];
    }

    /**
     * Compare deux erreurs entre elle.
     *
     * @param erreur L'erreur à comparer avec celle actuelle.
     *
     * @return Retourne {@code true} si les deux erreurs sont identiques (voir
     * la méthode {@link ErreurBaton#toString() toString()} définissant les
     * critères).
     */
    public boolean compare(ErreurBaton erreur) {
        return toString().equals(erreur.toString());
    }

    /**
     * Retourne si le codec est présent dans l'erreur avec mise en page.
     *
     * @param id_codec ID du codec vidéo.
     *
     * @return Le codec vidéo.
     */
    public String getCodec(int id_codec) {
        return (this.codec[id_codec]) ? "<span style= 'color: green'><strong>X</strong></span>" : "";
    }

    /**
     * Retourne un {@code boolean} disant si le codec est présent dans l'erreur
     * ou non.
     *
     * @param id_codec ID du codec dans la liste des codecs.
     *
     * @return {@code true} si le codec est dans l'erreur.
     */
    public boolean getCodecB(int id_codec) {
        return this.codec[id_codec];
    }

    /**
     * Retourne la description de l'erreur (selon Baton).
     *
     * @return La description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Retourne la durée en nombre d'images de l'erreur.
     *
     * @return Durée.
     */
    public int getDuree() {
        return this.duree;
    }

    /**
     * Retourne le type d'erreur (selon Baton).
     *
     * @return Type d'erreur.
     */
    public String getItem() {
        return this.item;
    }

    /**
     * Retourne le timecode de fin en SMPTE de l'erreur.
     *
     * @return Timecode de fin.
     */
    public String getTcEnd() {
        return this.tcEnd;
    }

    /**
     * Retourne le timecode de début en SMPTE de l'erreur.
     *
     * @return Timecode début.
     */
    public String getTcStart() {
        return this.tcStart;
    }

    /**
     * Représentation de l'Erreur Baton.
     *
     * @return Représentation de l'objet {@code ErreurBaton} en {@code String}.
     */
    @Override
    public String toString() {
        return this.description + " " + this.duree + " " + this.tcStart + " " + this.item;
    }
}
