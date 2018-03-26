package uk.ac.reading.ps026478.playgroundwars;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Main Activity class which contains all functions that are associated with the UI
 * thread and beginning the game thread.
 *
 * @author Joe Fazzino
 * @version 1.0
 * @since 19/04/2017
 */
public class MainActivity extends Activity {

    // Game thread variables
    private GameThread mGameThread;
    private GameView mGameView;

    // Game view UI variables
    private TextView mScoreTextView;
    private TextView mStatusTextView;
    private TextView mPlayerNameTextView;
    private Button mEndTurnButton;

    // Title image
    private ImageView mTitleImageView;

    // Main menu UI variables
    private Button mStartGameButton;
    private Button mHighScoresButton;
    private TextView mHighScoresTextView;
    private Button mHowToPlayButton;
    private Button mShowMainMenuButton;

    // Game setup UI variables
    private LinearLayout mPlayerDetailsLayout;
    private TextView mPlayerDetailsTextView;
    private EditText mPlayerDetailsEditor;
    private Button mPlayerDetailsConfirmButton;

    // Tutorial UI variables
    private ImageView mHowToPlayTitleImageView;
    private TextView mHowToPlayTextView;
    private ImageView mRulesImageView;

    // Names of players variables
    private String player1Name, player2Name;

    // Reference to Firebase database
    private DatabaseReference mDatabase;

    // ArrayList for reading from Firebase database
    private ArrayList<User> users = new ArrayList<>();

    int playerCounter = 1;

    int darthCounter = 0;

    /**
     * onCreate function is the function first run by the app
     *
     * <p>It assigns all the UI features to Java objects so they can be
     * interacted with programatically. It also begins the game thread and the
     * listeners for all the buttons.</p>
     *
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Assign objects to respective UI elements and set them up for start up
        mGameView = (GameView)findViewById(R.id.gamearea);
        mScoreTextView = (TextView)findViewById(R.id.score);
        mStatusTextView = (TextView)findViewById(R.id.text);
        mPlayerNameTextView = (TextView)findViewById(R.id.player_name_label);
        mEndTurnButton = (Button)findViewById(R.id.end_turn_button);
        mGameView.setStatusView(mStatusTextView);
        mGameView.setScoreView(mScoreTextView);
        mGameView.setPlayerName(mPlayerNameTextView);
        mGameView.setEndTurnButton(mEndTurnButton);
        mGameView.setVisibility(View.INVISIBLE);
        mScoreTextView.setVisibility(View.INVISIBLE);
        mStatusTextView.setVisibility(View.INVISIBLE);
        mPlayerNameTextView.setVisibility(View.INVISIBLE);
        mEndTurnButton.setVisibility(View.INVISIBLE);

        // Assign rest of UI objects
        mHowToPlayTitleImageView = (ImageView)findViewById(R.id.how_to_play_title);
        mHowToPlayTextView = (TextView)findViewById(R.id.how_to_play_textview);
        mRulesImageView = (ImageView)findViewById(R.id.rules_image);
        mPlayerDetailsLayout = (LinearLayout)findViewById(R.id.player_details_layout);
        mPlayerDetailsTextView = (TextView)findViewById(R.id.player_number);
        mPlayerDetailsEditor = (EditText)findViewById(R.id.player_name);
        mPlayerDetailsConfirmButton = (Button)findViewById(R.id.player_details_confirm);

        mTitleImageView = (ImageView)findViewById(R.id.title);

        // Set up buttons for starting game
        mStartGameButton = (Button)findViewById(R.id.play_button);
        mStartGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleImageView.setVisibility(View.GONE);
                mStartGameButton.setVisibility(View.GONE);
                mHighScoresButton.setVisibility(View.GONE);
                mHowToPlayButton.setVisibility(View.GONE);


                mPlayerDetailsLayout.setVisibility(View.VISIBLE);
            }
        });

        // Set up buttons for confirming details
        mPlayerDetailsConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerCounter == 2) {
                    player2Name = mPlayerDetailsEditor.getText().toString();

                    mGameView.setPlayer2Name(player2Name);
                    mPlayerDetailsLayout.setVisibility(View.GONE);

                    mGameView.setVisibility(View.VISIBLE);
                    mScoreTextView.setVisibility(View.VISIBLE);
                    mStatusTextView.setVisibility(View.VISIBLE);
                    mPlayerNameTextView.setVisibility(View.VISIBLE);
                    mEndTurnButton.setVisibility(View.VISIBLE);
                } else {
                    playerCounter++;
                    player1Name = mPlayerDetailsEditor.getText().toString();

                    mPlayerDetailsTextView.setText("Enter Player 2 Name: ");
                    mGameView.setPlayer1Name(player1Name);
                    mPlayerDetailsEditor.setText("");
                }


            }
        });

        // Set up grabing high scores from Firebase, sorting them descending and then showing them
        mHighScoresTextView = (TextView)findViewById(R.id.high_score_textview);
        mHighScoresButton = (Button)findViewById(R.id.high_score_button);
        mHighScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.child("users").getChildren()) {
                            User user = child.getValue(User.class);
                            users.add(user);
                        }

                        Collections.sort(users, new Comparator<User>() {
                            @Override
                            public int compare(User o1, User o2) {
                                return o1.getScore() > o2.getScore() ? -1 : (o1.getScore() < o2.getScore() ? 1 : 0);
                            }
                        });

                        for (int i = 0; i < 3; i++) {
                            mHighScoresTextView.setText(mHighScoresTextView.getText() + "\n" + users.get(i).getUsername() + "\t\t" + users.get(i).getScore());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mStartGameButton.setVisibility(View.GONE);
                mHighScoresButton.setVisibility(View.GONE);
                mHowToPlayButton.setVisibility(View.GONE);
                mHighScoresTextView.setVisibility(View.VISIBLE);
                mShowMainMenuButton.setVisibility(View.VISIBLE);
            }
        });

        // Set up tutorial button
        mHowToPlayButton = (Button)findViewById(R.id.instructions_button);
        mHowToPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleImageView.setVisibility(View.GONE);
                mStartGameButton.setVisibility(View.GONE);
                mHighScoresButton.setVisibility(View.GONE);
                mHowToPlayButton.setVisibility(View.GONE);
                mHowToPlayTitleImageView.setVisibility(View.VISIBLE);
                mHowToPlayTextView.setVisibility(View.VISIBLE);
                mRulesImageView.setVisibility(View.VISIBLE);
                mShowMainMenuButton.setVisibility(View.VISIBLE);
            }
        });

        // Set up return to main menu button
        mShowMainMenuButton = (Button)findViewById(R.id.main_menu_return_button);
        mShowMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHowToPlayTitleImageView.setVisibility(View.GONE);
                mHighScoresTextView.setVisibility(View.GONE);
                mHowToPlayTextView.setVisibility(View.GONE);
                mRulesImageView.setVisibility(View.GONE);
                mGameView.setVisibility(View.GONE);
                mEndTurnButton.setVisibility(View.GONE);
                mPlayerNameTextView.setVisibility(View.GONE);
                mScoreTextView.setVisibility(View.GONE);
                mShowMainMenuButton.setVisibility(View.GONE);

                mTitleImageView.setVisibility(View.VISIBLE);
                mStartGameButton.setVisibility(View.VISIBLE);
                mHighScoresButton.setVisibility(View.VISIBLE);
                mHowToPlayButton.setVisibility(View.VISIBLE);
            }
        });

        // Set up easter egg
        mHowToPlayTitleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (darthCounter != 4) {
                    Toast.makeText(MainActivity.this, "Press " + (4 - darthCounter) + " more times to " +
                            "unlock Sith Lord mode", Toast.LENGTH_SHORT).show();
                    darthCounter++;
                } else {
                    mHowToPlayTextView.setText(R.string.darth_plagius);
                }
            }
        });


        this.startGame(mGameView, null, savedInstanceState);
    }


    /**
     * The startGame function is called from the onCreate function.
     *
     * <p>Creates new TheGame object with the game thread that is passed.</p>
     * @param gView Game view reference
     * @param gThread Game thread reference
     * @param savedInstanceState Save state instance
     */
    private void startGame(GameView gView, GameThread gThread, Bundle savedInstanceState) {

        //Set up a new game, we don't care about previous states
        mGameThread = new TheGame(mGameView, mEndTurnButton, mPlayerNameTextView);
        mGameView.setThread(mGameThread);
        mGameThread.setState(GameThread.STATE_RUNNING);
    }


    /**
     * Executed when focus is changed from the app such as multi tasking away
     */
    @Override
    protected void onPause() {
        super.onPause();

        if(mGameThread.getMode() == GameThread.STATE_RUNNING) {
            mGameThread.setState(GameThread.STATE_PAUSE);
        }
    }


    /**
     * Executed when the app is destroyed from memory
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mGameView.cleanup();
        mGameThread = null;
        mGameView = null;
    }
}