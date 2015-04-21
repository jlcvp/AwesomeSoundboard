package com.leleusoft.awesomesoundboard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

public class MainActivity extends ActionBarActivity {

	public static final String TAG = "DEBUG";
	SoundPool soundController;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		soundController = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.ic_launcher)
			        .setContentTitle("My notification")
			        .setContentText("Hello World!")
			        .setDefaults(NotificationCompat.DEFAULT_ALL);
					
					NotificationManagerCompat mNotificationManager= 
											     NotificationManagerCompat.from(getApplicationContext());
					
					Notification notification=mBuilder.build();
					
					
					mNotificationManager.notify(5002, notification);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		AdapterSoundGrid adapter;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);

			ArrayList<GridItem> array = getSoundArray();
			Log.i(TAG, "arraysize = "+array.size());

			GridView gv = (GridView)rootView.findViewById(R.id.gridView1);
			adapter = new AdapterSoundGrid(array, getActivity());
			gv.setAdapter(adapter);
			
			gv.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					
					try {
						AssetManager am = getActivity().getAssets();
						Log.d(TAG,"am = "+am.toString());
						
						InputStream is = am.open(adapter.getItemPath(position));
						
						setRingtone(is, 0);
						
						Toast.makeText(getActivity(), 
								"Notification set: "+adapter.getItemPath(position),
								Toast.LENGTH_LONG).show();
						
					} catch (IOException e) {
						Toast.makeText(getActivity(), 
								"Error setting: "+adapter.getItemPath(position)+"as notification tone",
								Toast.LENGTH_LONG).show();
					}
					
					return true;
				}
			});
			
			gv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					soundController.play((int) id, 1.0f, 1.0f, 1 , 0, 1.0f);
				}
			});

			for(GridItem item:array)
			{
				AssetFileDescriptor afd=null;
				try {
					afd = getActivity().getAssets().openFd("mario_soundboard/"+	item.getSoundUri());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(afd !=null){
					long ide = soundController.load(afd, 1);
					item.id=ide;
				}
			}

			return rootView;


		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);



		}


	}

	public ArrayList<GridItem> getSoundArray() {

		ArrayList<GridItem> mArray = new ArrayList<GridItem>();
		String list[] = {""};

		try {
			list = getAssets().list("mario_soundboard");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//		smb3_sound_effects_


		for(int i=0; i<list.length;i++)
		{
			String name = list[i].substring(19);
			name = name.substring(0, name.lastIndexOf('.'));
			GridItem item = new GridItem(list[i],name);
			mArray.add(item);
		}

		return mArray;
	}
	
	private void setRingtone(InputStream fis, int type)
	{
		
		File k = copyFromAssets(fis);

		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
		values.put(MediaStore.MediaColumns.TITLE, k.getName());
		values.put(MediaStore.MediaColumns.SIZE, k.length());
		values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/wav");
		values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
		values.put(MediaStore.Audio.Media.IS_ALARM, false);
		values.put(MediaStore.Audio.Media.IS_MUSIC, false);

		//Insert it into the database
		Uri uri = MediaStore.Audio.Media.getContentUriForPath(k.getAbsolutePath());
		String[] proj  = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.TITLE};
		Cursor c = getContentResolver().query(uri, proj , ""+MediaStore.MediaColumns.DATA+" like '"+k.getAbsolutePath()+"'",null, null);
		
		Uri newUri = null;
		if(c!=null && c.getCount()>0) //já está no banco
		{
			this.getContentResolver().delete(uri, ""+MediaStore.MediaColumns.DATA+" like '"+k.getAbsolutePath()+"'", null);
			
		}		
			newUri = this.getContentResolver().insert(uri, values);
		
		
		RingtoneManager.setActualDefaultRingtoneUri(
		  this,
		  RingtoneManager.TYPE_NOTIFICATION,
		  newUri
		);
	}

	private File copyFromAssets(InputStream fis){
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/temp.wav");
		FileOutputStream fos = null;
		
		try {
			
			
			fos = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];
	 
			while ((read = fis.read(bytes)) != -1) {
				fos.write(bytes, 0, read);
			}
			
			if(fis!=null)
				fis.close();
			if(fos!=null)
				fos.close();
			
		} catch (IOException e) {			
			e.printStackTrace();
		}
				
		return file;
	}



}
