/******************************************************************************/
/**
@file          Translator.java
@copyright     Mateusz Michalski
*
@author        Mateusz Michalski
*
@language      Java JDK 1.8
*
@Description:  Translator - Translates passed text and reads it in the specified
*                           language.
*******************************************************************************/
package JERVIS;

import TextBase.SensitiveData;
import com.ibm.watson.developer_cloud.language_translation.v2.LanguageTranslation;
import com.ibm.watson.developer_cloud.language_translation.v2.model.TranslationResult;

public class Translator {
    
    private final String sSrcLanguage;
    private final String sDestinLanguage;
    private final LanguageTranslation service;
    
    public Translator(String sSrcLanguage, String sDestinLanguage){
        
        this.sSrcLanguage = sSrcLanguage;
        this.sDestinLanguage = sDestinLanguage;
        
        service = new LanguageTranslation();
        service.setUsernameAndPassword(SensitiveData.WATSON_TRNSLT_LGN, 
                SensitiveData.WATSON_TRNSLT_PSWD
        );
    }
    
    public String translate(String text){
                
        TranslationResult translationResult = 
                service.translate(text, sSrcLanguage, sDestinLanguage);

        String result = translationResult.toString();
        
        String tmp[] = result.split(",");
        tmp = tmp[1].split(":");
        result = tmp[2].replace("\"", "");
        
        System.out.println(result);
        return result;
    }
}
