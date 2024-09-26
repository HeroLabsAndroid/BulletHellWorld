package com.example.bullethellworld;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Locale;

public class GameOverDialog extends DialogFragment {

    DialogDismissedListener dismissListen;
    int score;
    int hiscore;

    TextView tvGameOverTitle, tvScore;
    Button btnOK;

    Context con;

    public GameOverDialog(DialogDismissedListener dismissListen, int score, Context con) {
        this.dismissListen = dismissListen;
        this.score = score;
        this.con=con;
        SharedPreferences sharedPref = con.getSharedPreferences("BLLTHLLWRLD", Context.MODE_PRIVATE);
        hiscore = sharedPref.getInt("HISCORE", 0);

        if(hiscore < score) {
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putInt("HISCORE", score);
            edit.apply();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dlg_gameover, null);

        btnOK = layout.findViewById(R.id.BTN_gameover_ok);
        tvGameOverTitle = layout.findViewById(R.id.TV_gameover_title);
        tvScore = layout.findViewById(R.id.TV_gameover_score);

        btnOK.setOnClickListener(v -> {
            dismissListen.onDialogDismissed();
            dismiss();
        });

        tvScore.setText(String.format(Locale.getDefault(), "%s: %d", getResources().getString(score < hiscore ? R.string.MSG_score : R.string.MSG_hiscore), score/100));
        tvGameOverTitle.setText(getResources().getString(R.string.MSG_gameover));

        builder.setView(layout);
        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        dismissListen.onDialogDismissed();
        super.onDismiss(dialog);
    }
}
