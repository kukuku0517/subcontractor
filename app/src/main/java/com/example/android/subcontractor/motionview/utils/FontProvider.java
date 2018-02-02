package com.example.android.subcontractor.motionview.utils;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * extracting Typeface from Assets is a heavy operation,
 * we want to make sure that we cache the typefaces for reuse
 */
public class FontProvider {

    private static final String DEFAULT_FONT_NAME = "helvetica";

    private final Map<String, Typeface> typefaces;
    private final Map<String, String> fontNameToTypefaceFile;
    private final Resources resources;
    private final List<String> fontNames;

    public FontProvider(Resources resources) {
        this.resources = resources;

        typefaces = new HashMap<>();

        // populate fonts
        fontNameToTypefaceFile = new HashMap<>();
        fontNameToTypefaceFile.put("arial", "arial.ttf");
        fontNameToTypefaceFile.put("eutemia", "eutemia.ttf");
        fontNameToTypefaceFile.put("greenpil", "greenpil.ttf");
        fontNameToTypefaceFile.put("grinched", "grinched.ttf");
        fontNameToTypefaceFile.put("helvetica", "helvetica.ttf");
        fontNameToTypefaceFile.put("libertango", "libertango.ttf");
        fontNameToTypefaceFile.put("Metal Macabre", "metalmacabre.ttf");
        fontNameToTypefaceFile.put("Parry Hotter", "parrypotter.ttf");
        fontNameToTypefaceFile.put("scriptin", "scriptin.ttf");
        fontNameToTypefaceFile.put("The Godfather v2", "thegodfather_v2.ttf");
        fontNameToTypefaceFile.put("Aka Dora", "akadora.ttf");
        fontNameToTypefaceFile.put("Waltograph", "waltograph42.ttf");

        fontNames = new ArrayList<>(fontNameToTypefaceFile.keySet());
    }

    /**
     * @param typefaceName must be one of the font names provided from {@link FontProvider#getFontNames()}
     * @return the Typeface associated with {@code typefaceName}, or {@link Typeface#DEFAULT} otherwise
     */
    public Typeface getTypeface(@Nullable String typefaceName) {
        if (TextUtils.isEmpty(typefaceName)) {
            return Typeface.DEFAULT;
        } else {
            //noinspection Java8CollectionsApi
            if (typefaces.get(typefaceName) == null) {
                typefaces.put(typefaceName,
                        Typeface.createFromAsset(resources.getAssets(), "fonts/" + fontNameToTypefaceFile.get(typefaceName)));
            }
            return typefaces.get(typefaceName);
        }
    }

    /**
     * use {@link FontProvider#getTypeface(String) to get Typeface for the font name}
     *
     * @return list of available font names
     */
    public List<String> getFontNames() {
        return fontNames;
    }

    /**
     * @return Default Font Name - <b>helvetica</b>
     */
    public String getDefaultFontName() {
        return DEFAULT_FONT_NAME;
    }
}