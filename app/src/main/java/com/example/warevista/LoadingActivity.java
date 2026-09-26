package com.example.warevista;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;


@OptIn(markerClass = UnstableApi.class)
public class LoadingActivity extends AppCompatActivity {


    // ============================================================
    // VIDEO PLAYER
    // ============================================================

    private PlayerView playerView;

    private ExoPlayer player;


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        // ========================================================
        // DISABLE BACK BUTTON
        // ========================================================

        getOnBackPressedDispatcher().addCallback(

                this,

                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        // Back disabled

                    }

                }

        );


        // ========================================================
        // SET UI
        // ========================================================

        setContentView(
                R.layout.activity_loading
        );


        // ========================================================
        // HIDE SYSTEM BARS
        // ========================================================

        hideSystemBars();


        // ========================================================
        // FIND PLAYER VIEW
        // ========================================================

        playerView =
                findViewById(
                        R.id.playerView
                );


        // ========================================================
        // HIDE VIDEO CONTROLS
        // ========================================================

        playerView.setUseController(
                false
        );


        // ========================================================
        // FILL SCREEN
        // ========================================================

        playerView.setResizeMode(

                AspectRatioFrameLayout.RESIZE_MODE_ZOOM

        );


        // ========================================================
        // CREATE PLAYER
        // ========================================================

        player =

                new ExoPlayer.Builder(
                        this
                ).build();


        playerView.setPlayer(
                player
        );


        // ========================================================
        // VIDEO URI
        // ========================================================

        Uri uri =

                Uri.parse(

                        "android.resource://"

                                + getPackageName()

                                + "/"

                                + R.raw.warehouse_intro

                );


        // ========================================================
        // MEDIA ITEM
        // ========================================================

        MediaItem mediaItem =

                MediaItem.fromUri(
                        uri
                );


        player.setMediaItem(
                mediaItem
        );


        // ========================================================
        // PREPARE
        // ========================================================

        player.prepare();


        // ========================================================
        // START VIDEO
        // ========================================================

        player.setPlayWhenReady(
                true
        );


        // ========================================================
        // VIDEO END
        // ========================================================

        player.addListener(

                new Player.Listener() {

                    @Override
                    public void onPlaybackStateChanged(

                            int playbackState

                    ) {

                        if (

                                playbackState ==

                                        Player.STATE_ENDED

                        ) {

                            openHome();

                        }

                    }

                }

        );

    }


    // ============================================================
    // HIDE SYSTEM BARS
    // ============================================================

    private void hideSystemBars() {


        // ========================================================
        // ANDROID 11+
        // ========================================================

        if (

                Build.VERSION.SDK_INT

                        >=

                        Build.VERSION_CODES.R

        ) {

            getWindow().setDecorFitsSystemWindows(
                    false
            );


            View decorView =

                    getWindow()
                            .getDecorView();


            WindowInsetsController controller =

                    decorView
                            .getWindowInsetsController();


            if (controller != null) {


                controller.hide(

                        WindowInsets.Type.statusBars()

                                |

                                WindowInsets.Type.navigationBars()

                );


                controller.setSystemBarsBehavior(

                        WindowInsetsController
                                .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

                );

            }

        }


        // ========================================================
        // OLD ANDROID
        // ========================================================

        else {

            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(

                            View.SYSTEM_UI_FLAG_FULLSCREEN

                                    |

                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

                                    |

                                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

                                    |

                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                                    |

                                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

                                    |

                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE

                    );

        }

    }


    // ============================================================
    // OPEN HOME
    // ============================================================

    private void openHome() {


        // ========================================================
        // IMPORTANT
        //
        // SharedPreferences માં:
        //
        // userId
        // warehouse
        // role
        // mobile
        // subscriptionUserId
        //
        // પહેલેથી save છે.
        //
        // અહીં કશું clear/overwrite કરવાનું નથી.
        // ========================================================


        Intent intent =

                new Intent(

                        LoadingActivity.this,

                        HomeActivity.class

                );


        intent.setFlags(

                Intent.FLAG_ACTIVITY_NEW_TASK

                        |

                        Intent.FLAG_ACTIVITY_CLEAR_TASK

        );


        startActivity(
                intent
        );


        // ========================================================
        // TRANSITION
        // ========================================================

        overridePendingTransition(

                android.R.anim.fade_in,

                android.R.anim.fade_out

        );


        finish();

    }


    // ============================================================
    // ON PAUSE
    // ============================================================

    @Override
    protected void onPause() {

        super.onPause();


        if (player != null) {

            player.pause();

        }

    }


    // ============================================================
    // ON RESUME
    // ============================================================

    @Override
    protected void onResume() {

        super.onResume();


        if (player != null) {

            player.play();

        }

    }


    // ============================================================
    // ON DESTROY
    // ============================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();


        if (player != null) {

            player.release();

            player = null;

        }

    }

}
