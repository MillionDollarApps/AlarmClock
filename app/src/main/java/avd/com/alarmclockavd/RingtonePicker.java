package avd.com.alarmclockavd;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;


/**
 * The type Ringtone picker.
 */
public class RingtonePicker extends Dialog {

	private MediaPlayer mediaPlayer;
	private View selectedView;
	private String title;
	private Context context;
	private ArrayList<String> musicList;
	private ArrayList<String> ringtoneList;
	private ImageView confirmButton;
	private TextView ringtoneTitle;

	/**
	 * Instantiates a new Ringtone picker.
	 *
	 * @param context the context
	 * @param ringtoneTitle the ringtone title
	 */
	public RingtonePicker(Context context, TextView ringtoneTitle) {
		super(context);
		this.context = context;
		this.title = ringtoneTitle.getText().toString();
		this.ringtoneTitle = ringtoneTitle;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//sets the layout
		initializeLayout();
		//initialize musicList and ringtoneList
		musicList = new ArrayList<>(getMusic().keySet());
		ringtoneList = new ArrayList<>(getRingtones().keySet());
		//initialize MediaPlayer
		mediaPlayer = new MediaPlayer();
		//intializeViews
		initializeViews();
	}

	//sets up the layout of the dialog
	private void initializeLayout() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_choose_ringtone);
		setCancelable(true);
		setCanceledOnTouchOutside(true);
	}

	//initializes the views the Dialog will use
	private void initializeViews() {
		//initializing confirm/cancel buttons
		confirmButton = (ImageView) findViewById(R.id.confirmButton);
		ImageView cancelButton = (ImageView) findViewById(R.id.cancelButton);
		//initializing the listviews;
		ListView ringtoneListView = (ListView) findViewById(R.id.ringtoneListView);
		ListView musicListView = (ListView) findViewById(R.id.musicListView);
		//setting up the adapters for the listviews
		musicListView.setAdapter(getAdapter(musicList));
		ringtoneListView.setAdapter(getAdapter(ringtoneList));
		//setting onItemClickListeners for the listviews
		listViewItemClickListener(ringtoneListView, ringtoneList);
		listViewItemClickListener(musicListView, musicList);
		//setting onClickListeners
		buttonClickListener(confirmButton);
		buttonClickListener(cancelButton);
	}

	//..
	private void buttonClickListener(final ImageView button) {
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopMediaPlayer(mediaPlayer);
				dismiss();
				if (button.equals(confirmButton)) {
					ringtoneTitle.setText(title);
				}
			}
		});
	}

	//stops the media player
	private void stopMediaPlayer(MediaPlayer mp) {
		if (mp.isPlaying()) {
			mp.stop();
		}
		mp.release();
	}

	//the name nails it
	private void listViewItemClickListener(final ListView listview, final ArrayList<String> list) {
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//setting up the selection process for the listviews
				setSelector(view);
				//setting up the title to be displayed
				title = list.get(position);
				//starting up media player to play the selected item
				playMediaPlayer(mediaPlayer);
			}
		});
	}

	//selector method for 2 lists
	private void setSelector(View view) {
		if (selectedView != null) {
			selectedView.setSelected(false);
			selectedView.setBackgroundResource(R.mipmap.back);
			selectedView.setActivated(false);
		}
		selectedView = view;
	}

	//sets up media player
	private void playMediaPlayer(MediaPlayer mp) {
		mp.reset();
		try {
			mp.setDataSource(context, Uri.parse(getUri()));
			mp.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mp.setLooping(false);
		mp.start();
	}

	/**
	 * Gets uri.
	 *
	 * @return the uri
	 */
//retrieves the Uri asociated with the title
	public String getUri() {
		String uriPath = getRingtones().get(title);
		if (uriPath == null) {
			uriPath = getMusic().get(title);
		}
		return uriPath;
	}

	// generates the array adapter for a list
	private ArrayAdapter<String> getAdapter(ArrayList<String> list) {
		return new ArrayAdapter<>(context, R.layout.choose_ringtone_row, R.id.rowTextView,
				list);
	}

	//retrieves a LinkedHashMap having the music title- uriPath as key-value
	private LinkedHashMap<String, String> getRingtones() {
		LinkedHashMap<String, String> ringtone = new LinkedHashMap<>();
		RingtoneManager manager = new RingtoneManager(context);
		Cursor cursor = manager.getCursor();
		while (cursor.moveToNext()) {
			ringtone.put(cursor.getString(1), cursor.getString(2) + "/" + cursor.getString(0));
		}
		cursor.close();
		return ringtone;
	}

	//retrieves a LinkedHashMap having the music title- uriPath as key-value
	private LinkedHashMap<String, String> getMusic() {
		LinkedHashMap<String, String> music = new LinkedHashMap<>();
		ContentResolver musicResolver = context.getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor cursor = musicResolver.query(musicUri, null, null, null, "title ASC");
		if (cursor != null && cursor.moveToFirst()) {
			int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
			do {
				music.put(cursor.getString(titleColumn), musicUri + "/" + cursor.getString(0));
			}
			while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return music;

	}

}
