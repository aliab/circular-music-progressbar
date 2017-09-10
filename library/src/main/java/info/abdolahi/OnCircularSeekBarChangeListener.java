package info.abdolahi;

/**
 * A callback that notifies clients when the progress level has been changed.
 * This includes changes that were initiated by the user
 * through a touch gesture or arrow key/trackball as well as changes
 * that were initiated programmatically.
 */
public interface OnCircularSeekBarChangeListener {

    /**
     * Notification that the progress level has changed.
     * Clients can use the fromUser parameter to distinguish user-initiated changes
     * from those that occurred programmatically.
     *
     * @param circularBar : The CircularBar whose progress has changed
     * @param progress    : The current progress level. This will be in the range min.
     * @param fromUser    : True if the progress change was initiated by the user.
     */
    void onProgressChanged(CircularMusicProgressBar circularBar, int progress, boolean fromUser);

    /**
     * User click on circular bar
     *
     * @param circularBar : The CircularBar in which click happen
     */
    void onClick(CircularMusicProgressBar circularBar);

    /**
     * User long click on circular bar
     *
     * @param circularBar : The CircularBar in which long gesture happen
     */
    void onLongPress(CircularMusicProgressBar circularBar);
}
