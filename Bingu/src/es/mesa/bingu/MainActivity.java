package es.mesa.bingu;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;
import es.mesa.bingu.config.Level;

public class MainActivity extends Activity {

	private final String TAG = "MainActivity";
	private final String TAG_VALUE = "value";
	private final int NUM_OF_CIRCLES=8;
	private int current_level = 1;
	private long current_time;
	private boolean running=false;
	
	TextView txt_level;
	Chronometer chrono;
	
	TextView b1,b2,b3,b4,b5,b6,b7,b8,puntuacion;
	
	Object[] mapCircles;
	int[] mapValues={0,0,0,0,0,0,0,0};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);		
		txt_level = (TextView) findViewById(R.id.txtLevel);
		chrono = (Chronometer) findViewById(R.id.chronometer);
		
		b1 = (TextView) findViewById(R.id.b1);
		b2 = (TextView) findViewById(R.id.b2);
		b3 = (TextView) findViewById(R.id.b3);
		b4 = (TextView) findViewById(R.id.b4);
		b5 = (TextView) findViewById(R.id.b5);
		b6 = (TextView) findViewById(R.id.b6);
		b7 = (TextView) findViewById(R.id.b7);
		b8 = (TextView) findViewById(R.id.b8);
		Object[] mapCircles={b1,b2,b3,b4,b5,b6,b7,b8};
		this.mapCircles=mapCircles;
		
		puntuacion = (TextView) findViewById(R.id.txt_puntuacion);

		b1.setOnTouchListener(new MyTouchListener());
		b2.setOnTouchListener(new MyTouchListener());
		b3.setOnTouchListener(new MyTouchListener());
		b4.setOnTouchListener(new MyTouchListener());
		b5.setOnTouchListener(new MyTouchListener());
		b6.setOnTouchListener(new MyTouchListener());
		b7.setOnTouchListener(new MyTouchListener());
		b8.setOnTouchListener(new MyTouchListener());

		puntuacion.setOnDragListener(new MyDragListener());
		puntuacion.setText("Time: ");
		
		loadLevelConfig();
	}

	private void loadLevelConfig() {
		setBackgroundTexts(
				R.drawable.circle_shape_1,R.drawable.circle_shape_2,
				R.drawable.circle_shape_3,R.drawable.circle_shape_4,
				R.drawable.circle_shape_5,R.drawable.circle_shape_6,
				R.drawable.circle_shape_7,R.drawable.circle_shape_8);		
	}

	@Override
	protected void onStart() {
		super.onStart();		
		init();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

	/**
	 * ChoiceTouchListener will handle touch events on draggable views
	 * 
	 */
	private final class MyTouchListener implements OnTouchListener {
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				if (!running) {
					running=true;
					current_time=SystemClock.elapsedRealtime();
					chrono.setBase(current_time);
					chrono.start();
				}
				//Integer.decode(("" + ((TextView) view).getText()));
				/*
				 * Drag details: we only need default behavior - clip data could
				 * be set to pass data as part of drag - shadow can be tailored
				 */
				//ClipData data = ClipData.newPlainText(TAG_VALUE,((TextView) view).getText());
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
						view);
				// start dragging the item touched
				view.startDrag(null, shadowBuilder, view, 0);
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * DragListener will handle dragged views being dropped on the drop area -
	 * only the drop action will have processing added to it as we are not -
	 * amending the default behavior for other parts of the drag process
	 * 
	 */
	private class MyDragListener implements OnDragListener {
		Drawable enterShape = getResources().getDrawable(R.drawable.circle_shape_drop);
	    Drawable normalShape = getResources().getDrawable(R.drawable.circle_shape);
	    
		@Override
		public boolean onDrag(View v, DragEvent event) {
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				// no action necessary
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				v.setBackgroundDrawable(enterShape);
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				v.setBackgroundDrawable(normalShape);
				break;
			case DragEvent.ACTION_DROP:
				View view = (View) event.getLocalState();
				TextView dropTarget = (TextView) v;
				TextView dropped = (TextView) view;
				
				int result=getDiference(dropTarget.getText(),dropped.getText());
				if (result==0) {
					dropTarget.setText(""+ result);
					dropTarget.setTypeface(Typeface.DEFAULT_BOLD);
					
					running=false;
					chrono.stop();
					current_time=SystemClock.elapsedRealtime()-current_time;
					victoryCondition();					
				}
				else gameWork(dropTarget,dropped,result);
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				v.setBackgroundDrawable(normalShape);
				break;
			default:
				break;
			}
			return true;
		}
	}
	
	public void gameWork(TextView dropTarget, TextView dropped, int result) {
		if (current_level<6) {
			if (result>100)	dropTarget.setText(""+ (result-100));
			else if (result<-100) dropTarget.setText(""+ (result+100));
			else dropTarget.setText(""+ result);
			dropped.setText("" + firstLevelRandom(Level.LEVELS[current_level]));
		}else if(current_level>5&&current_level<10){
			dropTarget.setText(""+ result);
			new MyThread(Level.LEVELS[current_level]).run();			
		} else{
			dropTarget.setText(""+ result);
			new MyThread(Level.LEVELS[current_level]).run();
		}
	}
	/*
	 * Funciones a futuro
	 */	
	private void rotateCirclesRight(){
		rotateRightArray(mapValues);
		updateMapValues();
	}
	private int[] rotateRightArray(int[] arr){
		//shift to the right
		Integer last = (Integer)arr[arr.length-1];
		System.arraycopy(arr, 0, arr, 1, arr.length-1 );
		arr[0] = last;
		return arr;
	}
	private void updateMapValues(){
		for (int i = 0; i < mapCircles.length; i++) 
			((TextView)mapCircles[i]).setText(""+mapValues[i]);
	}
	private void changeValue(TextView tv,int value){
		tv.setText(""+value);
	}
	private void changeImproveValue(TextView tv,int value){
		for (int i = 0; i < mapCircles.length; i++) {
			if (tv.equals(mapCircles[i])) {
				tv.setText(""+value);
				break;
			}
		}
	}
	private void changeMapValue(TextView tv,int value){
		for (int i = 0; i < mapCircles.length; i++) {
			if (tv.equals(mapCircles[i])) {
				mapValues[i]=value;
				tv.setText(""+value);
				break;
			}
		}
	}
	private void setCirclesVisibility(int...state){
		if (state.length==NUM_OF_CIRCLES) {
			for (int i = 0; i < mapCircles.length; i++) 
				((TextView)mapCircles[i]).setVisibility(state[i]);
		}else{
			//default
			Log.e(TAG, "Error. Lenght of the function expected different than 8");
			for (int i = 0; i < mapCircles.length; i++) 
				((TextView)mapCircles[i]).setVisibility(View.VISIBLE);
		}
	}
	private int[] makeArray(int... res){
		return res;
	}
	/*
	 * Fin funciones a futuro
	 */
	private int firstLevelRandom(int level){
		Random rd = new Random();
		int nd = rd.nextInt(6);
		if (nd == 1 || nd == 3 || nd == 5 ){
			int nxt = rd.nextInt(level);
			return (nxt==0?nxt+1:nxt);
		}
		int nxt = rd.nextInt(level);
		return 0 - (nxt==0?nxt+1:nxt);		
	}
	private int getDiference(CharSequence value, CharSequence minus) {
		return Integer.decode("" + value) + Integer.decode("" + minus);
	}
	/*
	 * Main functions
	 */
	private void init() {
		Log.d(TAG, "Init...");
		txt_level.setText(Level.LABEL_LEVEL+current_level);
		puntuacion.setText("" + firstLevelRandom(Level.LEVELS[current_level]));
		new MyThread(Level.LEVELS[current_level]).run();
	}
	private void nextLevel(){
		current_level++;
		txt_level.setText(Level.LABEL_LEVEL + current_level);
		chrono.setBase(SystemClock.elapsedRealtime());
		puntuacion.setText("" + firstLevelRandom(Level.LEVELS[current_level]));
		new MyThread(Level.LEVELS[current_level]).run();		
}
	private void victoryCondition() {
		if (current_level<11) winnerDialog().show();
		else endGame().show();
	}
	private Dialog winnerDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_title_win);
		builder.setMessage((current_time/1000)+" seg");
		builder.setPositiveButton(R.string.dialog_option_ok, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i("winnerDialog", "New game.");
				dialog.cancel();
				nextLevel();
				//puntuacion.setText(""+firstLevelRandom(999));
			}
		});
		builder.setNegativeButton(R.string.dialog_option_no, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i("winnerDialog", "Exit.");
				//finish();
				System.exit(1);
				dialog.cancel();
			}
		});
		return builder.create();
	}
	private Dialog endGame() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_title_win);
		builder.setMessage(R.string.dialog_message_win);
		builder.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		return builder.create();
	}
	/*
	 * End Main functions
	 */
	private void setBackgroundTexts(int... resource) {
		if (resource.length==NUM_OF_CIRCLES) {
			for (int i = 0; i < resource.length; i++) {
				((TextView)mapCircles[i]).setBackgroundDrawable(getResources().getDrawable(resource[i]));
			}
		}else{
//			b1.setBackgroundColor(getResources().getColor(R.color.Orange));
//			b2.setBackgroundColor(getResources().getColor(R.color.Cian));
//			b3.setBackgroundColor(getResources().getColor(R.color.Red));
//			b4.setBackgroundColor(getResources().getColor(R.color.solid_green));
//			b5.setBackgroundColor(getResources().getColor(R.color.solid_green));
//			b6.setBackgroundColor(getResources().getColor(R.color.solid_yellow));
//			b7.setBackgroundColor(getResources().getColor(R.color.solid_red));
//			b8.setBackgroundColor(getResources().getColor(R.color.Cian));		
			
		}		
	}
	
	
	/**
	 * Level Configuration class
	 * @author OMA
	 *
	 */
	private class MyThread extends Thread {
		private int lvl;
				
		public MyThread(int lvl) {
			super();
			this.lvl = lvl;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			int value = Integer.decode(puntuacion.getText().toString());
			levels(lvl);			
		}
		
		private void levels(int level){
			Log.d(TAG, " ##### level: "+level);
			if (current_level<10) {
				if (current_level==1) {
					//0->VISIBLE, 8->GONE
					setCirclesVisibility(0,8,0,8,8,0,8,0);
					mapValues=makeArray(firstLevelRandom(level),0,firstLevelRandom(level),0,0,firstLevelRandom(level),0,2);
					updateMapValues();
				} else if (current_level==2) {
					setCirclesVisibility(0,0,0,8,8,0,8,0);
					mapValues=makeArray(firstLevelRandom(level),firstLevelRandom(level),firstLevelRandom(level),0,0,firstLevelRandom(level),0,6);
					updateMapValues();
				} else if (current_level==3) {
					setCirclesVisibility(0,0,0,8,8,0,0,0);
					mapValues=makeArray(firstLevelRandom(level),firstLevelRandom(level),firstLevelRandom(level),0,0,firstLevelRandom(level),firstLevelRandom(level),8);
					updateMapValues();
				}else if (current_level==4) {
					setCirclesVisibility(0,8,0,0,0,0,8,0);
					mapValues=makeArray(firstLevelRandom(level),0,firstLevelRandom(level),firstLevelRandom(level),firstLevelRandom(level),firstLevelRandom(level),0,15);
					updateMapValues();
				}else if (current_level==5) {
					setCirclesVisibility(0,8,0,0,0,0,0,0);
					mapValues=makeArray(firstLevelRandom(level),0,firstLevelRandom(level),firstLevelRandom(level),firstLevelRandom(level),firstLevelRandom(level),firstLevelRandom(level),18);
					updateMapValues();
				}else if (current_level==6) {
					setCirclesVisibility(0,0,0,0,0,0,0,0);
					mapValues=makeArray(firstLevelRandom(level-(level/2)),firstLevelRandom(level),firstLevelRandom(level),firstLevelRandom(level),firstLevelRandom(level-(level/2)),firstLevelRandom(level),firstLevelRandom(level-(level/2)),5);
					updateMapValues();
				}else if (current_level==7) {
					setCirclesVisibility(0,8,0,0,0,0,8,0);
					mapValues=makeArray(firstLevelRandom(level),0,firstLevelRandom(level),firstLevelRandom(level),firstLevelRandom(level-(level/2)),firstLevelRandom(level),0,9);
					updateMapValues();
				}else if (current_level==8) {
					setCirclesVisibility(0,8,0,0,0,0,0,0);
					mapValues=makeArray(firstLevelRandom(level*2),0,firstLevelRandom(level-40),firstLevelRandom(level),firstLevelRandom(level-(level/2)),-firstLevelRandom(level),firstLevelRandom(level),9);
					updateMapValues();
				}else if (current_level==9) {
					setCirclesVisibility(0,8,0,0,0,0,0,0);
					mapValues=makeArray(firstLevelRandom(level-60),0,firstLevelRandom(level-45),-firstLevelRandom(level),firstLevelRandom(level-(level/2)),9,firstLevelRandom(level),firstLevelRandom(level*2));
					updateMapValues();
				} else {
					b1.setVisibility(android.view.View.VISIBLE);
					b2.setVisibility(android.view.View.VISIBLE);
					b3.setVisibility(android.view.View.VISIBLE);
					b4.setVisibility(android.view.View.VISIBLE);
					b5.setVisibility(android.view.View.VISIBLE);
					b6.setVisibility(android.view.View.VISIBLE);
					b7.setVisibility(android.view.View.VISIBLE);
					b8.setVisibility(android.view.View.VISIBLE);
					
					b1.setText("" + firstLevelRandom(level));
					b2.setText("" + firstLevelRandom(level));
					b3.setText("" + firstLevelRandom(level));
					b4.setText("" + firstLevelRandom(level));
					b5.setText("" + firstLevelRandom(level-(level/2)));
					b6.setText("" + firstLevelRandom(level));
					b7.setText("" + firstLevelRandom(level));
					b8.setText("" + 9);
				}
			} else {
				//level >100
				setBackgroundTexts(
						R.drawable.circle_shape_3,R.drawable.circle_shape_8,
						R.drawable.circle_shape_5,R.drawable.circle_shape_2,
						R.drawable.circle_shape_7,R.drawable.circle_shape_4,
						R.drawable.circle_shape_1,R.drawable.circle_shape_6);
				b1.setVisibility(android.view.View.VISIBLE);
				b2.setVisibility(android.view.View.VISIBLE);
				b3.setVisibility(android.view.View.VISIBLE);
				b4.setVisibility(android.view.View.VISIBLE);
				b5.setVisibility(android.view.View.VISIBLE);
				b6.setVisibility(android.view.View.VISIBLE);
				b7.setVisibility(android.view.View.VISIBLE);
				b8.setVisibility(android.view.View.VISIBLE);
				
				b1.setText("" + firstLevelRandom(level));
				b2.setText("" + firstLevelRandom(level));
				b3.setText("" + firstLevelRandom(level));
				b4.setText("" + firstLevelRandom(level));
				b5.setText("" + firstLevelRandom(level-(level/2)));
				b6.setText("" + firstLevelRandom(level));
				b7.setText("" + firstLevelRandom(level));
				b8.setText("" + 9);
			}			
		}
	}
}