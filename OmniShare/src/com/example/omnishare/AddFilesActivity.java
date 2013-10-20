package com.example.omnishare;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AddFilesActivity extends ListActivity {

    //for file list 
    private ArrayList<String> item = null;
    private ArrayList<String> path = null;
    private String root;
    private TextView myPath;
    ArrayList<String> fileList = new ArrayList();
    DBController dbController = new DBController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_files);

        //for file List

        myPath = (TextView) findViewById(R.id.path);
        root = Environment.getExternalStorageDirectory().getPath();
        getDir(root);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_files, menu);
        return true;
    }

    private void getDir(String dirPath) {
        myPath.setText("Location: " + dirPath);
        item = new ArrayList<String>();
        path = new ArrayList<String>();
        File f = new File(dirPath);
        File[] files = f.listFiles();

        if (!dirPath.equals(root)) {
            item.add(root);
            path.add(root);
            item.add("../");
            path.add(f.getParent());
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            if (!file.isHidden() && file.canRead()) {
                path.add(file.getPath());
                if (file.isDirectory()) {
                    item.add(file.getName() + "/");
                } else {
                    item.add(file.getName());
                }
            }
        }


        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.activity_fileitem, item);
        setListAdapter(fileList);
    }

    /*
     * (non-Javadoc) This method is called while browsing file structure
     * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(path.get(position));

        if (file.isDirectory()) {
            if (file.canRead()) {
                getDir(path.get(position));
            } else {
                new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("[" + file.getName() + "] folder can't be read!").setPositiveButton("OK", null).show();
            }
        } else {
            new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("[" + file.getName() + "] was added to the meeting.").setPositiveButton("OK", null).show();
            
            /*
             * INSERT selected file to db (this is surprisingly quick)
             */
            Intent intent = getIntent();
            String queryid = intent.getStringExtra("meetingId");
            
            if(queryid != null)
            {
	            if (!fileList.contains(file.getAbsolutePath()));
	            {
	            	HashMap<String, String> queryValues = new HashMap<String, String>();
		            queryValues.put("fileName", file.getName().toString());
		            queryValues.put("fileLocation", file.getAbsolutePath().toString());
		            queryValues.put("fileMeetingRef", queryid.toString());
		            dbController.insertMeetingFile(queryValues);	            	
	            	
	                fileList.add(file.getAbsolutePath());
	                System.out.println("File " + file.getAbsolutePath() + "  added to fileList");
	                
	               
	            }
	            
	           
            }
            else
            {
            	if (!fileList.contains(file.getAbsolutePath()));
 	            {
 	                fileList.add(file.getAbsolutePath());
 	                System.out.println("File " + file.getAbsolutePath() + "  added to fileList");
 	            }
            }  
        }
    }

    public void sendFiles(View view) {
        //new SendFilesTask().execute(fileList);
    	//to store filesnames
    	Intent returnIntent = new Intent();		
    	returnIntent.putStringArrayListExtra("fileList", fileList); 
    	returnIntent.putExtra("meetingId", getIntent().getStringExtra("meetingId"));
    	setResult(RESULT_OK, returnIntent);
        finish();
        
    }

    private class SendFilesTask extends AsyncTask<ArrayList<String>, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("onPreExecute() SendFilesTask");

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            System.out.println("Progress at " + values[0]);

            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarAddFiles);
            pb.setProgress(values[0]);
        }

        @Override
		protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("Done sending files to server");
            Intent intent = getIntent();
            intent.putStringArrayListExtra("fileList", fileList);
            finish();
        }

        @Override
        protected String doInBackground(ArrayList<String>... inputs) {
            float counter = 100 / inputs[0].size();
            for (int i = 0; i < inputs[0].size(); i++) {
                ServerInterface.sendFile(new File(inputs[0].get(i)), getApplicationContext());
                publishProgress((int) ((i + 1) * counter));
            }

            return "Done sending files to server";
        }
    }
}
