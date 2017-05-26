package br.edu.ufcg.analytics.meliorbusao.models;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an expandable card with some informations about one bus route.
 *
 * @see RouteSummary
 */
public class RouteCard implements Parent<RouteSummary> {

    private RouteSummary routeSummary;

    public RouteCard(RouteSummary routeSummary) {
        this.routeSummary = routeSummary;
    }

    public RouteSummary getRouteSummary() {
        return routeSummary;
    }

    @Override
    public List<RouteSummary> getChildList() {
        List<RouteSummary> list = new ArrayList<>();
        list.add(routeSummary);
        return list;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    /**
     * Set the first element from a list to be the {@link #routeSummary}.
     *
     * @param list A list that should contains only one {@link RouteSummary}. If the list passed
     *             contains more than one {@link RouteSummary}, the first one will be set as the
     *             {@link #routeSummary}.
     */
    public void setChildObjectList(ArrayList<RouteSummary> list) {
        if (list != null && !list.isEmpty()) {
            routeSummary = list.get(0);
        }
    }
}
