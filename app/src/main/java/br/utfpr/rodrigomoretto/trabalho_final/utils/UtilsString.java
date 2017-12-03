package br.utfpr.rodrigomoretto.trabalho_final.utils;

/**
 * Created by STHGM on 03-Dec-17.
 */

public class UtilsString {

    public static boolean stringVazia(String texto){

        if (texto == null || texto.trim().length() == 0){
            return true;
        }else{
            return false;
        }
    }
}
