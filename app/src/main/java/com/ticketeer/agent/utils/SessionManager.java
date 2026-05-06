package com.ticketeer.agent.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "TicketeerSession";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_AGENT_NOM = "agent_nom";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void sauvegarderSession(String token, String agentNom) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_AGENT_NOM, agentNom);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getAgentNom() {
        return prefs.getString(KEY_AGENT_NOM, null);
    }

    public boolean estConnecte() {
        return getToken() != null;
    }

    public void deconnexion() {
        editor.clear();
        editor.apply();
    }
}
