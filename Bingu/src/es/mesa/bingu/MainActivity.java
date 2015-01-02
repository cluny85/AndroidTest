package es.mesa.bingu;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import es.mesa.bingu.config.Level;

public class MainActivity extends Activity {

	private final String TAG = "MainActivity";
	private final String TAG_VALUE = "value";
	private int current_level;
	
	TextView txt_level;
	
	TextView b1;
	TextView b2;
	TextView b3;
	TextView b4;
	TextView b5;
	TextView b6;
	TextView b7;
	TextView b8;

	TextView puntuacion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		txt_level = (TextView) findViewById(R.id.txtLevel);
		
		b1 = (TextView) findViewById(R.id.b1);
		b2 = (TextView) findViewById(R.id.b2);
		b3 = (TextView) findViewById(R.id.b3);
		b4 = (TextView) findViewById(R.id.b4);
		b5 = (TextView) findViewById(R.id.b5);
		b6 = (TextView) findViewById(R.id.b6);
		b7 = (TextView) findViewById(R.id.b7);
		b8 = (TextView) findViewById(R.id.b8);

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
		
		setBackgroundTexts();
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();		
		init();
		//puntuacion.setText("" + generateRandom(500));
		//new MyThread().run();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * ChoiceTouchListener will handle touch events on draggable views
	 * 
	 */
	private final class MyTouchListener implements OnTouchListener {
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

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

				dropTarget.setText(""
					+ getDiference(dropTarget.getText(),dropped.getText()));
				dropTarget.setTypeface(Typeface.DEFAULT_BOLD);
				
				if (dropTarget.getText().toString().equalsIgnoreCase("0")) {
					victoryCondition();					
				}
				else {
					new MyThread(Level.LEVELS[current_level]).run();
				}
				
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

	private int generateRandom() {
		Random rd = new Random();
		if (rd.nextDouble() > 0.5)
			return new Random().nextInt(current_level);
		return 0 - new Random().nextInt(current_level);
	}

	private int generateRandom(int n) {
		Random rd = new Random();
		if (rd.nextDouble() > 0.5)
			return new Random().nextInt(n);
		return 0 - new Random().nextInt(n);
	}

	private void timerRandom() {
		
	}

	private int getDiference(CharSequence value, CharSequence minus) {
		return Integer.decode("" + value) + Integer.decode("" + minus);
	}

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
			
			b1.setText("" + generateRandom(lvl));
			b2.setText("" + generateRandom(lvl));
			b3.setText("" + generateRandom(lvl));
			b4.setText("" + generateRandom(lvl));
			b5.setText("" + generateRandom(lvl-(lvl/2)));
			b6.setText("" + generateRandom(lvl));
			b7.setText("" + generateRandom(lvl));
			b8.setText("" + 9);
			
			
		}
	}
	private Dialog winnerDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_title_win);
		builder.setMessage(R.string.dialog_content_win);
		builder.setPositiveButton(R.string.dialog_option_ok, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i("winnerDialog", "New game.");
				dialog.cancel();
				nextLevel();
				//puntuacion.setText(""+generateRandom(999));
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
		builder.setMessage("You win the game!!");
		builder.setPositiveButton("OK", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		return builder.create();
	}
	private void init() {
		current_level=10;
		txt_level.setText(Level.LABEL_LEVEL+current_level);
		puntuacion.setText("" + generateRandom(Level.LEVELS[current_level]));
		new MyThread(Level.LEVELS[current_level]).run();
	}
	private void setBackgroundTexts() {
		
//		b1.setBackgroundColor(getResources().getColor(R.color.Orange));
//		b2.setBackgroundColor(getResources().getColor(R.color.Cian));
//		b3.setBackgroundColor(getResources().getColor(R.color.Red));
//		b4.setBackgroundColor(getResources().getColor(R.color.solid_green));
//		b5.setBackgroundColor(getResources().getColor(R.color.solid_green));
//		b6.setBackgroundColor(getResources().getColor(R.color.solid_yellow));
//		b7.setBackgroundColor(getResources().getColor(R.color.solid_red));
//		b8.setBackgroundColor(getResources().getColor(R.color.Cian));		
	}
	private void nextLevel(){
			current_level++;
			txt_level.setText(Level.LABEL_LEVEL + current_level);
			puntuacion.setText("" + generateRandom(Level.LEVELS[current_level]));
			new MyThread(Level.LEVELS[current_level]).run();		
	}
	private void victoryCondition() {
		if (current_level<11) winnerDialog().show();
		else endGame().show();
	}
}