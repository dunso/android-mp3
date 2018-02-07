package Utils;

public class DateUtil {

    /**
     * Format Datetime from long int to String HH;mm:SS
     *
     * @param time: long int time
     * @return: HH;mm:SS
     */
    public static String formatTime(int time) {
        time /= 1000;
        int hour = time / 60 / 60;
        int minute = time / 60 % 60;
        int second = time % 60;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}
