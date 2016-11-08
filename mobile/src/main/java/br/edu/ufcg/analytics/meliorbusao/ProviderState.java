package br.edu.ufcg.analytics.meliorbusao;

import java.util.Calendar;

public class ProviderState {
    private boolean state;
    private long time;

    public ProviderState(boolean state) {
        this.state = state;
        this.time = Calendar.getInstance().getTimeInMillis();
    }

    /**
     * @return timestamp da localização
     */
    public long getTime() {
        return time;
    }

    /**
     * @return se existe localização
     */
    public boolean getState() {
        return state;
    }
}
