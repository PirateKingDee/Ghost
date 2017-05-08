/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;

import static android.view.KeyEvent.KEYCODE_A;
import static android.view.KeyEvent.KEYCODE_Z;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private String word = "";
    private Button challenge;
    private Button reset;
    private TextView gameStatus;
    private TextView fragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        fragment = (TextView) findViewById(R.id.ghostText);
        AssetManager assetManager = getAssets();
        gameStatus = (TextView) findViewById(R.id.gameStatus);

        challenge = (Button)findViewById(R.id.challege);
        reset = (Button)findViewById(R.id.reset);
        try {
            dictionary = new FastDictionary(assetManager.open("words.txt"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = dictionary.getGoodWordStartingWith(word);
                if(s == null){
                    gameStatus.setText("User Win");
                }
                else{
                    fragment.setText(s);
                    gameStatus.setText("Computer Win");
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                word = "";
                onStart(null);
            }
        });
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        TextView fragment = (TextView) findViewById(R.id.ghostText);
        if(word == ""){
            word += "a";

            fragment.setText(word);
            return;
        }

        String s = dictionary.getGoodWordStartingWith(word);
        if(s != null){
            label.setText(s);
            word += s.charAt(word.length());
            fragment.setText(word);
            return;
        }
        else{
            label.setText("Challege! You can't bluff this computer!");
        }

        // Do computer turn stuff then make it the user's turn again
        userTurn = true;
        //label.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode >= KEYCODE_A && keyCode <= KEYCODE_Z){
            Log.d("letter",String.valueOf(keyCode));
            word += Character.toString((char)event.getUnicodeChar());
            fragment.setText(word);
            gameStatus.setText(word);
            computerTurn();
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }
}
