package com.shawnhu.seagull.fragments;

import android.net.Uri;

/**
 * Created by shawnhu on 7/27/14.
 */
/* TODO: fragment communication;
 * TODO: fragment class that has only one instance
 */
public interface OnFragmentInteractionListener {
     /**
     * This interface should be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
     public void onFragmentInteraction(Uri uri);

}
