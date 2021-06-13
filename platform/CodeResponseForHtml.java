package platform;

public class CodeResponseForHtml extends CodeResponse {

    private int timeRest;
    private int viewRest;

    public CodeResponseForHtml(String code, String date, long time, long views, int timeRest, int viewRest) {
        super(code, date, time, views);
        this.timeRest = timeRest;
        this.viewRest = viewRest;
    }

    public int getTimeRest() {
        return timeRest;
    }

    public void setTimeRest(int timeRest) {
        this.timeRest = timeRest;
    }

    public int getViewRest() {
        return viewRest;
    }

    public void setViewRest(int viewRest) {
        this.viewRest = viewRest;
    }
}
