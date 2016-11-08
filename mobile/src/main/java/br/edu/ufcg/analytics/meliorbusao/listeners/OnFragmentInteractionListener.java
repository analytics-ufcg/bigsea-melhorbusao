package br.edu.ufcg.analytics.meliorbusao.listeners;


import android.support.v4.widget.CursorAdapter;

public interface OnFragmentInteractionListener {

     void setTitle(String title);
     void setButtonVisibility(int id, boolean visibility);
     void setSearchHint(String hint);
     void setSearchSuggestionAdapter(CursorAdapter adapter);

}
