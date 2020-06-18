package ru.boriskezikov.beacon.test.provider;

import android.content.Intent;
import android.net.Uri;

import java.util.Random;

import ru.boriskezikov.beacon.test.model.Intents;


public class IntentProvider extends Intent {
    public static Intent getRandomIntent() {
        switch (new Random().nextInt(Intents.values().length)) {
            case 0:
                return getYoutubeIntent();
            case 1:
                return getMapIntent();
            default:
                return getYoutubeIntent();
        }
    }

    public static Intent getSpecificIntent(Intents intent){
        switch (intent) {
            case YOUTUBE:
                return getYoutubeIntent();
            case MAP:
                return getMapIntent();
            default:
                return getYoutubeIntent();
        }
    }

    static Intent getYoutubeIntent(){
        Intent youtubeIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
        );

        youtubeIntent.setPackage("com.google.android.youtube");

        return youtubeIntent;
    }

    static Intent getMapIntent(){
        Uri gmmIntentUri = Uri.parse("geo:53.2799,34.2068");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        return mapIntent;
    }
}
