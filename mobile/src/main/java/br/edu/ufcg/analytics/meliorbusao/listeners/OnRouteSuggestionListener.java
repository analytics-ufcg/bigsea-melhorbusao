package br.edu.ufcg.analytics.meliorbusao.listeners;

import br.edu.ufcg.analytics.meliorbusao.models.Route;

/**
 * Created by orion on 05/02/16.
 */
public interface OnRouteSuggestionListener {
    public boolean onRouteSuggestionClick(Route route);
}
