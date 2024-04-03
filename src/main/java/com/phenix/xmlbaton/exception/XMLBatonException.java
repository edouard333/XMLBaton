package com.phenix.xmlbaton.exception;

/**
 * Erreur pour les XMLBaton.
 *
 * @author <a href="mailto:edouard128@hotmail.com">Edouard Jeanjean</a>
 */
public class XMLBatonException extends Exception {

    /**
     * Erreur XML Baton.
     */
    public XMLBatonException() {
    }

    /**
     * Erreur avec un message.
     *
     * @param message Le message.
     */
    public XMLBatonException(String message) {
        super(message);
    }
}
