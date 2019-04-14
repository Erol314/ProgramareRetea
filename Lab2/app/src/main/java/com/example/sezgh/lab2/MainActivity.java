package com.example.sezgh.lab2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        final Button button = findViewById(R.id.button_id);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

//                ExecutionResults.setText("");

                CThread t1 = new CThread("1");
                CThread t2 = new CThread("2");
                CThread t3 = new CThread("3");
                CThread t4 = new CThread("4");
                CThread t5 = new CThread("5");
                CThread t6 = new CThread("6");

                // Set dependencies
                t1.waitFor(t5);
                t2.waitFor(t5, t6);
                t3.waitFor(t5, t6);
                t4.waitFor(t6);

                // Run
                t1.start();
                t2.start();
                t3.start();
                t4.start();
                t5.start();
                t6.start();


            }
        });
    }


    public class CThread extends Thread{

        
        TextView ExecutionResults = findViewById(R.id.textView);

        // the message we would want to print later on
        private String msg;
        // array of threads that this object should wait (be locked) for
        private CThread[] waitFor = new CThread[0];
        // when this objects thread finishes its work we set this variable to true
        private boolean hasRun = false;

//        private String PreviousText = ExecutionResults.toString();

        // constructor that sets the msg
        CThread(String msg) {
            this.msg = msg;
        }

        // providing an array to this method will indicate which threads to lock on
        void waitFor(CThread... waitFor){
            this.waitFor = new CThread[waitFor.length];
            System.arraycopy(waitFor, 0, this.waitFor, 0, waitFor.length);
        }

        // we lock on to Thread if it was not run yet, else we assume it finished executing
        private void wait(int i){
            synchronized (this.waitFor[i]) {
                try {
                    if (!this.waitFor[i].hasRun)
                        this.waitFor[i].wait();
                } catch (Exception ignored) {}
            }
        }

        // when we start our object, it will run this code in its own thread
        public void run(){


            // lock on each Thread we indicated using waitFor method
            for (int i = 0; i < this.waitFor.length; i++)
                wait(i);

            // once lock is released, we print our msg
//            System.out.println(this.msg);

            ExecutionResults.append(msg);

            // and tell every waiting object that this thread has finished executing
            synchronized (this) {
                this.notify();
                this.hasRun = true;
            }
        }
    }
}