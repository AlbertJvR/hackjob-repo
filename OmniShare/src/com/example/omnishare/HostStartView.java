package com.example.omnishare;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HostStartView extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	DBController dbController = new DBController(this);
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	ChordMain chordmain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_start_view);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Show the Up button in the action bar.
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);						
						System.out.println("Current Page " + position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		chordmain = new ChordMain(this.getApplicationContext());
		chordmain.startChord();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host_start_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		
		System.out.println("onTabSelected" + tab.getPosition());
		//update Live filelist
		if(tab.getPosition() == 1) //1 is the id of the filelist fragment
		{
			View rootView = mViewPager.getRootView();	
			Intent intent = getIntent();
			String queryid = intent.getStringExtra("meetingId");
			
			ArrayList<String> fileList = ServerInterface.getCurrentFileList(getApplicationContext());
			if (fileList != null)
			{
				System.out.println("FileList Size " + fileList.size());
			}
			
			//for filelist and onclick functionality	
			System.out.println("CurrentView queryID new " + queryid);
			final ArrayList<HashMap<String, String>> meetingFileList = dbController.getAllMeetingFiles(queryid);
			
			if(fileList.isEmpty())
			{
				Toast toast = Toast.makeText(getApplicationContext(), "No Server Set, Offline mode enabled...", Toast.LENGTH_LONG);
				toast.show();
				for(int i = 0; i < meetingFileList.size(); i++)
				{
					fileList.add(meetingFileList.get(i).get("fileLocation"));
				}
			}
			
			
			if (fileList != null && fileList.size() != 0)
			{
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mViewPager.getChildAt(tab.getPosition()).getContext(),R.layout.activity_fileitem, fileList);
				ListView lv = (ListView) rootView.findViewById(R.id.lv_fragment_filelist);
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,int position, long id)
					{
						TextView fileName = (TextView) view.findViewById(R.id.rowtext);
						String valmeetingId = (fileName.getText() != null ? fileName.getText().toString() : "");
						
						String filePath = meetingFileList.get(position).get("fileLocation");
						String tempFilePathString = filePath.toLowerCase();
						
		                System.out.println("CurrentView Onclick " + filePath);
		                
		                if(tempFilePathString.contains(".pdf"))
		                {
		                	Intent pdfIntent = new Intent(getApplicationContext(), PdfhostviewActivity.class);
			                pdfIntent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, filePath);	
			                startActivity(pdfIntent);		                
		                }else
	                	if(tempFilePathString.contains(".jpg") || tempFilePathString.contains(".jpeg") || tempFilePathString.contains(".bmp") || tempFilePathString.contains(".png"))
		                {
		                	Intent intent = new Intent(getApplicationContext(), DisplayImageActivity.class);
		                	intent.putExtra("filePath", filePath);
		                	startActivity(intent);		                
		                }
	                	else
	                	if(tempFilePathString.contains(".ppt"))
		                {
		                	Intent intent = new Intent(getApplicationContext(), PPTViewActivity.class);
		                	intent.putExtra("filePath", filePath);
		                	startActivity(intent);		                
		                }
	                	else
	                	{
		                	Intent intent = new Intent(getApplicationContext(), DisplayVideoActivity.class);
		                	intent.putExtra("filePath", filePath);
		                	startActivity(intent);		                
		                }						
					}
				});
				lv.setAdapter(arrayAdapter);
			}
		}		
	}
	
	/**
	 * Add files to running network (HOST)
	 */
	public void addFilesHost(View view){
		Intent intent = getIntent();
		String queryid = intent.getStringExtra("meetingId");
		Intent resultIntent = new Intent(getApplicationContext(), AddFilesActivity.class);
		resultIntent.putExtra("meetingId", queryid);
		startActivityForResult(resultIntent, 1);
	}
	
	/**
	 * Returns file list from AddFilesActivity.java
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  System.out.println("REQ CODE " + requestCode + "  RES CODE " + resultCode);
		
		if (requestCode == 1) {

		     if(resultCode == RESULT_OK){      
		    		//fileList = data.getStringArrayListExtra("fileList");	
		    	 String queryid = data.getStringExtra("meetingId");	
		    	 System.out.println("meeting ID in callback " + queryid);
		    	 final ArrayList<HashMap<String, String>> meetingFileList = dbController.getAllMeetingFiles(queryid);
		 		
		    	 if (meetingFileList.size() != 0)
		 		{
		 			ListView lv = (ListView)findViewById(R.id.lv_fragment_filelist);
		 			
		 			lv.setOnItemClickListener(new OnItemClickListener() 
		 			{
		 				@Override //FOR TEST ONLY
						public void onItemClick(AdapterView<?> parent, View view,int position, long id)
						{
							TextView fileName = (TextView) view.findViewById(R.id.fileName);
							String valmeetingId = (fileName.getText() != null ? fileName.getText().toString() : "");
												
							 //TEST by B for pdf viewer activity
							String filePath = meetingFileList.get(position).get("fileLocation");
			                System.out.println("PDF VIEW TEST " + filePath);
			                
			                if(filePath.contains(".pdf"))
			                {
				                Intent pdfIntent = new Intent(getApplicationContext(), PdfhostviewActivity.class);
				                pdfIntent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, filePath);
				                startActivity(pdfIntent);
			                }
							
						}
		 			});
		 			
		 			ListAdapter adapter = new SimpleAdapter(this, meetingFileList, R.layout.activity_fileitemrepresentation, new String[] {"fileId", "fileName" }, new int[] {R.id.fileId, R.id.fileName });
		 			lv.setAdapter(adapter);
		 		}
		    	 
				ArrayList<HashMap<String, String>> tempmeetingFileList = dbController.getAllMeetingFiles(queryid);
				ArrayList<String> actualFileNames = new ArrayList<String>();
		    	
				//to add the 3 column of the db to a list of absolute file paths
				for(int k = 0; k< tempmeetingFileList.size(); k++)
				{
					Collection<String> fileList = tempmeetingFileList.get(k).values();
					Iterator<String> i = fileList.iterator();
					int y = 0;
					while(i.hasNext())
					{
						String temp = i.next();
						if(y == 3) //3rd Attribute from the cursor.
						{
							actualFileNames.add(temp);
						}
						
						System.out.println(y++ +  " FILE IN FILE LIST BEFORE TRANSFER " + temp);
					}
				}
		    	//Files persisted to server here
		    	new SendFilesTask().execute(actualFileNames);
		     }
		     if (resultCode == RESULT_CANCELED) {    		         
		     }
		  }
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

	       //     ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarAddFiles);
	     //       pb.setProgress(values[0]);
	        }

	        @Override
			protected void onPostExecute(String result) {
	            super.onPostExecute(result);
	            System.out.println("Done sending files to server");
	      //      Intent intent = getIntent();
	    //        intent.putStringArrayListExtra("fileList", fileList);
	           // chordmain.sendToAll("File Uploaded", 2);
	        }

	        @Override
	        protected String doInBackground(ArrayList<String>... inputs) {
	        	if(!inputs[0].isEmpty())
	        	{
		            float counter = 100 / inputs[0].size();
		            for (int i = 0; i < inputs[0].size(); i++) {
		                ServerInterface.sendFile(new File(inputs[0].get(i)), getApplicationContext());
		                publishProgress((int) ((i + 1) * counter));
		            }
		            chordmain.sendToAll("File Uploaded", 2);
	        	}else
	        	{
	        		System.out.println("DO IN BG FILELIST WAS EMPTY");
	        	}
	        	
	        	
	            return "Done sending " + inputs[0].size() +" files to server";
	        }
	    }

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			switch(position)
			{			
				case 0:
				{
					Fragment fragment = new DummySectionFragment();
					Bundle args = new Bundle();
					args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
					fragment.setArguments(args);
					return fragment;
				}
				case 1:
				{
					Fragment fragment = new FileListFragment();
					Bundle args = new Bundle();
					args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
					fragment.setArguments(args);
					return fragment;
				}
				case 2:
				{
					Fragment fragment = new DummySectionFragment();
					Bundle args = new Bundle();
					args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
					fragment.setArguments(args);
					return fragment;
				}
				default:
				{
					Fragment fragment = new DummySectionFragment();
					Bundle args = new Bundle();
					args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
					fragment.setArguments(args);
					return fragment;
				}
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.hostview_curr_view_title).toUpperCase(l);
			case 1:
				return getString(R.string.hostview_file_list_title).toUpperCase(l);
			case 2:
				return getString(R.string.hostview_active_disp_device).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_host_start_view_dummy, container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}
	
	public static class FileListFragment extends Fragment
	{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public FileListFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_filelist_hostview,container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			
			ArrayList<String> fileList = ServerInterface.getCurrentFileList(getActivity());
			if (fileList != null)
				System.out.println("FileList Size " + fileList.size());

			if (fileList != null && fileList.size() != 0)
			{
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(FileListFragment.this.getActivity(),R.layout.activity_fileitem, fileList);
				ListView lv = (ListView) rootView.findViewById(R.id.lv_fragment_filelist);
				lv.setAdapter(arrayAdapter);
			}
			
			
			
			return rootView;
		}		
		
	}

}