package com.bochenchleba.guesstheanimal;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;


public class GameFragment extends Fragment{

    GameFragmentCallback callback;

    private MediaPlayer correctSoundMp, wrongSoundMp, animalSoundMp;
    private Random random = new Random();

    private ArrayList<String> animalsList;
    private ArrayList<String> remainingAnimalsList;
    private ArrayList<String> randomAnimals = new ArrayList<>();
    private String correctAnimalName;
    private int correctAnswerPos;
    private int remainingAnimalsCount;
    private int numberOfBtns;
    private int minutesTimer = 0;
    private int secondsTimer = 0;
    private int correctCounter = 0;
    private int prevAnimal = -1;
    private int round=0;
    private boolean userWasWrong = false;
    private boolean orientationIsVertical;
    private boolean timerIsRunning = false;
    private boolean gameIsOver = false;

    private View mainView;
    private ConstraintLayout mainLayout, answerLayout;
    private Button playSoundBtn, startBtn;
    private TextView timeTv, infoTv, counterTv;

    private static final int NUMBER_OF_QUESTIONS = 5;

    private static final String SAVED_BUNDLE_MINUTES_TIMER = "minutes";
    private static final String SAVED_BUNDLE_SECONDS_TIMER = "seconds";
    private static final String SAVED_BUNDLE_CORRECT_COUNTER = "correct";
    private static final String SAVED_BUNDLE_ROUND = "round";
    private static final String SAVED_BUNDLE_REMAINING_LIST = "remaining";

    private static final int ANIMATION_CORRECT_BTN_ROTATION_TIME = 300;
    private static final int ANIMATION_CORRECT_BTN_ROTATION_FACTOR = 1800;
    private static final int ANIMATION_LAYOUT_FADE_TIME = 300;
    private static final float ANIMATION_LAYOUT_FADEOUT_ALPHA = 0f;
    private static final float ANIMATION_LAYOUT_FADEIN_ALPHA = 1f;
    private static final int ANIMATION_WRONG_BTN_TIME = 300;
    private static final int ANIMATION_WRONG_BTN_FACTOR = 1200;  //700

    final Handler timeHandler = new Handler();
    Runnable run = new Runnable() {
        @Override
        public void run() {
            timerIsRunning = true;

            if (secondsTimer == 60){

                minutesTimer++;
                secondsTimer = 0;
            }

            else {
                secondsTimer++;
            }

            String formattedTime =
                    String.format(Locale.getDefault(), "%d", minutesTimer) + ":" +
                            String.format(Locale.getDefault(), "%02d", secondsTimer);

            timeTv.setText(formattedTime);

            timeHandler.postDelayed(this, 1000);
        }
    };

    public void newGame(){

        minutesTimer = 0;
        secondsTimer = 0;
        correctCounter = 0;
        round = 0;
        remainingAnimalsList =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.animals)));
        remainingAnimalsCount = remainingAnimalsList.size();

        gameIsOver = false;

        prepare();
        startTimer();
    }

    public void pauseTimer(){
        timeHandler.removeCallbacks(run);
        timerIsRunning = false;
    }

    public void startTimer(){
        if (!timerIsRunning && !gameIsOver)
            timeHandler.post(run);
    }

    private void prepare(){

        playSoundBtn.setOnClickListener(
                v -> animalSoundMp.start());

        numberOfBtns = answerLayout.getChildCount();

        for (int i=0; i<numberOfBtns; i++){

            View btn = answerLayout.getChildAt(i);

            btn.setTag(R.id.TAG_POSITION, i);
            btn.setOnClickListener(v -> {

                if ( (int) v.getTag(R.id.TAG_POSITION) ==  correctAnswerPos ){
                    correctAnswer(v);
                }
                else {
                    wrongAnswer(v);
                }
            });
        }

        if (orientationIsVertical)
            infoTv.setText(getString(R.string.game_fragment_inf0_tv_normal));
        else
            infoTv.setText(getString(R.string.game_fragment_inf0_tv_normal_landscape));

        String ttd = "";

        if (round < NUMBER_OF_QUESTIONS)
            ttd = String.valueOf(round+1) +"/"+ String.valueOf(NUMBER_OF_QUESTIONS);
        else
            ttd = String.valueOf(NUMBER_OF_QUESTIONS) +"/"+ String.valueOf(NUMBER_OF_QUESTIONS);

        counterTv.setText(ttd);

        answerLayout.animate()
                .setDuration(ANIMATION_LAYOUT_FADE_TIME)
                .alpha(ANIMATION_LAYOUT_FADEOUT_ALPHA)
                .withEndAction(() -> {
                    nextQuestion();

                    answerLayout.animate()
                            .setDuration(ANIMATION_LAYOUT_FADE_TIME)
                            .alpha(ANIMATION_LAYOUT_FADEIN_ALPHA)
                            .withEndAction(() -> {
                                for (int i = 0; i< answerLayout.getChildCount(); i++)
                                    answerLayout.getChildAt(i).setClickable(true);
                            })
                            .start();
                }).start();
    }

    private void nextQuestion(){

        if (round < NUMBER_OF_QUESTIONS){

            userWasWrong=false;
            randomAnimals.clear();
            int correctAnimal;

            //Get correct animal
            do{
                correctAnimal = random.nextInt(remainingAnimalsCount);
            }
            while (correctAnimal == prevAnimal);

            correctAnimalName = remainingAnimalsList.get(correctAnimal);

            prevAnimal = correctAnimal;

            remainingAnimalsList.remove(correctAnimal);
            remainingAnimalsCount--;

            //Get rest of animals
            for (int i=0; i< numberOfBtns - 1; i++) {
                int animal; String animalName;

                do{

                    animal = random.nextInt(animalsList.size());
                    animalName = animalsList.get(animal);
                }
                while ( animalName.equals(correctAnimalName) || randomAnimals.contains(animalName) );

                randomAnimals.add(animalName);
            }

            //Set Btns
            correctAnswerPos = random.nextInt(numberOfBtns);

            for (int i=0, j=0; i<numberOfBtns; i++) {

                View btn = answerLayout.getChildAt(i);

                btn.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
                btn.setTranslationY(0);
                btn.setTranslationY(0);
                btn.setTranslationX(0);
                btn.setTranslationX(0);

                if (i == correctAnswerPos){
                    setButtonBackground(i, correctAnimalName);
                }
                else{
                    String name = randomAnimals.get(j++);

                    btn.setTag(R.id.TAG_ANIMAL, name);
                    setButtonBackground(i, name);
                }
            }

            playAnimalSound();
        }

        else {

            timeHandler.removeCallbacks(run);
            timerIsRunning = false;

            answerLayout.setClickable(false);

            callback.gameWon(timeTv.getText().toString(), correctCounter, round);
            gameIsOver = true;
        }

    }

    private void correctAnswer(final View v){

        round++;

        for (int i = 0; i< answerLayout.getChildCount(); i++){
            answerLayout.getChildAt(i).setClickable(false);
        }

        correctSoundMp.start();

        v.setBackgroundColor(getResources().getColor(R.color.colorCorrectly));

        String textToDisplay = "";
        if (orientationIsVertical)
            textToDisplay = getString(R.string.game_fragment_info_tv_correct) + correctAnimalName;
        else
            textToDisplay = getString(R.string.game_fragment_info_tv_correct) + "\n" + correctAnimalName;

        infoTv.setText(textToDisplay);
        infoTv.setTextColor(getResources().getColor(R.color.colorCorrectly));

        if (!userWasWrong)
            correctCounter++;

        v.animate()
                .setDuration(ANIMATION_CORRECT_BTN_ROTATION_TIME)
                .rotationBy(ANIMATION_CORRECT_BTN_ROTATION_FACTOR)
                .withEndAction(() -> answerLayout.animate()

                        .setDuration(ANIMATION_LAYOUT_FADE_TIME)
                        .alpha(ANIMATION_LAYOUT_FADEOUT_ALPHA)
                        .withEndAction(() -> {

                            nextQuestion();

                            answerLayout.animate()
                                    .setDuration(ANIMATION_LAYOUT_FADE_TIME)
                                    .alpha(ANIMATION_LAYOUT_FADEIN_ALPHA)
                                    .withEndAction(() -> {

                                        infoTv.setTextColor(getResources()
                                                .getColor(R.color.colorPrimaryText));

                                        if (orientationIsVertical)
                                            infoTv.setText(getString(
                                                    R.string.game_fragment_inf0_tv_normal));
                                        else
                                            infoTv.setText(getString(
                                                    R.string.game_fragment_inf0_tv_normal_landscape));

                                        for (int i = 0; i< answerLayout.getChildCount(); i++){
                                            answerLayout.getChildAt(i).setClickable(true);
                                        }

                                        if (round < NUMBER_OF_QUESTIONS){

                                            String textToDisplay1 = (round+1) + "/"+
                                                    String.valueOf(NUMBER_OF_QUESTIONS);
                                            counterTv.setText(textToDisplay1);
                                        }
                                    }).start();}).start()).start();

    }

    private void wrongAnswer(View v){

        v.setClickable(false);

        wrongSoundMp.start();

        v.setBackgroundColor(getResources().getColor(R.color.colorWrong));

        userWasWrong=true;

        String clickedAnimal = String.valueOf(v.getTag(R.id.TAG_ANIMAL));

        String textToDisplay = "";
        if (orientationIsVertical)
            textToDisplay = getString(R.string.game_fragment_info_tv_wrong) + clickedAnimal;
        else
            textToDisplay = getString(R.string.game_fragment_info_tv_wrong_landscape) + "\n" + clickedAnimal;

        infoTv.setText(textToDisplay);
        infoTv.setTextColor(getResources().getColor(R.color.colorWrongText));

        if ((int) v.getTag(R.id.TAG_POSITION) % 2 == 0){

            if (orientationIsVertical){
                v.animate()
                        .setDuration(ANIMATION_WRONG_BTN_TIME)
                        .translationXBy(-1 * ANIMATION_WRONG_BTN_FACTOR)
                        .start();
            }
            else {
                v.animate()
                        .setDuration(ANIMATION_WRONG_BTN_TIME)
                        .translationYBy(-1 * ANIMATION_WRONG_BTN_FACTOR)
                        .start();
            }
        }

        else {

            if (orientationIsVertical){
                v.animate()
                        .setDuration(ANIMATION_WRONG_BTN_TIME)
                        .translationXBy(ANIMATION_WRONG_BTN_FACTOR)
                        .start();
            }
            else {
                v.animate()
                        .setDuration(ANIMATION_WRONG_BTN_TIME)
                        .translationYBy(ANIMATION_WRONG_BTN_FACTOR)
                        .start();
            }
        }
    }

    private void setButtonBackground (int pos, String animal){

        int imageResourceId = getResources().getIdentifier(animal, "drawable",
                "com.bochenchleba.guesstheanimal");

        ImageButton btn = (ImageButton) answerLayout.getChildAt(pos);
        btn.setImageDrawable(getResources().getDrawable(imageResourceId));
    }

    private void playAnimalSound(){

        int soundResourceId = getResources().getIdentifier(correctAnimalName, "raw",
                "com.bochenchleba.guesstheanimal");

        try{
            animalSoundMp = MediaPlayer.create(getContext(), soundResourceId);
            animalSoundMp.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createMediaPlayers(){

        correctSoundMp = MediaPlayer.create(getContext(), R.raw.succesful);
        wrongSoundMp = MediaPlayer.create(getContext(), R.raw.wrong);
    }

    private void findViews(){

        mainLayout = mainView.findViewById(R.id.layout_main);
        answerLayout = mainView.findViewById(R.id.btnsLayout);
        playSoundBtn = mainView.findViewById(R.id.button_play_sound);
        startBtn = mainView.findViewById(R.id.btn_start);
        timeTv = mainView.findViewById(R.id.tv_time);
        infoTv = mainView.findViewById(R.id.tv_hint);
        counterTv = mainView.findViewById(R.id.tv_counter);
    }

    private void init(){

        orientationIsVertical =
                getActivity().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;

        callback = (MainActivity) getActivity();

        findViews();

        createMediaPlayers();

        animalsList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.animals)));

        startBtn.setOnClickListener(
                v -> {

                    minutesTimer = 0;
                    secondsTimer = 0;

                    prepare();

                    v.setClickable(false);
                    v.animate()
                            .setDuration(ANIMATION_LAYOUT_FADE_TIME)
                            .alpha(ANIMATION_LAYOUT_FADEOUT_ALPHA)
                            .withEndAction(() -> {
                                        mainLayout.animate()
                                                .setDuration(ANIMATION_LAYOUT_FADE_TIME)
                                                .alpha(ANIMATION_LAYOUT_FADEIN_ALPHA)
                                                .start();

                                        mainLayout.setClickable(true);
                                    }
                            ).start();
                });
    }

    private void recoverSavedData(Bundle save){

        if (save == null){
            remainingAnimalsList = animalsList;
        }

        else {

            minutesTimer = save.getInt(SAVED_BUNDLE_MINUTES_TIMER);
            secondsTimer = save.getInt(SAVED_BUNDLE_SECONDS_TIMER);
            correctCounter = save.getInt(SAVED_BUNDLE_CORRECT_COUNTER);
            round = save.getInt(SAVED_BUNDLE_ROUND);

            remainingAnimalsList = save.getStringArrayList(SAVED_BUNDLE_REMAINING_LIST);
            if (remainingAnimalsList == null)
                remainingAnimalsList = animalsList;

            if (round > 0){

                startBtn.setVisibility(View.INVISIBLE);
                mainLayout.setAlpha(1f);

                prepare();
            }


        }

        remainingAnimalsCount = remainingAnimalsList.size();
    }

    public GameFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_main, container, false);

        init();

        recoverSavedData(savedInstanceState);

        return mainView;
    }


    @Override
    public void onPause(){
        super.onPause();

        pauseTimer();
    }

    public void onResume(){
        super.onResume();

        startTimer();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt(SAVED_BUNDLE_MINUTES_TIMER, minutesTimer);
        savedInstanceState.putInt(SAVED_BUNDLE_SECONDS_TIMER, secondsTimer);
        savedInstanceState.putInt(SAVED_BUNDLE_CORRECT_COUNTER, correctCounter);
        savedInstanceState.putInt(SAVED_BUNDLE_ROUND, round);
        savedInstanceState.putStringArrayList(SAVED_BUNDLE_REMAINING_LIST, null);
    }


    public interface GameFragmentCallback{

        void gameWon(String time, int correct, int round);
    }

}
