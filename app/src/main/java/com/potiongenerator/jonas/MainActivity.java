package com.potiongenerator.jonas;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Loader load;
    private Random ran;
    private int seed;

    public MainActivity() {
        ran = new Random();
        seed = ran.nextInt(1000000);
        ran.setSeed(seed);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        load = new Loader(MainActivity.this);

        final Button generate = findViewById(R.id.Generate);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seed = ran.nextInt(1000000);
                ran.setSeed(seed);

                generatePotion();
            }
        });

        final Button load = findViewById(R.id.Load);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPotion();
            }
        });

        final Button get = findViewById(R.id.Get);
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPotion();
            }
        });
    }

    protected void generatePotion() {
        MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.sound);
        mp.start();

        String[] titles = load.load("res/Potions.txt", "Title");
        String[] effects = load.load("res/Potions.txt", "Effect");
        String[] strengths = load.load("res/Potions.txt", "Strength");
        String[] sideEffects = load.load("res/Potions.txt", "Side Effect");
        String[] containers = load.load("res/Potions.txt", "Container");
        String[] appearance = load.load("res/Potions.txt", "Appearance");
        String[] appearance2 = load.load("res/Potions.txt", "Appearance 2");
        String[] textures = load.load("res/Potions.txt", "Texture");
        String[] tastes = load.load("res/Potions.txt", "Taste");
        String[] labels = load.load("res/Potions.txt", "Label");

        String title = titles[ran.nextInt(titles.length)];
        String effect = effects[ran.nextInt(effects.length)];
        String strength = strengths[ran.nextInt(strengths.length)];
        String sideEffect = sideEffects[ran.nextInt(sideEffects.length)];
        String container = containers[ran.nextInt(containers.length)];
        String look = appearance[ran.nextInt(appearance.length)];
        String look2 = appearance2[ran.nextInt(appearance2.length)];
        String texture = textures[ran.nextInt(textures.length)];
        String smell = tastes[ran.nextInt(tastes.length)];
        String taste = tastes[ran.nextInt(tastes.length)];
        String label = labels[ran.nextInt(labels.length)];

        ((TextView) findViewById(R.id.Title)).setText("This is a " + title);
        ((TextView) findViewById(R.id.Effect)).setText("The main effect is " + effect);
        ((TextView) findViewById(R.id.Strength)).setText("The potions strength is " + strength);
        ((TextView) findViewById(R.id.SideEffect)).setText("The potion also causes (usually) tempoarily " + sideEffect);
        ((TextView) findViewById(R.id.Container)).setText("The container is a " + container);
        ((TextView) findViewById(R.id.Appearance)).setText("The liquid looks " + look);
        ((TextView) findViewById(R.id.Appearance2)).setText("With " + look2);
        ((TextView) findViewById(R.id.Texture)).setText("The texture is " + texture);
        ((TextView) findViewById(R.id.Smell)).setText("It smells like " + smell.substring(0, smell.length()-1) + ", but tastes like " + taste);
        ((TextView) findViewById(R.id.Label)).setText("It has a label " + label);
    }

    protected void loadPotion() {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.user_input, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        final EditText userInput = (EditText) view.findViewById(R.id.userinput);

        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setSeed(Integer.parseInt(userInput.getText().toString()));
                generatePotion();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    protected void setSeed(int seed) {
        ran.setSeed(seed);
        this.seed = seed;
    }

    protected void getPotion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.get_potion, null);
        TextView text = (TextView) view.findViewById(R.id.text);

        builder.setView(view)
                .setMessage("The seed is: ")
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        Dialog dialog = builder.create();
        dialog.show();

        text.setText(""+getSeed());
    }

    protected int getSeed() {
        return seed;
    }
}

class Loader {
    private Context context;
    public Loader(Context context) {
        this.context = context;
    }

    public String[] load(String file, String lables) {
        String[] result = null;

        AssetManager am = context.getAssets();

        try {
            InputStream is = am.open("Potions.txt");

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String curr;
            int dice = 0;
            String lable = "";
            while ((curr = reader.readLine()) != null) {
                if (curr.startsWith("d")) {
                    dice = Integer.parseInt(curr.substring(1, curr.indexOf(' ')));
                    lable = curr.substring(curr.indexOf(' ')+1, curr.indexOf('.'));
                    result = new String[dice];
                }

                if (!lable.equalsIgnoreCase(lables) || !curr.equalsIgnoreCase("<start>")) continue;

                int line = 0;
                while ((curr = reader.readLine()) != null && !curr.isEmpty()) {
                    curr = curr.substring(curr.indexOf(' ')+1, curr.length());

                    result[line] = curr.toLowerCase();
                    line++;
                }

                break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
