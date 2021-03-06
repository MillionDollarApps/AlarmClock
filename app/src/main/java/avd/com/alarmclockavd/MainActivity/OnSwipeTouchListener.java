package avd.com.alarmclockavd.MainActivity;

import android.content.Context;
import android.view.*;
import android.view.View.OnTouchListener;

/**
 * The type On swipe touch listener.
 */
public class OnSwipeTouchListener implements OnTouchListener {

	private final GestureDetector mGestureDetector;

	/**
	 * Instantiates a new On swipe touch listener.
	 *
	 * @param context the context
	 */
	public OnSwipeTouchListener(Context context) {
		mGestureDetector = new GestureDetector(context, new GestureListener());

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}


	/**
	 * On swipe left.
	 */
	public void onSwipeLeft() {
	}

	/**
	 * On swipe right.
	 */
	public void onSwipeRight() {
	}

	/**
	 * On click.
	 */
	public void onClick() {
	}


	private final class GestureListener implements GestureDetector.OnGestureListener {

		private static final int SWIPE_THRESHOLD = 80;
		private static final int SWIPE_VELOCITY_THRESHOLD = 70;

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			onClick();
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			boolean result = false;
			float diffX = e2.getX() - e1.getX();
			if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) >
					SWIPE_VELOCITY_THRESHOLD) {
				if (diffX > 0) {
					onSwipeRight();
				} else {
					onSwipeLeft();
				}
				result = true;
			}
			return result;

		}
	}
}
