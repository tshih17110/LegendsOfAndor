package com.example.LegendsOfAndor;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.ArrayList;
import java.util.List;

enum FightResponses {
    NO_CREATURE_FOUND, NOT_CURRENT_TURN, DAY_ENDED, JOINED_FIGHT
}

enum PassResponses {
    PASS_SUCCESSFUL, MUST_END_DAY, ONLY_PLAYER_LEFT, NOT_CURRENT_TURN, DAY_ENDED, PASS_SUCCESSFUL_WP_DEDUCTED
}

enum EndDayResponses {
    DAY_ALREADY_ENDED, NOT_CURRENT_TURN, NEW_DAY, GAME_OVER, SUCCESS
}

public class Board extends AppCompatActivity {
    public ImageView warrior;
    private Button move;
    private Button fight;
    private Button pass;
    private Button endDay;
    private Button chatb;
    private Button optionsb;

    boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        move = findViewById(R.id.move);
        fight = findViewById(R.id.fight);
        pass = findViewById(R.id.pass);
        endDay = findViewById(R.id.endDay);
        chatb= findViewById(R.id.chatb);
        optionsb = findViewById(R.id.optionsb);

        warrior = findViewById(R.id.warrior);
        warrior.setX(100);  //get the color, then determine the location
        warrior.setY(100);

        move.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setFlag();
            }
        });

        chatb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), ChatScreen.class);
                startActivity(myIntent);
            }
        });

        fight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    AsyncTask<String, Void, FightResponses> asyncTask;

                    FightSender messageSender = new FightSender();
                    asyncTask = messageSender.execute("");

                    if (asyncTask.get() == FightResponses.JOINED_FIGHT) {
                        Toast.makeText(Board.this, "Joining fight...", Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(v.getContext(), MonsterFight.class);
                        startActivity(myIntent);
                    } else if (asyncTask.get() == FightResponses.NO_CREATURE_FOUND) {
                        Toast.makeText(Board.this, "Fight error. No creature found.", Toast.LENGTH_LONG).show();
                    } else if (asyncTask.get() == FightResponses.DAY_ENDED) {
                        Toast.makeText(Board.this, "Fight error. Cannot fight after day ended.", Toast.LENGTH_LONG).show();
                    } else { // not current turn
                        Toast.makeText(Board.this, "Fight error. It is not your turn yet.", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AsyncTask<String, Void, PassResponses> asyncTask;

                    PassSender passSender = new PassSender();
                    asyncTask = passSender.execute("");

                    if (asyncTask.get() == PassResponses.PASS_SUCCESSFUL) {
                        Toast.makeText(Board.this, "Successfully passed turn.", Toast.LENGTH_LONG).show();
                    } else if (asyncTask.get() == PassResponses.DAY_ENDED) {
                        Toast.makeText(Board.this, "Pass error. Your day already ended.", Toast.LENGTH_LONG).show();
                    } else if (asyncTask.get() == PassResponses.MUST_END_DAY) {
                        Toast.makeText(Board.this, "Pass error. You must end your day now.", Toast.LENGTH_LONG).show();
                    } else if (asyncTask.get() == PassResponses.NOT_CURRENT_TURN){
                        Toast.makeText(Board.this, "Pass error. It is not your turn yet.", Toast.LENGTH_LONG).show();
                    } else if (asyncTask.get() == PassResponses.PASS_SUCCESSFUL_WP_DEDUCTED) {
                        Toast.makeText(Board.this, "Successfully passed turn. 2 Willpower deducted for overtime.", Toast.LENGTH_LONG).show();
                    } else { // ONLY_PLAYER_LEFT
                        Toast.makeText(Board.this, "Pass error. You are the only player left.", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        endDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AsyncTask<String, Void, EndDayResponses> asyncTask;

                    EndDaySender endDaySender = new EndDaySender();
                    asyncTask = endDaySender.execute("");

                    if (asyncTask.get() == EndDayResponses.DAY_ALREADY_ENDED) {
                        Toast.makeText(Board.this, "End day error. Your day already ended.", Toast.LENGTH_LONG).show();
                    } else if (asyncTask.get() == EndDayResponses.NEW_DAY) {
                        Toast.makeText(Board.this, "New day!", Toast.LENGTH_LONG).show();
                    } else if (asyncTask.get() == EndDayResponses.GAME_OVER) {
                        Toast.makeText(Board.this, "Game over!", Toast.LENGTH_LONG).show();
                    } else if (asyncTask.get() == EndDayResponses.NOT_CURRENT_TURN){
                        Toast.makeText(Board.this, "End day. It is not your turn yet.", Toast.LENGTH_LONG).show();
                    } else { // ONLY_PLAYER_LEFT
                        Toast.makeText(Board.this, "Success. You ended your day.", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });;

        optionsb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), OptionsTab.class);
                startActivity(myIntent);
            }
        });
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(flag){
            this.flag = false;
            Bitmap layout = BitmapFactory.decodeResource(getResources(),R.drawable.overlay);

            int newColor = layout.getPixel((int)(event.getX()),(int)(event.getY()));
            int newRegionNumber = Color.blue(newColor);
            //Region newRegion = RegionDatabase.getInstance().getRegionDatabase().get(newRegionNumber);

            int oldColor = layout.getPixel((int)(this.warrior.getX()),(int)(this.warrior.getY()));
            int oldRegionNumber = Color.blue(oldColor);
            //Region currentRegion = RegionDatabase.getInstance().getRegionDatabase().get(oldRegionNumber);

            //if(currentRegion.getAdjacentRegions().contains(newRegion))
            //{
                //this.warrior.setX(event.getX());
                //this.warrior.setY(event.getY());
                //return true;
            //}
            //else
                //{
                    //Toast.makeText(Board.this, "The clicked region is not a neighbor to your current region. Please choose your destination again.", Toast.LENGTH_LONG).show();
                    //return super.dispatchTouchEvent(event);
                //}
            //check if the region is a neighbor to the current region;
            //set the player's current region to new region;
            //add the player to the new region and remove the player from the previous region
        }
        return super.dispatchTouchEvent(event);
    }
    public void setFlag(){
        this.flag = true;
    }

    private static class FightSender extends AsyncTask<String, Void, FightResponses> {
        @Override
        protected FightResponses doInBackground(String... strings) {
            MyPlayer myPlayer = MyPlayer.getInstance();
            HttpResponse<String> response;

            try {
                response = Unirest.post("http://"+myPlayer.getServerIP()+":8080/"+myPlayer.getGame().getGameName() +"/"+ myPlayer.getPlayer().getUsername() + "/fight")
                        .asString();
                String resultAsJsonString = response.getBody();

                return new Gson().fromJson(resultAsJsonString, FightResponses.class);
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class PassSender extends AsyncTask<String, Void, PassResponses> {
        @Override
        protected PassResponses doInBackground(String... strings) {
            MyPlayer myPlayer = MyPlayer.getInstance();
            HttpResponse<String> response;

            try {
                response = Unirest.post("http://"+myPlayer.getServerIP()+":8080/"+myPlayer.getGame().getGameName() +"/"+ myPlayer.getPlayer().getUsername() + "/pass")
                        .asString();
                String resultAsJsonString = response.getBody();

                return new Gson().fromJson(resultAsJsonString, PassResponses.class);
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class EndDaySender extends AsyncTask<String, Void, EndDayResponses> {
        @Override
        protected EndDayResponses doInBackground(String... strings) {
            MyPlayer myPlayer = MyPlayer.getInstance();
            HttpResponse<String> response;

            try {
                response = Unirest.post("http://"+myPlayer.getServerIP()+":8080/"+myPlayer.getGame().getGameName() +"/"+ myPlayer.getPlayer().getUsername() + "/endDay")
                        .asString();
                String resultAsJsonString = response.getBody();

                return new Gson().fromJson(resultAsJsonString, EndDayResponses.class);
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}