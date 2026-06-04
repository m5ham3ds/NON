package com.example.service

import org.json.JSONObject
import timber.log.Timber

object InjectionManager {

    fun buildInjectionJs(
        card: String,
        usernameSel: String,
        passwordSel: String,
        submitSel: String,
        username: String
    ): String {
        val safeCard = JSONObject.quote(card).removeSurrounding("\"").replace("'", "\\'")
        val safeUsername = JSONObject.quote(username).removeSurrounding("\"").replace("'", "\\'")
        
        val js = """
        (function() {
            try {
                var u = document.querySelector('${usernameSel.replace("'", "\\'")}');
                var p = document.querySelector('${passwordSel.replace("'", "\\'")}');
                var s = document.querySelector('${submitSel.replace("'", "\\'")}');
                if (u && p && s) {
                    u.value = '$safeUsername';
                    p.value = '$safeCard';
                    s.click();
                    return 'injected';
                }
                return 'selectors_not_found';
            } catch(e) { return 'error:' + e.message; }
        })();
        """.trimIndent()
        Timber.v("Constructed injection JS: $js")
        return js
    }

    fun buildLogoutJs(logoutSel: String): String {
        return """
        (function() {
            try {
                var btn = document.querySelector('${logoutSel.replace("'", "\\'")}');
                if (btn) {
                    btn.click();
                    return 'clicked';
                }
                return 'not_found';
            } catch(e) { return 'error:' + e.message; }
        })();
        """.trimIndent()
    }
}
