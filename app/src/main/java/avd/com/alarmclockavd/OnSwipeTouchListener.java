package avd.com.alarmclockavd;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeTouchListener implements OnTouchListener {

	private final GestureDetector mGestureDetector;

	public OnSwipeTouchListener (Context ctx) {
		mGestureDetector = new GestureDetector (ctx, new GestureListener ());
	}


	public GestureDetector getmGestureDetector () {
		return mGestureDetector;
	}


	@Override
	public boolean onTouch (View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent (event);
	}

	public void onSwipeRight () {
	}

	public void onSwipeLeft () {
	}

	public void onClick () {
	}

	public void onSwipeBottom () {
	}

	private final class GestureListener implements GestureDetector.OnGestureListener {

		private static final int SWIPE_THRESHOLD = 80;
		private static final int SWIPE_VELOCITY_THRESHOLD = 70;

		@Override
		public boolean onDown (MotionEvent e) {
			return true;
		}

		@Override
		public void onShowPress (MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp (MotionEvent e) {
			onClick ();
			return true;
		}

		@Override
		public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress (MotionEvent e) {

		}

		@Override
		public boolean onFling (MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			boolean result = false;
			float diffY = e2.getY () - e1.getY ();
			float diffX = e2.getX () - e1.getX ();
			if (Math.abs (diffX) > SWIPE_THRESHOLD && Math.abs (velocityX) > SWIPE_VELOCITY_THRESHOLD) {
				if (diffX > 0) {
					onSwipeRight ();
				} else {
					onSwipeLeft ();
				}
				result = true;
			}
			return result;

		}
	}
}
