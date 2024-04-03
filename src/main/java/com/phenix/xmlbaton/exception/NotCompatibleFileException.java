package com.phenix.xmlbaton.exception;

/**
 * Erreur indiquant que le fichier donné à "XMLBaton" n'est pas compatible.
 *
 * @author <a href="mailto:edouard128@hotmail.com">Edouard Jeanjean</a>
 */
public class NotCompatibleFileException extends XMLBatonException {

    /**
     * Erreur compatibilité.
     *
     * @param fichier Le fichier.
     */
    public NotCompatibleFileException(String fichier) {
        super("Le fichier '" + fichier + "' n'est pas compatible XML Baton.");
    }
}
