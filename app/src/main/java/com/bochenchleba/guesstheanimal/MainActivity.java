package com.bochenchleba.guesstheanimal;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


 public class MainActivity extends AppCompatActivity
         implements WinFragment.WinFragmentCallback, LibraryFragment.LibraryFragmentCallback,
                    GameFragment.GameFragmentCallback{

    FragmentManager fm;

    GameFragment gameFragment;
    LibraryFragment libraryFragment;

    Boolean gameFragmentExists = false;
    Boolean libraryFragmentExists = false;

    private static final String GAME_FRAGMENT_TAG = "gameFragment";
    private static final String LIBRARY_FRAGMENT_TAG = "libraryFragment";
    private static final String WIN_FRAGMENT_TAG = "winDialog";

    private static final String SAVED_BUNDLE_GAME_FRAGMENT_EXISTS = "fragmentExists";
    private static final String SAVED_BUNDLE_LIBRARY_FRAGMENT_EXISTS = "libraryExists";

    private void showLibrary(boolean gameIsOver){

        if (libraryFragment != null){

            libraryFragment.show(fm, LIBRARY_FRAGMENT_TAG);
            libraryFragment.setGameIsOver(gameIsOver);

            MediaPlayer libraryMp = MediaPlayer.create(this, R.raw.encyclo);
            libraryMp.start();

            if (gameFragment != null){
                gameFragment.pauseTimer();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fm = getSupportFragmentManager();

        if (savedInstanceState != null){

            gameFragmentExists = savedInstanceState
                    .getBoolean(SAVED_BUNDLE_GAME_FRAGMENT_EXISTS, false);
            libraryFragmentExists = savedInstanceState
                    .getBoolean(SAVED_BUNDLE_LIBRARY_FRAGMENT_EXISTS, false);
        }



        if (gameFragmentExists){
            gameFragment = (GameFragment) fm.findFragmentByTag(GAME_FRAGMENT_TAG);
        }

        else {

            gameFragment = new GameFragment();
            fm.beginTransaction()
                    .replace(R.id.main_frame_layout, gameFragment, GAME_FRAGMENT_TAG)
                    .commit();
            gameFragmentExists = true;
        }

            libraryFragment = new LibraryFragment();
            libraryFragmentExists = true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_library) {

            showLibrary(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(SAVED_BUNDLE_GAME_FRAGMENT_EXISTS, gameFragmentExists);
        savedInstanceState.putBoolean(SAVED_BUNDLE_LIBRARY_FRAGMENT_EXISTS, libraryFragmentExists);
    }


    @Override
    public void winFragmentDismissed(boolean libraryClicked) {

        if (gameFragment != null && !libraryClicked){
            gameFragment.newGame();
        }
    }

    @Override
    public void winFragmentLibraryClicked() {

        showLibrary(true);
    }

     @Override
     public void libraryFragmentDismissed(boolean gameIsOver) {

        if (gameFragment != null){

            if (gameIsOver)
                gameFragment.newGame();
            else
                gameFragment.startTimer();
        }
     }

     @Override
     public void gameWon(String time, int correct, int round) {

        WinFragment winFragment = (WinFragment) fm.findFragmentByTag(WIN_FRAGMENT_TAG);

        if (winFragment == null){
             winFragment = WinFragment.newInstance(time,correct, round);
             winFragment.show(fm, WIN_FRAGMENT_TAG);
        }
     }
 }
