package com.bochenchleba.guesstheanimal;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibraryFragment extends DialogFragment {

    private LibraryFragmentCallback callback;

    public boolean gameIsOver = false;


    public void setGameIsOver (boolean isOver){
        this.gameIsOver = isOver;
    }

    private void setRecycler(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        List<LibraryEntry> entries = new ArrayList<>();
        List<String> titlesList = Arrays.asList(getResources().getStringArray(R.array.animals));
        List<String> descsList = Arrays.asList(getResources().getStringArray(R.array.descs));

        for (int i=0; i < titlesList.size(); i++){

            int id = getResources().getIdentifier(
                    titlesList.get(i), "drawable", "com.bochenchleba.guesstheanimal");

            LibraryEntry entry = new LibraryEntry();

            entry.setName(titlesList.get(i));
            entry.setDesc(descsList.get(i));
            entry.setImage(id);

            entries.add(entry);
        }

        RecyclerAdapter.RecyclerItemClickListener recyclerClickListener = this::playAnimalSound;

        RecyclerAdapter adapter = new RecyclerAdapter(view.getContext(), entries, recyclerClickListener);

        recyclerView.setAdapter(adapter);
    }

    private void playAnimalSound(String animal){

        int soundResourceId = getResources().getIdentifier(animal, "raw",
                "com.bochenchleba.guesstheanimal");

        try{
            MediaPlayer animalSoundMp = MediaPlayer.create(getContext(), soundResourceId);
            animalSoundMp.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public LibraryFragment(){
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_library, container);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        callback = (MainActivity) getActivity();

        setRecycler(v);

        ImageButton backBtn = v.findViewById(R.id.dialog_library_return_ibtn);
        backBtn.setOnClickListener(v1 -> {
            callback.libraryFragmentDismissed(gameIsOver);
            dismiss();
        });

        return v;
    }


    @Override
    public void onResume(){
        super.onResume();
        getDialog().getWindow().setLayout(
                getResources().getDisplayMetrics().widthPixels,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        callback.libraryFragmentDismissed(gameIsOver);
    }



    public interface LibraryFragmentCallback{

        void libraryFragmentDismissed(boolean gameIsOver);
    }
}
