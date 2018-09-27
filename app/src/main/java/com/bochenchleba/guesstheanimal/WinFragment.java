package com.bochenchleba.guesstheanimal;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class WinFragment extends DialogFragment {

    WinFragmentCallback callback;

    private static final String BUNDLE_KEY_TIME = "time";
    private static final String BUNDLE_KEY_CORRECT = "correct";
    private static final String BUNDLE_KEY_ROUNDS = "rounds";

    private boolean libraryClicked = false;

    public WinFragment(){
    }

    public static WinFragment newInstance(String time, int correctAnswers, int roundsPlayed) {

        WinFragment frag = new WinFragment();

        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_TIME, time);
        args.putInt(BUNDLE_KEY_CORRECT, correctAnswers);
        args.putInt(BUNDLE_KEY_ROUNDS, roundsPlayed);

        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        callback = (MainActivity) getActivity();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_win, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String time = getArguments().getString(BUNDLE_KEY_TIME, "00:00");
        int correct = getArguments().getInt(BUNDLE_KEY_CORRECT, 1);
        int roundsPlayed = getArguments().getInt(BUNDLE_KEY_ROUNDS, 5);


        TextView titleTv = view.findViewById(R.id.tv_dialog_line1);

        double winRatio = (double) correct/roundsPlayed;
        String toDisplayTitle = "";

        if (winRatio < 0.5)
            toDisplayTitle = getString(R.string.win_dialog_title_poor);

        else if (winRatio > 0.75)
            toDisplayTitle = getString(R.string.win_dialog_title_good);
        else
            toDisplayTitle = getString(R.string.win_dialog_title_normal);

        titleTv.setText(toDisplayTitle);


        TextView textTv = view.findViewById(R.id.tv_dialog_line2);

        String toDisplayText = getResources().getString(
                R.string.win_dialog_text, correct, roundsPlayed, time);
        textTv.setText(toDisplayText);


        Button againBtn = view.findViewById(R.id.btn_again);
        Button libraryBtn = view.findViewById(R.id.btn_library);

        againBtn.setOnClickListener(v -> v.animate()
                .setDuration(50)
                .withStartAction(() ->
                        v.setBackgroundColor(getResources().getColor(R.color.colorCorrectly)))
                .withEndAction(() -> {
                    v.setBackgroundColor(getResources().getColor(R.color.colorDialogBtn));

                    callback.winFragmentDismissed(libraryClicked);

                    dismiss();
                })
                .start());

        libraryBtn.setOnClickListener(v -> {

            libraryClicked = true;

            callback.winFragmentLibraryClicked();

            dismiss();
        });

    }


    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        callback.winFragmentDismissed(false);
    }


    public interface WinFragmentCallback {

        void winFragmentDismissed(boolean libraryClicked);
        void winFragmentLibraryClicked();
    }
}
