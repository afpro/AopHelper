package net.afpro.idea.aophelper;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;

public class Utils {

    private static final Logger log = Logger.getInstance(Utils.class);


    public static final String PREFIX = "butterknifezelezny_prefix";

    /**
     * Load field name prefix from code style
     *
     * @return
     */
    public static String getPrefix() {
        if (PropertiesComponent.getInstance().isValueSet(PREFIX)) {
            return PropertiesComponent.getInstance().getValue(PREFIX);
        } else {
            CodeStyleSettingsManager manager = CodeStyleSettingsManager.getInstance();
            CodeStyleSettings settings = manager.getCurrentSettings();
            return settings.FIELD_NAME_PREFIX;
        }
    }

    /**
     * Easier way to check if string is empty
     *
     * @param text
     * @return
     */
    public static boolean isEmptyString(String text) {
        return (text == null || text.trim().length() == 0);
    }


}
