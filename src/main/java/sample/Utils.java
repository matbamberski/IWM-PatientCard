package sample;

public class Utils {
    static String CutLastNumbers (String text){
        int lastAlphaCharacter = 0;
        for(int i = 0; i < text.length(); i++){
            if(Character.isLetter(text.charAt(i))){
                lastAlphaCharacter = i;
            }
        }
        return text.substring(0, lastAlphaCharacter + 1);
    }
}
