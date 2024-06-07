package com.example.zajil.util

import android.content.Context
import android.os.LocaleList
import android.view.View
import androidx.fragment.app.FragmentActivity
import java.util.Locale


object LocaleHelper {


    /*fun String.translate(){
        try {
            String translatedText = Translate.execute(test, Language.ENGLISH, Language.FRENCH);//You can pass params as per text input and desired output.
            System.out.println(translatedText)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    fun FragmentActivity.changeDirection(langauge: String) {
        when (langauge) {
            "en" -> window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
            else -> window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }

    }

    fun Context.setLocale(language: String): Context {
        persist(language)
        return updateResources(this, language)
    }

    private fun persist(language: String) {
        App.preferenceManager.SELECTED_LANGUAGE = language
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        LocaleList.setDefault(LocaleList(locale))
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        val newContext = context.createConfigurationContext(configuration)
        App.mRes = newContext.resources
        return newContext
    }

}
