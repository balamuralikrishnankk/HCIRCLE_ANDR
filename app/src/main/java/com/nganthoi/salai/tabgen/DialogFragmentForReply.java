package com.nganthoi.salai.tabgen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by atul on 5/5/16.
 */
public class DialogFragmentForReply extends DialogFragment {

    public DialogFragmentForReply() {
    }

    public static DialogFragmentForReply newInstance(String title) {
        DialogFragmentForReply frag = new DialogFragmentForReply();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dialog_for_reply,container,false);
        return view;
    }
}
