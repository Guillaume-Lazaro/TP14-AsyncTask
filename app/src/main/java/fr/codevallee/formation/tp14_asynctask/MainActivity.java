package fr.codevallee.formation.tp14_asynctask;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button buttonGo;
    private Button buttonStop;
    private TextView etat;

    private final int MAX_PROGRESSION = 100;
    private final int LOADING_STEP = 10;
    private final int TIME_STEP = 300;

    private ProgressAsyncTask progressAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Interface:
        this.buttonGo = (Button) findViewById(R.id.button_go);
        this.buttonStop = (Button) findViewById(R.id.button_stop);
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        this.etat = (TextView) findViewById(R.id.tv_etat);

        //Initialisation:
        this.progressBar.setProgress(0);
        this.etat.setText("Départ");

        //Création du click listener pour lancer la tache
        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(progressAsyncTask==null) {
                    Log.d("Trace", "Ma tache est null !! Du coup je l'instancie");
                    progressAsyncTask = new ProgressAsyncTask();
                    if (progressAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
                        progressAsyncTask.execute();
                    } else {
                        Log.d("Trace", "Nope! Je lance pas la task car elle est dans l'état " + progressAsyncTask.getStatus().toString());
                    }
                } else if(progressAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
                    Log.d("Trace","J'ai atteint la fin! Je recommence du coup");
                    progressAsyncTask = new ProgressAsyncTask();
                    progressAsyncTask.execute();
                } else {
                    Log.d("Trace", "Ma tache existe déjà et elle est pas fini du coup j'en crée pas une nouvelle!");
                }
            }
        });

        //Création du click listener pour stopper la tache:
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(progressAsyncTask.getStatus()==AsyncTask.Status.RUNNING) {
                    Log.d("Trace","Pof! J'annule ma tache!");
                    progressBar.setProgress(0);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etat.setText("Annulé");
                        }
                    });
                    progressAsyncTask.cancel(true);
                } else {
                    Log.d("Trace","Ben en fait je peux pas annuler ma tache car elle est dans l'etat "+progressAsyncTask.getStatus().toString());
                }
            }
        });
    }

    public class ProgressAsyncTask extends AsyncTask<String, Integer, String> {

        private long oldTime=0;

        @Override
        protected String doInBackground(String... params) {
            for(int progression=0 ; progression<=MAX_PROGRESSION ; progression+=LOADING_STEP) {
                try {
                    Thread.sleep(TIME_STEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(progression);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.d("Trace","Lancement de l'asyncTask!");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etat.setText("Chargement");
                }
            });
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            // Progression de la barre
            progressBar.setProgress(values[0]);
            Calendar cal = Calendar.getInstance();

            //Calcul du "lag"
            long lag = (cal.getTimeInMillis()-oldTime)-300;
            Log.d("Trace","progression = "+values[0]+" / "+MAX_PROGRESSION + " le lag est de "+ lag + "ms");
            oldTime = cal.getTimeInMillis();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Trace","Fin de l'asyncTask!");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etat.setText("Fini");
                }
            });
        }

        @Override
        protected void onCancelled() {
            Log.d("Trace","Annulation de l'asyncTask!");
        }
    }
}
